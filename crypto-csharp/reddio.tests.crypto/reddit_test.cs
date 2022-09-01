using System;
using System.Globalization;
using System.Numerics;
using Microsoft.VisualBasic.CompilerServices;
using Xunit;

using Reddio.Crypto;

namespace Reddio.Tests {

    public class CryptoServiceTest 
    {
        [Fact]
        public void TestGetPrivateKeyFromEthSignature()
        {
            var ethSignatureStr = "21fbf0696d5e0aa2ef41a2b4ffb623bcaf070461d61cf7251c74161f82fec3a4370854bc0a34b3ab487c1bc021cd318c734c51ae29374f2beb0e6f2dd49b4bf41c";
			var expectedPrivateKeyStr = "766f11e90cd7c7b43085b56da35c781f8c067ac0d578eabdceebc4886435bda";

            var ethSignature = BigInteger.Parse(ethSignatureStr, NumberStyles.AllowHexSpecifier);
            var expectedPrivateKey = BigInteger.Parse(expectedPrivateKeyStr, NumberStyles.AllowHexSpecifier);

            Assert.Equal(expectedPrivateKey, Crypto.CryptoService.GetPrivateKeyFromEthSignature(ethSignature));
        }

        BigInteger privateKey = BigInteger.Parse("3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc", NumberStyles.AllowHexSpecifier);

        [Theory]
        [InlineData("1", "3162358736122783857144396205516927012128897537504463716197279730251407200037", "1447067116407676619871126378936374427636662490882969509559888874644844560850")]
        [InlineData("11", "2282960348362869237018441985726545922711140064809058182483721438101695251648", "2905868291002627709651322791912000820756370440695830310841564989426104902684")]
        [InlineData("223", "2851492577225522862152785068304516872062840835882746625971400995051610132955", "2227464623243182122770469099770977514100002325017609907274766387592987135410")]
        [InlineData("9999", "3551214266795401081823453828727326248401688527835302880992409448142527576296", "2580950807716503852408066180369610390914312729170066679103651110985466032285")]
        [InlineData("387e76d1667c4454bfb835144120583af836f8e32a516765497d23eabe16b3f", "3518448914047769356425227827389998721396724764083236823647519654917215164512", "3042321032945513635364267149196358883053166552342928199041742035443537684462")]
        [InlineData("3a7e76d1697c4455bfb835144120283af236f8e32a516765497d23eabe16b2", "2261926635950780594216378185339927576862772034098248230433352748057295357217", "2708700003762962638306717009307430364534544393269844487939098184375356178572")]
        [InlineData("0fa5f0cd1ebff93c9e6474379a213ba111f9e42f2f1cb361b0327e0737203", "3016953906936760149710218073693613509330129567629289734816320774638425763370", "306146275372136078470081798635201810092238376869367156373203048583896337506")]
        [InlineData("4c1e9550e66958296d11b60f8e8e7f7ae99dd0cfa6bd5fa652c1a6c87d4e2cc", "3562728603055564208884290243634917206833465920158600288670177317979301056463", "1958799632261808501999574190111106370256896588537275453140683641951899459876")]
        [InlineData("6362b40c218fb4c8a8bd42ca482145e8513b78e00faa0de76a98ba14fc37ae8", "3485557127492692423490706790022678621438670833185864153640824729109010175518", "897592218067946175671768586886915961592526001156186496738437723857225288280")]
        public void TestSign(string docHashStr, string rStr, string sStr)
        {
            var docHash = BigInteger.Parse(docHashStr, NumberStyles.AllowHexSpecifier);
            var expectedR = BigInteger.Parse(rStr);
            var expectedS = BigInteger.Parse(sStr);

            var (r, s) = Crypto.CryptoService.Sign(privateKey, docHash, null);

            Assert.Equal(expectedR, r);
            Assert.Equal(expectedS, s);
        }
        
        [Theory]
        [InlineData("01ef15c18599971b7beced415a40f0c7deacfd9b0d1819e03d723d8bc943cfca", "0000000000000000000000000000000000000000000000000000000000000002", "0411494b501a98abd8262b0da1351e17899a0c4ef23dd2f96fec5ba847310b20", "0405c3191ab3883ef2b763af35bc5f5d15b3b4e99461d70e84c654a351a7c81b", true)]
        [InlineData("077a4b314db07c45076d11f62b6f9e748a39790441823307743cf00d6597ea43", "0397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f", "0173fd03d8b008ee7432977ac27d1e9d1a1f6c98b1a2f05fa84a21c84c44e882", "01f2c44a7798f55192f153b4c48ea5c1241fbb69e6132cc8a0da9c5b62a4286e", false)]
        public void TestVerify(string publicKey, string msgHash, string rStr, string sStr, bool valid)
        {
            var publicKeyBigInt = BigInteger.Parse(publicKey, NumberStyles.AllowHexSpecifier);
            var msgHashBigInt = BigInteger.Parse(msgHash, NumberStyles.AllowHexSpecifier);
            var r = BigInteger.Parse(rStr, NumberStyles.AllowHexSpecifier);
            var s = BigInteger.Parse(sStr, NumberStyles.AllowHexSpecifier);

            var validResult = Crypto.CryptoService.Verify(publicKeyBigInt, msgHashBigInt, r, s);
            Assert.Equal(valid, validResult);
        }

        [Theory]
        [InlineData("03c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc", "077a3b314db07c45076d11f62b6f9e748a39790441823307743cf00d6597ea43")]
        [InlineData("0000000000000000000000000000000000000000000000000000000000000012", "019661066e96a8b9f06a1d136881ee924dfb6a885239caa5fd3f87a54c6b25c4")]
        public void TestGetPublicKey(string privateKey, string publicKey)
        {
            var privateKeyBigInt = BigInteger.Parse(privateKey, NumberStyles.AllowHexSpecifier);
            var publicKeyBigInt = BigInteger.Parse(publicKey, NumberStyles.AllowHexSpecifier);
            var publicKeyResult = Crypto.CryptoService.GetPublicKey(privateKeyBigInt);
            Assert.Equal(publicKeyBigInt, publicKeyResult);
        }

        [Theory]
        [InlineData(
            "2154549703648910716",
            "1",
            "34",
            "3003a65651d3b9fb2eff934a4416db301afd112a8492aaf8d7297fc87dcd9f4",
            "21",
            "5fa3383597691ea9d827a79e1a4f0f7949435ced18ca9619de8ab97e661020",
            "438953",
            null,
            "6366b00c218fb4c8a8b142ca482145e8513c78e00faa0de76298ba14fc37ae7"
        )]
        public void TestGetTransferMsgHash(
            string amount,
            string nonce,
            string senderVaultId,
            string token,
            string receiverVaultId,
            string receiverPublicKey,
            string expirationTimeStamp,
            string? condition,
            string expectedHash)
        {
            BigInteger? parsedCondition = null;
            if (condition != null)
            {
                parsedCondition = BigInteger.Parse(condition);
            }

            var actual = CryptoService.GetTransferMsgHash(
                Int64.Parse(amount),
                Int64.Parse(nonce),
                Int64.Parse(senderVaultId),
                BigInteger.Parse(token, NumberStyles.AllowHexSpecifier),
                Int64.Parse(receiverVaultId),
                BigInteger.Parse(receiverPublicKey, NumberStyles.AllowHexSpecifier),
                Int64.Parse(expirationTimeStamp),
                parsedCondition);
            Assert.Equal(expectedHash, actual.ToString("x"));
        }       
        
        [Theory]
        [InlineData(
            "2154549703648910716",
            "1",
            "34",
            "3003a65651d3b9fb2eff934a4416db301afd112a8492aaf8d7297fc87dcd9f4",
            "21",
            "5fa3383597691ea9d827a79e1a4f0f7949435ced18ca9619de8ab97e661020",
            "438953",
            "70bf591713d7cb7150523cf64add8d49fa6b61036bba9f596bd2af8e3bb86f9",
            "593128169",
            "7",
            null,
            "5359c71cf08f394b7eb713532f1a0fcf1dccdf1836b10db2813e6ff6b6548db"
        )]
        public void TestGetTransferMsgHashWithFee(
            string amount,
            string nonce,
            string senderVaultId,
            string token,
            string receiverVaultId,
            string receiverPublicKey,
            string expirationTimeStamp,
            string feeToken,
            string feeVaultId,
            string feeLimit,
            string? condition,
            string expectedHash)
        {
            BigInteger? parsedCondition = null;
            if (condition != null)
            {
                parsedCondition = BigInteger.Parse(condition);
            }

            var actual = CryptoService.GetTransferMsgHashWithFee(
                Int64.Parse(amount),
                Int64.Parse(nonce),
                Int64.Parse(senderVaultId),
                BigInteger.Parse(token, NumberStyles.AllowHexSpecifier),
                Int64.Parse(receiverVaultId),
                BigInteger.Parse(receiverPublicKey, NumberStyles.AllowHexSpecifier),
                Int64.Parse(expirationTimeStamp),
                BigInteger.Parse(feeToken, NumberStyles.AllowHexSpecifier),
                Int64.Parse(feeVaultId),
                Int64.Parse(feeLimit),
                parsedCondition);
            Assert.Equal(expectedHash, actual.ToString("x"));
        }

        [Theory]
        [InlineData(
            "21",
            "27",
            "2154686749748910716",
            "1470242115489520459",
            "5fa3383597691ea9d827a79e1a4f0f7989c35ced18ca9619de8ab97e661020",
            "774961c824a3b0fb3d2965f01471c9c7734bf8dbde659e0c08dca2ef18d56a",
            "0",
            "438953",
            "397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f"
        )]
        public void TestGetLimitOrderMsgHash(
            string vaultSell,
            string vaultBuy,
            string amountSell,
            string amountBuy,
            string tokenSell,
            string tokenBuy,
            string nonce,
            string expirationTimeStamp,
            string expectedHash)
        {
            var actual = CryptoService.GetLimitOrderMsgHash(
                Int64.Parse(vaultSell),
                Int64.Parse(vaultBuy),
                Int64.Parse(amountSell),
                Int64.Parse(amountBuy),
                BigInteger.Parse(tokenSell, NumberStyles.AllowHexSpecifier),
                BigInteger.Parse(tokenBuy, NumberStyles.AllowHexSpecifier),
                Int64.Parse(nonce),
                Int64.Parse(expirationTimeStamp)
            );
            Assert.Equal(expectedHash, actual.ToString("x"));
        }

        [Theory]
        [InlineData(
            "21",
            "27",
            "2154686749748910716",
            "1470242115489520459",
            "5fa3383597691ea9d827a79e1a4f0f7989c35ced18ca9619de8ab97e661020",
            "774961c824a3b0fb3d2965f01471c9c7734bf8dbde659e0c08dca2ef18d56a",
            "0",
            "438953",
            "70bf591713d7cb7150523cf64add8d49fa6b61036bba9f596bd2af8e3bb86f9",
            "593128169",
            "7",
            "2a6c0382404920ebd73c1cbc319cd38974e7e255e00394345e652b0ce2cefbd"
        )]
        public void TestGetLimitOrderMsgHashWithFee(
            string vaultSell,
            string vaultBuy,
            string amountSell,
            string amountBuy,
            string tokenSell,
            string tokenBuy,
            string nonce,
            string expirationTimeStamp,
            string feeToken,
            string feeVaultId,
            string feeLimit,
            string expectedHash)
        {
            var actual = CryptoService.GetLimitOrderMsgHashWithFee(
                Int64.Parse(vaultSell),
                Int64.Parse(vaultBuy),
                Int64.Parse(amountSell),
                Int64.Parse(amountBuy),
                BigInteger.Parse(tokenSell, NumberStyles.AllowHexSpecifier),
                BigInteger.Parse(tokenBuy, NumberStyles.AllowHexSpecifier),
                Int64.Parse(nonce),
                Int64.Parse(expirationTimeStamp),
                BigInteger.Parse(feeToken, NumberStyles.AllowHexSpecifier),
                Int64.Parse(feeVaultId),
                Int64.Parse(feeLimit)
            );
            Assert.Equal(expectedHash, actual.ToString("x"));
        }
    }
}
