using System.Reflection;
using Reddio.Api.V1;
using Reddio.Api.V1.Rest;
using Xunit;

namespace Reddio.Tests.Api.V1.Rest;

public class ReddioRestClientTest
{
    [Fact]
    public async void TestGetRecord()
    {
        var restClient = ReddioRestClient.Testnet();
        var response = await restClient.GetRecord(new GetRecordMessage(
            "0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 300523));
        Assert.Equal(SequenceRecord.SequenceStatusAccepted, response.Data[0].Status);
    }

    [Fact]
    public async void TestGetRecords()
    {
        var restClient = ReddioRestClient.Testnet();
        var response = await restClient.GetRecords(new GetRecordsMessage(
            "0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
            null,
            null,
            null
        ));
        Assert.NotEmpty(response.Data.List);
    }

    [Fact]
    public async void TestGetContractInfoERC721()
    {
        var restClient = ReddioRestClient.Testnet();
        var response =
            await restClient.GetContractInfo(new GetContractInfoMessage("ERC721",
                "0x941661bd1134dc7cc3d107bf006b8631f6e65ad5"));
        Assert.Equal("OK", response.Status);
        Assert.Equal(1, response.Data.Quantum);
        Assert.Equal("0", response.Data.Decimals);
    }
    
    [Fact]
    public async void TestGetContractInfoEth()
    {
        var restClient = ReddioRestClient.Testnet();
        var response =
            await restClient.GetContractInfo(new GetContractInfoMessage("ETH",
                "ETH"));
        Assert.Equal("OK", response.Status);
        Assert.Equal(1000000000000, response.Data.Quantum);
        Assert.Equal("18", response.Data.Decimals);
    }
}