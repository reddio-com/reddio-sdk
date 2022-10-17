using Reddio.Api.V1;
using Xunit;

namespace Reddio.Tests.Api.V1;

public class ReddioClientTests
{
    [Fact]
    public async void Test1()
    {
        var client = ReddioClient.Testnet();
        var assetId = await client.GetAssetId("0x941661bd1134dc7cc3d107bf006b8631f6e65ad5","497","ERC721");
        Assert.Equal("0x1c9c7dee2be5015eddff167dda36f319f6bfcd4bd41f9d5cb49fe164bc58cb1",assetId);
    }
}