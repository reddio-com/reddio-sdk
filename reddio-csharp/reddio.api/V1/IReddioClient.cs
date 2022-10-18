using Reddio.Api.V1.Rest;

namespace Reddio.Api.V1;

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

    public Task<ResponseWrapper<GetRecordResponse>> GetRecord(string starkKey, Int64 sequenceId);
    public Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetApproved(string starkKey, Int64 sequenceId);

    public Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetApproved(
        string starkKey,
        Int64 sequenceId,
        TimeSpan interval,
        CancellationToken cancellationToken
    );
}