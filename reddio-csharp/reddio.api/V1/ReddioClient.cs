using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Reddio.Api.V1.Rest;
using Reddio.Crypto;

namespace Reddio.Api.V1
{
    public class ReddioClient : IReddioClient
    {
        private IReddioRestClient _restClient;

        public ReddioClient(IReddioRestClient restClient)
        {
            _restClient = restClient;
        }

        public async Task<ResponseWrapper<TransferResponse>> Transfer(
            string starkKey,
            string privateKey,
            string amount,
            string contractAddress,
            string tokenId,
            string type,
            string receiver,
            long expirationTimeStamp = 4194303
        )
        {
            var assetId = await GetAssetId(contractAddress, tokenId, type);
            var (senderVaultId, receiverVaultId) = await GetVaultIds(assetId, starkKey, receiver);

            var getNonceResponse = await _restClient.GetNonce(new GetNonceMessage(starkKey));
            var nonce = getNonceResponse.Data.Nonce;

            var contractInfo = await _restClient.GetContractInfo(new GetContractInfoMessage(type, contractAddress));
            var resolvedAmount =
                Convert.ToInt64(
                    Double.Parse(amount) * Math.Pow(10, Double.Parse(contractInfo.Data.Decimals)) /
                    contractInfo.Data.Quantum
                ).ToString();
            var signature = SignTransferMessage(privateKey,
                resolvedAmount,
                nonce,
                senderVaultId,
                assetId,
                receiverVaultId,
                receiver,
                expirationTimeStamp);

            var transferMessage = new TransferMessage(
                assetId,
                starkKey,
                resolvedAmount,
                nonce,
                senderVaultId,
                receiver,
                receiverVaultId,
                expirationTimeStamp,
                signature
            );
            return await _restClient.Transfer(transferMessage);
        }

        public async Task<ResponseWrapper<GetRecordResponse>> GetRecord(string starkKey, long sequenceId)
        {
            return await this._restClient.GetRecord(new GetRecordMessage(starkKey, sequenceId));
        }


        public async Task<ResponseWrapper<GetRecordsResponse>> GetRecords(string starkKey, long? limit = null,
            long? page = null, string? contractAddress = null)
        {
            return await _restClient.GetRecords(new GetRecordsMessage(
                    starkKey,
                    limit?.ToString(),
                    page?.ToString(),
                    contractAddress
                )
            );
        }

        public async Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetAccepted(string starkKey,
            long sequenceId)
        {
            var interval = TimeSpan.FromSeconds(5);
            var timeout = TimeSpan.FromMinutes(1);
            CancellationTokenSource source = new CancellationTokenSource(timeout);
            return await WaitingTransferGetAccepted(starkKey, sequenceId, interval, source.Token);
        }

        public async Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetAccepted(string starkKey,
            long sequenceId,
            TimeSpan interval,
            CancellationToken cancellationToken)
        {
            var getRecordMessage = new GetRecordMessage(starkKey, sequenceId);
            for (;;)
            {
                cancellationToken.ThrowIfCancellationRequested();
                var response = await _restClient.GetRecord(getRecordMessage);
                if (SequenceRecord.SequenceStatusAccepted == response.Data[0].Status)
                {
                    return response;
                }

                if (SequenceRecord.SequenceStatusFailed == response.Data[0].Status)
                {
                    throw new TransferFailedException("Transfer failed", response.Data);
                }

                await Task.Delay(interval, cancellationToken);
            }
        }

        public async Task<ResponseWrapper<GetBalanceResponse>> GetBalance(string starkKey, string assetId)
        {
            return await _restClient.GetBalance(new GetBalanceMessage(starkKey, assetId));
        }

        public async Task<ResponseWrapper<GetBalancesResponse>> GetBalances(string starkKey)
        {
            return await _restClient.GetBalances(new GetBalancesMessage(starkKey));
        }

        public async Task<ResponseWrapper<OrderResponse>> Order(string privateKey, string starkKey, string price,
            string amount, string tokenAddress, string tokenId,
            string marketplaceUuid, string tokenType, OrderType orderType)
        {
            var orderInfo = await _restClient.OrderInfo(
                new OrderInfoMessage(starkKey, "ETH:ETH", $"{tokenType}:{tokenAddress}:{tokenId}"));

            if (orderInfo.Status != "OK")
            {
                throw new Exception($"get order info, status is {orderInfo.Status}, error is {orderInfo.Error}");
            }

            var vaultIds = orderInfo.Data.VaultIds;
            var quoteToken = orderInfo.Data.AssetIds[1];

            var amountBuy = Convert.ToInt64(
                Double.Parse(price) * Double.Parse(amount) * Math.Pow(10, 6)
            ).ToString();

            // hard coded format ETH on layer2 (price * (10 **decimals) / quantum)
            var formatPrice = Convert.ToInt64(
                Double.Parse(price) * Math.Pow(10, 6)
            ).ToString();
            var orderMessage = new OrderMessage();
            orderMessage.Amount = amount;
            orderMessage.BaseToken = orderInfo.Data.BaseToken;
            orderMessage.QuoteToken = quoteToken;
            orderMessage.Price = formatPrice;
            orderMessage.StarkKey = starkKey;
            orderMessage.ExpirationTimestamp = 4194303;
            orderMessage.Nonce = orderInfo.Data.Nonce;
            orderMessage.FeeInfo = new FeeInfo(
                Convert.ToInt64(Double.Parse(orderInfo.Data.FeeRate) * Double.Parse(amountBuy)),
                orderInfo.Data.FeeToken,
                Convert.ToInt64(vaultIds[0])
            );

            if (orderType == OrderType.BUY)
            {
                orderMessage.Direction = OrderMessage.DIRECTION_BID;
                orderMessage.TokenSell = orderInfo.Data.BaseToken;
                orderMessage.TokenBuy = quoteToken;
                orderMessage.AmountSell = amountBuy;
                orderMessage.AmountBuy = amount;
                orderMessage.VaultIdBuy = vaultIds[1];
                orderMessage.VaultIdSell = vaultIds[0];
            }
            else
            {
                orderMessage.Direction = OrderMessage.DIRECTION_ASK;
                orderMessage.TokenSell = quoteToken;
                orderMessage.TokenBuy = orderInfo.Data.BaseToken;
                orderMessage.AmountSell = amount;
                orderMessage.AmountBuy = amountBuy;
                orderMessage.VaultIdBuy = vaultIds[0];
                orderMessage.VaultIdSell = vaultIds[1];
            }

            orderMessage.Signature = SignOrderMsgWithFee(
                privateKey,
                orderMessage.VaultIdSell,
                orderMessage.VaultIdBuy,
                orderMessage.AmountSell,
                orderMessage.AmountBuy,
                orderMessage.TokenSell,
                orderMessage.TokenBuy,
                orderMessage.Nonce,
                orderMessage.ExpirationTimestamp,
                orderMessage.FeeInfo.TokenId,
                orderMessage.FeeInfo.SourceVaultId,
                orderMessage.FeeInfo.FeeLimit
            );
            return await _restClient.Order(orderMessage);
        }

        public async Task<ResponseWrapper<OrderResponse>> Order(
            string privateKey,
            string starkKey,
            string contractType,
            string contractAddress,
            string tokenId,
            string price,
            string amount,
            OrderType orderType,
            string baseTokenType = "ETH",
            string baseTokenContract = "eth",
            string marketplaceUuid = "")
        {
            var orderInfo = await _restClient.OrderInfo(
                new OrderInfoMessage(starkKey, $"{baseTokenType}:{baseTokenContract}",
                    $"{contractType}:{contractAddress}:{tokenId}"));

            if (orderInfo.Status != "OK")
            {
                throw new Exception($"get order info, status is {orderInfo.Status}, error is {orderInfo.Error}");
            }

            var vaultIds = orderInfo.Data.VaultIds;
            var quoteToken = orderInfo.Data.AssetIds[1];
            var baseTokenContractInfo =
                await _restClient.GetContractInfo(new GetContractInfoMessage(baseTokenType, baseTokenContract));
            var formatPriceLong = Convert.ToInt64(
                Double.Parse(price) * Math.Pow(10, Double.Parse(baseTokenContractInfo.Data.Decimals)) /
                baseTokenContractInfo.Data.Quantum
            );
            var formatPrice = formatPriceLong.ToString();
            var amountBuy = Convert.ToInt64(
                Convert.ToDouble(formatPriceLong) * Double.Parse(amount)
            ).ToString();
            var orderMessage = new OrderMessage();
            orderMessage.Amount = amount;
            orderMessage.BaseToken = orderInfo.Data.BaseToken;
            orderMessage.QuoteToken = quoteToken;
            orderMessage.Price = formatPrice;
            orderMessage.StarkKey = starkKey;
            orderMessage.ExpirationTimestamp = 4194303;
            orderMessage.Nonce = orderInfo.Data.Nonce;
            orderMessage.FeeInfo = new FeeInfo(
                Convert.ToInt64(Double.Parse(orderInfo.Data.FeeRate) * Double.Parse(amountBuy)),
                orderInfo.Data.FeeToken,
                Convert.ToInt64(vaultIds[0])
            );

            if (orderType == OrderType.BUY)
            {
                orderMessage.Direction = OrderMessage.DIRECTION_BID;
                orderMessage.TokenSell = orderInfo.Data.BaseToken;
                orderMessage.TokenBuy = quoteToken;
                orderMessage.AmountSell = amountBuy;
                orderMessage.AmountBuy = amount;
                orderMessage.VaultIdBuy = vaultIds[1];
                orderMessage.VaultIdSell = vaultIds[0];
            }
            else
            {
                orderMessage.Direction = OrderMessage.DIRECTION_ASK;
                orderMessage.TokenSell = quoteToken;
                orderMessage.TokenBuy = orderInfo.Data.BaseToken;
                orderMessage.AmountSell = amount;
                orderMessage.AmountBuy = amountBuy;
                orderMessage.VaultIdBuy = vaultIds[0];
                orderMessage.VaultIdSell = vaultIds[1];
            }

            orderMessage.Signature = SignOrderMsgWithFee(
                privateKey,
                orderMessage.VaultIdSell,
                orderMessage.VaultIdBuy,
                orderMessage.AmountSell,
                orderMessage.AmountBuy,
                orderMessage.TokenSell,
                orderMessage.TokenBuy,
                orderMessage.Nonce,
                orderMessage.ExpirationTimestamp,
                orderMessage.FeeInfo.TokenId,
                orderMessage.FeeInfo.SourceVaultId,
                orderMessage.FeeInfo.FeeLimit
            );
            return await _restClient.Order(orderMessage);
        }

        internal async Task<string> GetAssetId(string contractAddress, string tokenId, string type)
        {
            var contractInfo = await _restClient.GetContractInfo(new GetContractInfoMessage(type, contractAddress));
            var getAssetIdMessage = new GetAssetIdMessage(contractAddress, type, tokenId, contractInfo.Data.Quantum);
            var getAssetIdResponse = await _restClient.GetAssetId(getAssetIdMessage);
            var assetId = getAssetIdResponse.Data.AssetId;
            return assetId;
        }

        internal Signature SignTransferMessage(
            string privateKey,
            string amount,
            Int64 nonce,
            string senderVaultId,
            string token,
            string receiverVaultId,
            string receiverPublicKey,
            Int64 expirationTimeStamp = 4194303
        )
        {
            var (r, s) = CryptoService.Sign(
                CryptoService.ParsePositive(privateKey.ToLower().Replace("0x", "")),
                CryptoService.GetTransferMsgHash(
                    Int64.Parse(amount),
                    nonce,
                    Int64.Parse(senderVaultId),
                    CryptoService.ParsePositive(token.ToLower().Replace("0x", "")),
                    Int64.Parse(receiverVaultId),
                    CryptoService.ParsePositive(receiverPublicKey.ToLower().Replace("0x", "")),
                    expirationTimeStamp,
                    null
                ),
                null
            );
            var result = new Signature($"0x{r.ToString("x")}", $"0x{s.ToString("x")}");
            return result;
        }

        internal Signature SignOrderMsgWithFee(
            string privateKey,
            string vaultIdSell,
            string vaultIdBuy,
            string amountSell,
            string amountBuy,
            string tokenSell,
            string tokenBuy,
            long nonce,
            long expirationTimestamp,
            string feeToken,
            long feeSourceVaultId,
            long feeLimit
        )
        {
            var hash = CryptoService.GetLimitOrderMsgHashWithFee(
                Int64.Parse(vaultIdSell),
                Int64.Parse(vaultIdBuy),
                Int64.Parse(amountSell),
                Int64.Parse(amountBuy),
                CryptoService.ParsePositive(tokenSell.ToLower().Replace("0x", "")),
                CryptoService.ParsePositive(tokenBuy.ToLower().Replace("0x", "")),
                nonce,
                expirationTimestamp,
                CryptoService.ParsePositive(feeToken.ToLower().Replace("0x", "")),
                feeSourceVaultId,
                feeLimit
            );
            var (r, s) = CryptoService.Sign(
                CryptoService.ParsePositive(privateKey.ToLower().Replace("0x", "")),
                hash,
                null
            );
            var result = new Signature($"0x{r.ToString("x")}", $"0x{s.ToString("x")}");
            return result;
        }


        internal async Task<(string, string)> GetVaultIds(string assetId, string starkKey, string receiver)
        {
            var getVaultIdResponse =
                await _restClient.GetVaultId(new GetVaultIdMessage(assetId, new List<string> { starkKey, receiver }));
            var senderVaultId = getVaultIdResponse.Data.VaultIds[0];
            var receiverVaultId = getVaultIdResponse.Data.VaultIds[1];
            return (senderVaultId, receiverVaultId);
        }

        public static ReddioClient Mainnet()
        {
            return new ReddioClient(ReddioRestClient.Mainnet());
        }

        public static ReddioClient Testnet()
        {
            return new ReddioClient(ReddioRestClient.Testnet());
        }
    }

    public class TransferFailedException : Exception
    {
        public GetRecordResponse Reocrd { get; }

        public TransferFailedException(string message, GetRecordResponse reocrd) : base(message)
        {
            this.Reocrd = reocrd;
        }
    }
}