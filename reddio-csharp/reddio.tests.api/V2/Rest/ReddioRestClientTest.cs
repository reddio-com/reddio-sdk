using System;
using Newtonsoft.Json;
using reddio.api.V2.Rest;
using Xunit;

namespace reddio.tests.api.V2.Rest;

public class ReddioRestClientTest
{
    [Fact]
    public async void TestGetBalance()
    {
        var restClient = ReddioRestClient.Testnet();
        var response = await restClient.GetBalance(new GetBalanceMessage(
                "0x1c9d32ba737263bbdc274c474488179ce4bc09173339b7f4f495caf0040337c", null, null, null
            )
        );
        Assert.Equal("OK", response.Status);
    }
}