package crypto

import (
	"math/big"
	"testing"

	"github.com/stretchr/testify/assert"
)

const privateKey = "3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc"

type SignCase struct {
	msgHash, s, r string
}

type VerifyCase struct {
	publicKey, msgHash, s, r string
	valid                    bool
}

type GetPublicKeyCase struct {
	privateKey, publicKey string
}

func TestSign(t *testing.T) {
	cases := []SignCase{
		{
			msgHash: "1",
			r:       "3162358736122783857144396205516927012128897537504463716197279730251407200037",
			s:       "1447067116407676619871126378936374427636662490882969509559888874644844560850",
		},
		{
			msgHash: "11",
			r:       "2282960348362869237018441985726545922711140064809058182483721438101695251648",
			s:       "2905868291002627709651322791912000820756370440695830310841564989426104902684",
		},

		{
			msgHash: "223",
			r:       "2851492577225522862152785068304516872062840835882746625971400995051610132955",
			s:       "2227464623243182122770469099770977514100002325017609907274766387592987135410",
		},

		{
			msgHash: "9999",
			r:       "3551214266795401081823453828727326248401688527835302880992409448142527576296",
			s:       "2580950807716503852408066180369610390914312729170066679103651110985466032285",
		},

		{
			msgHash: "387e76d1667c4454bfb835144120583af836f8e32a516765497d23eabe16b3f",
			r:       "3518448914047769356425227827389998721396724764083236823647519654917215164512",
			s:       "3042321032945513635364267149196358883053166552342928199041742035443537684462",
		},

		{
			msgHash: "3a7e76d1697c4455bfb835144120283af236f8e32a516765497d23eabe16b2",
			r:       "2261926635950780594216378185339927576862772034098248230433352748057295357217",
			s:       "2708700003762962638306717009307430364534544393269844487939098184375356178572",
		},

		{
			msgHash: "fa5f0cd1ebff93c9e6474379a213ba111f9e42f2f1cb361b0327e0737203",
			r:       "3016953906936760149710218073693613509330129567629289734816320774638425763370",
			s:       "306146275372136078470081798635201810092238376869367156373203048583896337506",
		},

		{
			msgHash: "4c1e9550e66958296d11b60f8e8e7f7ae99dd0cfa6bd5fa652c1a6c87d4e2cc",
			r:       "3562728603055564208884290243634917206833465920158600288670177317979301056463",
			s:       "1958799632261808501999574190111106370256896588537275453140683641951899459876",
		},

		{
			msgHash: "6362b40c218fb4c8a8bd42ca482145e8513b78e00faa0de76a98ba14fc37ae8",
			r:       "3485557127492692423490706790022678621438670833185864153640824729109010175518",
			s:       "897592218067946175671768586886915961592526001156186496738437723857225288280",
		},
	}

	pk, ok := new(big.Int).SetString(privateKey, 16)
	assert.True(t, ok)

	for _, c := range cases {
		hash, ok := new(big.Int).SetString(c.msgHash, 16)
		assert.True(t, ok)
		r, s, err := Sign(pk, hash, nil)
		assert.Nil(t, err)
		assert.Equal(t, c.r, r.Text(10))
		assert.Equal(t, c.s, s.Text(10))
	}
}

func TestVerify(t *testing.T) {
	cases := []VerifyCase{
		{
			publicKey: "01ef15c18599971b7beced415a40f0c7deacfd9b0d1819e03d723d8bc943cfca",
			msgHash:   "0000000000000000000000000000000000000000000000000000000000000002",
			r:         "0411494b501a98abd8262b0da1351e17899a0c4ef23dd2f96fec5ba847310b20",
			s:         "0405c3191ab3883ef2b763af35bc5f5d15b3b4e99461d70e84c654a351a7c81b",
			valid:     true,
		},
		{
			publicKey: "077a4b314db07c45076d11f62b6f9e748a39790441823307743cf00d6597ea43",
			msgHash:   "0397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f",
			r:         "0173fd03d8b008ee7432977ac27d1e9d1a1f6c98b1a2f05fa84a21c84c44e882",
			s:         "01f2c44a7798f55192f153b4c48ea5c1241fbb69e6132cc8a0da9c5b62a4286e",
			valid:     false,
		},
	}

	for _, c := range cases {
		publicKey, ok := new(big.Int).SetString(c.publicKey, 16)
		assert.True(t, ok)
		msgHash, ok := new(big.Int).SetString(c.msgHash, 16)
		assert.True(t, ok)
		r, ok := new(big.Int).SetString(c.r, 16)
		assert.True(t, ok)
		s, ok := new(big.Int).SetString(c.s, 16)
		assert.True(t, ok)

		valid, err := Verify(publicKey, msgHash, r, s)
		assert.Nil(t, err)
		assert.Equal(t, c.valid, valid)
	}
}

func TestGetPublicKey(t *testing.T) {
	cases := []GetPublicKeyCase{
		{
			privateKey: "03c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc",
			publicKey:  "077a3b314db07c45076d11f62b6f9e748a39790441823307743cf00d6597ea43",
		},
		{
			privateKey: "0000000000000000000000000000000000000000000000000000000000000012",
			publicKey:  "019661066e96a8b9f06a1d136881ee924dfb6a885239caa5fd3f87a54c6b25c4",
		},
	}

	for _, c := range cases {
		privateKey, ok := new(big.Int).SetString(c.privateKey, 16)
		assert.True(t, ok)
		expectedKey, ok := new(big.Int).SetString(c.publicKey, 16)
		assert.True(t, ok)
		publicKey, err := GetPublicKey(privateKey)
		assert.Nil(t, err)
		assert.True(t, expectedKey.Cmp(publicKey) == 0)
	}
}

func TestFFIError(t *testing.T) {
	pk, ok := new(big.Int).SetString(privateKey, 16)
	assert.True(t, ok)
	invalidHex, ok := new(big.Int).SetString("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16)
	assert.True(t, ok)
	_, _, err := Sign(pk, invalidHex, nil)
	assert.NotNil(t, err)
	assert.Equal(t, "not an invalid hex number", err.Error())
}

func TestGeneratePrivateKey(t *testing.T) {
	type GeneratePrivateKeyCase struct {
		ethSignature string
		privateKey   string
	}

	cases := []GeneratePrivateKeyCase{
		{
			ethSignature: "21fbf0696d5e0aa2ef41a2b4ffb623bcaf070461d61cf7251c74161f82fec3a4370854bc0a34b3ab487c1bc021cd318c734c51ae29374f2beb0e6f2dd49b4bf41c",
			privateKey:   "766f11e90cd7c7b43085b56da35c781f8c067ac0d578eabdceebc4886435bda",
		},
	}

	for _, c := range cases {
		ethSignature, ok := new(big.Int).SetString(c.ethSignature, 16)
		assert.True(t, ok)
		expectedKey, ok := new(big.Int).SetString(c.privateKey, 16)
		assert.True(t, ok)
		privateKey, err := GetPrivateKeyFromEthSignature(ethSignature)
		assert.Nil(t, err)
		assert.True(t, expectedKey.Cmp(privateKey) == 0)
	}
}

func TestGetTransferMsgHash(t *testing.T) {
	type GetTransferMsgHashCase struct {
		amount              int64
		nonce               int64
		senderVaultID       int64
		token               string
		receiverVaultID     int64
		receiverPublicKey   string
		expirationTimeStamp int64
		condition           *string
		expectedHash        string
	}
	cases := []GetTransferMsgHashCase{
		{
			amount:              2154549703648910716,
			nonce:               1,
			senderVaultID:       34,
			token:               "3003a65651d3b9fb2eff934a4416db301afd112a8492aaf8d7297fc87dcd9f4",
			receiverVaultID:     21,
			receiverPublicKey:   "5fa3383597691ea9d827a79e1a4f0f7949435ced18ca9619de8ab97e661020",
			expirationTimeStamp: 438953,
			condition:           nil,
			expectedHash:        "6366b00c218fb4c8a8b142ca482145e8513c78e00faa0de76298ba14fc37ae7",
		},
	}

	for _, c := range cases {
		token, ok := new(big.Int).SetString(c.token, 16)
		assert.True(t, ok)
		receiverPublicKey, ok := new(big.Int).SetString(c.receiverPublicKey, 16)
		assert.True(t, ok)
		var condition *big.Int
		if c.condition != nil {
			condition, ok = new(big.Int).SetString(*c.condition, 16)
			assert.True(t, ok)
		}
		expected, ok := new(big.Int).SetString(c.expectedHash, 16)
		assert.True(t, ok)
		msgHash, err := GetTransferMsgHash(
			c.amount,
			c.nonce,
			c.senderVaultID,
			token,
			c.receiverVaultID,
			receiverPublicKey,
			c.expirationTimeStamp,
			condition,
		)

		assert.Nil(t, err)
		assert.NotNil(t, msgHash)
		assert.True(t, expected.Cmp(msgHash) == 0)
	}
}

func TestGetLimitOrderMsgHash(t *testing.T) {
	type GetLimitOrderMsgHashCase struct {
		vaultSell           int64
		vaultBuy            int64
		amountSell          int64
		amountBuy           int64
		tokenSell           string
		tokenBuy            string
		nonce               int64
		expirationTimeStamp int64
		expectedHash        string
	}

	cases := []GetLimitOrderMsgHashCase{
		{
			vaultSell:           21,
			vaultBuy:            27,
			amountSell:          2154686749748910716,
			amountBuy:           1470242115489520459,
			tokenSell:           "5fa3383597691ea9d827a79e1a4f0f7989c35ced18ca9619de8ab97e661020",
			tokenBuy:            "774961c824a3b0fb3d2965f01471c9c7734bf8dbde659e0c08dca2ef18d56a",
			nonce:               0,
			expirationTimeStamp: 438953,
			expectedHash:        "397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f",
		},
	}
	for _, c := range cases {
		tokenSell, ok := new(big.Int).SetString(c.tokenSell, 16)
		assert.True(t, ok)
		tokenBuy, ok := new(big.Int).SetString(c.tokenBuy, 16)
		assert.True(t, ok)

		expected, ok := new(big.Int).SetString(c.expectedHash, 16)
		assert.True(t, ok)
		msgHash, err := GetLimitOrderMsgHash(
			c.vaultSell,
			c.vaultBuy,
			c.amountSell,
			c.amountBuy,
			tokenSell,
			tokenBuy,
			c.nonce,
			c.expirationTimeStamp,
		)

		assert.Nil(t, err)
		assert.NotNil(t, msgHash)
		assert.True(t, expected.Cmp(msgHash) == 0)
	}
}
