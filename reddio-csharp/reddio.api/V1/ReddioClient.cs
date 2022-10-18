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

        public async Task<ResponseWrapper<TransferResponse>> Transfer(string starkKey, string privateKey, string amount,
            string contractAddress, string tokenId, string type,
            string receiver, long expirationTimeStamp = 4194303)
        {
            var assetId = await GetAssetId(contractAddress, tokenId, type);
            var (senderVaultId, receiverVaultId) = await GetVaultIds(assetId, starkKey, receiver);

            var getNonceResponse = await _restClient.GetNonce(new GetNonceMessage(starkKey));
            var nonce = getNonceResponse.Data.Nonce;

            var signature = SignTransferMessage(privateKey,
                amount,
                nonce,
                senderVaultId,
                assetId,
                receiverVaultId,
                receiver,
                expirationTimeStamp);

            var transferMessage = new TransferMessage(
                assetId,
                starkKey,
                amount,
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

                await Task.Delay(interval, cancellationToken);
            }
        }

        internal async Task<string> GetAssetId(string contractAddress, string tokenId, string type)
        {
            var getAssetIdMessage = new GetAssetIdMessage(contractAddress, type, tokenId);
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
}