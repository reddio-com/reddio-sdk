using System.Collections;
using System.Collections.Generic;
using Reddio.Crypto;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.UI;

public class SignTheMessage : MonoBehaviour
{
    public Button signButton;
    // Start is called before the first frame update
    void Start()
    {
        var btn = signButton.GetComponent<Button>();
        btn.onClick.AddListener(SignMessageThenPrint);

    }

    private void SignMessageThenPrint()
    {
        var (r,s) = CryptoService.Sign(CryptoService.ParsePositive("3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc"), CryptoService.ParsePositive("387e76d1667c4454bfb835144120583af836f8e32a516765497d23eabe16b3f"), null);
        Debug.Log("Signed!\n");
        Debug.Log("The expected r is:\n3518448914047769356425227827389998721396724764083236823647519654917215164512\nActual r is:\n"+r+"\nThe expected s is:\n3042321032945513635364267149196358883053166552342928199041742035443537684462\nActual s is:\n"+s);

    }
}
