using System;
using System.Reflection;
using System.Text.Json;
using Microsoft.VisualStudio.TestPlatform.CommunicationUtilities;
using Reddio.Api.V1;
using Reddio.Api.V1.Rest;
using Xunit;
using Xunit.Abstractions;

namespace Reddio.Tests.Api.V1;

public class ReddioClientTests
{
    private readonly ITestOutputHelper _testOutputHelper;

    public ReddioClientTests(ITestOutputHelper testOutputHelper)
    {
        _testOutputHelper = testOutputHelper;
    }

    [Fact]
    public async void TestGetAssetId()
    {
        var client = ReddioClient.Testnet();
        var assetId = await client.GetAssetId("0x941661bd1134dc7cc3d107bf006b8631f6e65ad5", "497", "ERC721");
        Assert.Equal("0x1c9c7dee2be5015eddff167dda36f319f6bfcd4bd41f9d5cb49fe164bc58cb1", assetId);
    }

    [Fact]
    public async void TestGetVaults()
    {
        var client = ReddioClient.Testnet();
        var (senderVaultId, receiverVaultId) = await client.GetVaultIds(
            "0x1c9c7dee2be5015eddff167dda36f319f6bfcd4bd41f9d5cb49fe164bc58cb1",
            "0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
            "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c");
        Assert.Equal("23400424", senderVaultId);
        Assert.Equal("23400425", receiverVaultId);
    }

    [Fact]
    public async void TestSignTransferMessage()
    {
        var client = ReddioClient.Testnet();
        var s = client.SignTransferMessage(
            "0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d",
            "1",
            59,
            "23400424",
            "0x1c9c7dee2be5015eddff167dda36f319f6bfcd4bd41f9d5cb49fe164bc58cb1",
            "23400425",
            "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c"
        );
        Assert.Equal("0x7b191774b10a208331d716ab4fe0ecd24b430d0142bdd123a14d243abf626b1", s.R);
        Assert.Equal("0x21f3a32d5779668d66af7f9f161d90afb3765c3a6326b6397f73ea346f94e5d", s.S);
    }

    [Fact]
    public async void TestTransfer()
    {
        var client = ReddioClient.Testnet();
        var result = await client.Transfer(
            "0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
            "0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d",
            "1",
            "0x941661bd1134dc7cc3d107bf006b8631f6e65ad5",
            "497",
            "ERC721",
            "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c"
        );
        Assert.Equal("OK", result.Status);
        _testOutputHelper.WriteLine(JsonSerializer.Serialize(result));
    }

    [Fact]
    public async void TestWaitingRecordGetAccepted()
    {
        var client = ReddioClient.Testnet();
        var result =
            await client.WaitingTransferGetAccepted("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
                300523);
        Assert.Equal("OK", result.Status);
        Assert.Single(result.Data);
        Assert.Equal(SequenceRecord.SequenceStatusAccepted, result.Data[0].Status);
        _testOutputHelper.WriteLine(JsonSerializer.Serialize(result));
    }

    [Fact]
    public async void TestGetRecords()
    {
        var client = ReddioClient.Testnet();
        var result = await client.GetRecords("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0");
        _testOutputHelper.WriteLine(JsonSerializer.Serialize(result));
    }

    [Fact]
    public async void TestGetRecordsPagination()
    {
        var client = ReddioClient.Testnet();
        var result = await client.GetRecords("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 0);
        Assert.Equal(5, result.Data.List.Count);
        _testOutputHelper.WriteLine(JsonSerializer.Serialize(result));
    }
}