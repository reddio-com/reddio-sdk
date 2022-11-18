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
    public async void TestGetAssetIdERC20()
    {
        var client = ReddioClient.Mainnet();
        var assetId = await client.GetAssetId("0x288b2b6f8767661cc67f16412ed430e03e1915dc", "", "ERC20");
        Assert.Equal("0x13d6fce9e73d9a5c7f6c65a3660b347ca994fe0a7b22e6a015e8d6bcc42990", assetId);
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
    public async void TestTransferNFT()
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
    public async void TestTrasnferERC20()
    {
        var client = ReddioClient.Testnet();
        var result = await client.Transfer(
            "0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581",
            "0xf0b94c9485dfb212b5e7882670657ef8aca14ffd8d5dedc918fd9237ccd724",
            "0.0013",
            "0x57f3560b6793dcc2cb274c39e8b8eba1dd18a086",
            "",
            "ERC20",
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

    // [Fact(Skip = "not reproducible test")]
    [Fact]
    public async void TestOrder()
    {
        IReddioClient client = ReddioClient.Testnet();
        IReddioRestClient restClient = ReddioRestClient.Testnet();

        var balances = await restClient.GetBalances(new GetBalancesMessage(
            "0x6ecaebbe5b9486472d964217e5470380782823bb0d865240ba916d01636310a",
            "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5"
        ));
        Assert.Equal("OK", balances.Status);
        var toSell = balances.Data.list[0];
        var order = await client.Order(
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            "0.013",
            "1",
            "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
            toSell.TokenId,
            "11ed793a-cc11-4e44-9738-97165c4e14a7",
            "ERC721",
            OrderType.SELL
        );
        Assert.Equal("OK", order.Status);
    }
}