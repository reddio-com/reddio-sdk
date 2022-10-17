using Reddio.Api.V1.Rest;
using Reddio.Crypto;

namespace reddio.api.V1;

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
        var getAssetIdMessage = new GetAssetIdMessage(contractAddress, type, tokenId);
        var getAssetIdResponse = await _restClient.GetAssetId(getAssetIdMessage);
        var assetId = getAssetIdResponse.Data.AssetId;
        var getVaultIdResponse =
            await _restClient.GetVaultId(new GetVaultIdMessage(assetId, new List<string>() { starkKey, receiver }));
        var senderVaultId = getVaultIdResponse.Data.VaultIds[0];
        var receiverVaultId = getVaultIdResponse.Data.VaultIds[1];
        var getNonceResponse = await _restClient.GetNonce(new GetNonceMessage(starkKey));
        var nonce = getNonceResponse.Data.Nonce;

        var s = CryptoService.Sign(CryptoService.ParsePositive(privateKey), CryptoService.GetTransferMsgHash(
                Int64.Parse(amount),
                nonce,
                Int64.Parse(senderVaultId),
                CryptoService.ParsePositive(assetId),
                Int64.Parse(receiverVaultId),
                CryptoService.ParsePositive(receiver),
                expirationTimeStamp,
                null
            ),
            null
        );

        var signature = new Signature(s.Item1.ToString("x"), s.Item2.ToString("x"));
        var transferMessage = new TransferMessage(
            tokenId,
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

    public static ReddioClient Mainnet()
    {
        return new ReddioClient(ReddioRestClient.Mainnet());
    }
    
    public static ReddioClient Testnet()
    {
        return new ReddioClient(ReddioRestClient.Testnet());
    }
}