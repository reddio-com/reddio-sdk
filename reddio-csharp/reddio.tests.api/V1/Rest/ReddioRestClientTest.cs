using System.Reflection;
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
            "0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0"));
        Assert.NotEmpty(response.Data.list);
    }
}