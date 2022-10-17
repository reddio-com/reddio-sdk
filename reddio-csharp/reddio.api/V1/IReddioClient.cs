using Reddio.Api.V1.Rest;

namespace reddio.api.V1;
/// <summary>
/// High-Level API.
/// </summary>
public interface IReddioClient
{
    public Task<ResponseWrapper<TransferResponse>> Transfer(
        String starkKey,
        String privateKey,
        String amount,
        String contractAddress,
        String tokenId,
        String type,
        String receiver,
        Int64 expirationTimeStamp
    );
}