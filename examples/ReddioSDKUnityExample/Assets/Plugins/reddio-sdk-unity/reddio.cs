using System;
using System.Globalization;
using System.Numerics;
using System.Runtime.InteropServices;
using System.Text;

namespace Reddio.Crypto {
    public class CryptoException : Exception
    {
        public CryptoException(string message): base(message)
        {
        }
    }

    public class CryptoService {
        [DllImport("libcrypto", EntryPoint="get_private_key_from_eth_signature")]
        private static extern int GetPrivateKeyFromEthSignatureImpl([MarshalAs(UnmanagedType.LPStr)]string ethSignature, [MarshalAs(UnmanagedType.LPStr)]StringBuilder privateKeyStr);

        [StructLayout(LayoutKind.Sequential)]
        private struct SignDocument {
            [MarshalAs(UnmanagedType.LPStr)]
            public string privateKey;
            [MarshalAs(UnmanagedType.LPStr)]
            public string msgHash;
            [MarshalAs(UnmanagedType.LPStr)]
            public string? seed;
        }

        [StructLayout(LayoutKind.Sequential)]
        private struct SignResult {
            // cannot use [MarshalAs(UnmanagedType.LPStr)] and StringBuilder
            // to automatically marshal the string in a field of struct
            public IntPtr r;
            public IntPtr s;
        }

        private const int BIG_INT_BUFFER_SIZE = 65;

        [DllImport("libcrypto", EntryPoint="sign")]
        private static extern int SignImpl(SignDocument doc, SignResult result);

        [StructLayout(LayoutKind.Sequential)]
        private struct Signature {
            [MarshalAs(UnmanagedType.LPStr)]
            public string publicKey;
            [MarshalAs(UnmanagedType.LPStr)]
            public string msgHash;
            [MarshalAs(UnmanagedType.LPStr)]
            public string r;
            [MarshalAs(UnmanagedType.LPStr)]
            public string s;
        }
        [DllImport("libcrypto", EntryPoint="verify")]
        private static extern int VerifyImpl(Signature signature, ref bool ok);
        [DllImport("libcrypto", EntryPoint="get_public_key")]
        private static extern int GetPublicKey([MarshalAs(UnmanagedType.LPStr)]string privateKey, [MarshalAs(UnmanagedType.LPStr)]StringBuilder publicKey);
        
        [DllImport("libcrypto", EntryPoint="explain")]
        private static extern string ExplainError(int errno);

        public static BigInteger ParsePositive(string hex) 
        {
            return BigInteger.Parse("0" + hex, NumberStyles.HexNumber);
        }

        public static BigInteger GetPrivateKeyFromEthSignature(BigInteger ethSignature)
        {
            var ethSignatureStr = ethSignature.ToString("x");
            return GetPrivateKeyFromEthSignature(ethSignatureStr);
        }

        public static BigInteger GetPrivateKeyFromEthSignature(string ethSignatureStr)
        {
            var privateKeyStr = new StringBuilder(BIG_INT_BUFFER_SIZE);

            var errno = GetPrivateKeyFromEthSignatureImpl(ethSignatureStr, privateKeyStr);
            if (errno != 0) {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(privateKeyStr.ToString());
        }
            
        public static (BigInteger, BigInteger) Sign(BigInteger privateKey, BigInteger msgHash, BigInteger? seed)
        {
            var privateKeyStr = privateKey.ToString("x");
            var msgHashStr = msgHash.ToString("x");
            string? seedStr = seed?.ToString("x");

            var doc = new SignDocument {
                privateKey = privateKeyStr,
                msgHash = msgHashStr,
                seed = seedStr,
            };
            var result = new SignResult {
                r = Marshal.AllocHGlobal(BIG_INT_BUFFER_SIZE),
                s = Marshal.AllocHGlobal(BIG_INT_BUFFER_SIZE),
            };

            var errno = SignImpl(doc, result);
            if (errno != 0) {
                throw new CryptoException(ExplainError(errno));
            }

            var r = Marshal.PtrToStringAnsi(result.r);
            var s = Marshal.PtrToStringAnsi(result.s);

            Marshal.FreeHGlobal(result.r);
            Marshal.FreeHGlobal(result.s);
            return (ParsePositive(r), ParsePositive(s));
        }

        public static bool Verify(BigInteger publicKey, BigInteger msgHash, BigInteger r, BigInteger s) 
        {
            var verified = false;
            var publicKeyStr = publicKey.ToString("x");
            var msgHashStr = msgHash.ToString("x");
            var rStr = r.ToString("x");
            var sStr = s.ToString("x");

            var signature = new Signature {
                publicKey = publicKeyStr,
                msgHash = msgHashStr,
                r = rStr,
                s = sStr,
            };
            var errno = VerifyImpl(signature, ref verified);
            if (errno != 0) {
                throw new CryptoException(ExplainError(errno));
            }
            return verified;
        }

        public static BigInteger GetPublicKey(BigInteger privateKey)
        {
            var privateKeyStr = privateKey.ToString("x");
            var publicKeyStr = new StringBuilder(BIG_INT_BUFFER_SIZE);
            var errno = GetPublicKey(privateKeyStr, publicKeyStr);
            if (errno != 0) {
                throw new CryptoException(ExplainError(errno));
            }
            return ParsePositive(publicKeyStr.ToString());
        }
    }
}
