using Reddio.Api.V1.Rest;
using Reddio.Crypto;

namespace Reddio.Api.V1;

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
            tokenId,
            receiverVaultId,
            receiver,
            expirationTimeStamp);

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

    internal async Task<string> GetAssetId(string contractAddress, string tokenId, string type)
    {
        var getAssetIdMessage = new GetAssetIdMessage(contractAddress, type, tokenId);
        var getAssetIdResponse = await _restClient.GetAssetId(getAssetIdMessage);
        var assetId = getAssetIdResponse.Data.AssetId;
        return assetId;
    }

    internal Signature SignTransferMessage(
        String privateKey,
        String amount,
        Int64 nonce,
        String senderVaultId,
        String token,
        String receiverVaultId,
        String receiverPublicKey,
        Int64 expirationTimeStamp
    )
    {
        var (r, s) = CryptoService.Sign(CryptoService.ParsePositive(privateKey), CryptoService.GetTransferMsgHash(
                Int64.Parse(amount),
                nonce,
                Int64.Parse(senderVaultId),
                CryptoService.ParsePositive(token),
                Int64.Parse(receiverVaultId),
                CryptoService.ParsePositive(receiverPublicKey),
                expirationTimeStamp,
                null
            ),
            null
        );
        var result = new Signature(r.ToString("x"), s.ToString("x"));
        return result;
    }

    internal async Task<(String, String)> GetVaultIds(string assetId, string starkKey, string receiver)
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