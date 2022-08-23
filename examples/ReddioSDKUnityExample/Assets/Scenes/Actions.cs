using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Reddio.Crypto;
using TMPro;
using UnityEngine;
using WalletConnectSharp.Core.Models;
using WalletConnectSharp.Core.Models.Ethereum;
using WalletConnectSharp.Core.Models.Ethereum.Types;
using WalletConnectSharp.Unity;
using WalletConnectUnity.Demo.Scripts;

public class Actions : MonoBehaviour
{
    public TextMeshProUGUI accountText;
    public TextMeshProUGUI logText;

    void FixedUpdate()
    {
        if (WalletConnect.ActiveSession.Accounts == null)
        {
            accountText.text = "Waiting for Connection";
            return;
        }

        accountText.text = "\nConnected to Chain " + WalletConnect.ActiveSession.ChainId + ":\n" +
                           WalletConnect.ActiveSession.Accounts[0];
    }

    public async void Disconnect()
    {
        await WalletConnect.ActiveSession.Disconnect();
    }

    public async Task<string> SignTypedData<T>(T data, EIP712Domain eip712Domain, int addressIndex = 0)
    {
        var address = WalletConnect.ActiveSession.Accounts[addressIndex];

        var results = await WalletConnect.ActiveSession.EthSignTypedData(address, data, eip712Domain);

        return results;
    }

    public async void GetStarkKey()
    {
        var address = WalletConnect.ActiveSession.Accounts[0];
        var payload = new ReddioSign(address, "Sign", 5);
        var response = await WalletConnect.ActiveSession.Send<ReddioSign, EthResponse>(payload);
        Debug.Log("Reddio Sign Completed");
        Debug.Log(response.Result);

        var privateKey = CryptoService.GetPrivateKeyFromEthSignature(response.Result);
        var publicKey = CryptoService.GetPublicKey(privateKey);

        logText.text += $"Private Key:\n{privateKey.ToString("x")}\nPublic Key/Stark Key:\n{publicKey.ToString("x")}\n";

        Debug.Log("Private Key");
        Debug.Log(privateKey.ToString("x"));
        Debug.Log("Public Key");
        Debug.Log(publicKey.ToString("x"));
    }

    public class ReddioSign : JsonRpcRequest
    {
        [JsonProperty("params")] private string[] _parameters;

        public ReddioSign(string address, string message, int chainId)
        {
            this.Method = "eth_signTypedData_v4";

            var typeData = ReddioSignPayload.Create(message, chainId);
            var encodedTypeData = JsonConvert.SerializeObject(typeData);
            Debug.Log("encodedTypeData");
            Debug.Log(encodedTypeData);

            this._parameters = new string[] { address, encodedTypeData };
        }
    }

    public class ReddioSignPayload
    {
        [JsonProperty("domain")] public ReddioSignPayloadDomain Domain;
        [JsonProperty("message")] public ReddioSignPayloadMessage Message;
        [JsonProperty("primaryType")] public string PrimaryType;
        [JsonProperty("types")] public Dictionary<string, List<ReddioSignPayloadTypesEntry>> Types;

        private ReddioSignPayload(ReddioSignPayloadDomain domain, ReddioSignPayloadMessage message, string primaryType,
            Dictionary<string, List<ReddioSignPayloadTypesEntry>> types)
        {
            Domain = domain;
            Message = message;
            PrimaryType = primaryType;
            Types = types;
        }

        public static ReddioSignPayload Create(string message, int chainId)
        {
            return new ReddioSignPayload(
                new ReddioSignPayloadDomain(chainId),
                new ReddioSignPayloadMessage(message),
                "reddio",
                new Dictionary<string, List<ReddioSignPayloadTypesEntry>>()
                {
                    {
                        "EIP712Domain", new List<ReddioSignPayloadTypesEntry>()
                        {
                            new("chainId", "uint256")
                        }
                    },
                    {
                        "reddio", new List<ReddioSignPayloadTypesEntry>()
                        {
                            new("contents", "string")
                        }
                    }
                }
            );
        }
    }

    public class ReddioSignPayloadMessage
    {
        public ReddioSignPayloadMessage(string contents)
        {
            Contents = contents;
        }

        [JsonProperty("contents")] public string Contents;
    }
}

public class ReddioSignPayloadTypesEntry
{
    [JsonProperty("name")] public string Name;
    [JsonProperty("type")] public string Type;

    public ReddioSignPayloadTypesEntry(string name, string type)
    {
        Name = name;
        Type = type;
    }
}

public class ReddioSignPayloadDomain
{
    [JsonProperty("chainId")] public int ChainId;

    public ReddioSignPayloadDomain(int chainId)
    {
        ChainId = chainId;
    }
}