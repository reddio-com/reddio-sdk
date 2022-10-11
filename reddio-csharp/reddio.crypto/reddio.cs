using System;
using System.Globalization;
using System.Numerics;
using System.Runtime.InteropServices;
using System.Text;

namespace Reddio.Crypto
{
    public class CryptoException : Exception
    {
        public CryptoException(string message) : base(message)
        {
        }
    }

    public class CryptoService
    {
        [DllImport("reddio", EntryPoint = "get_private_key_from_eth_signature")]
        private static extern int GetPrivateKeyFromEthSignatureImpl([MarshalAs(UnmanagedType.LPStr)] string ethSignature, [MarshalAs(UnmanagedType.LPStr)] StringBuilder privateKeyStr);

        [DllImport("reddio", EntryPoint = "get_random_private_key")]
        private static extern int GetRandomPrivateKeyImpl([MarshalAs(UnmanagedType.LPStr)] StringBuilder privateKeyStr);

        [StructLayout(LayoutKind.Sequential)]
        private struct SignDocument
        {
            [MarshalAs(UnmanagedType.LPStr)]
            public string privateKey;
            [MarshalAs(UnmanagedType.LPStr)]
            public string msgHash;
            [MarshalAs(UnmanagedType.LPStr)]
            public string? seed;
        }

        [StructLayout(LayoutKind.Sequential)]
        private struct SignResult
        {
            // cannot use [MarshalAs(UnmanagedType.LPStr)] and StringBuilder
            // to automatically marshal the string in a field of struct
            public IntPtr r;
            public IntPtr s;
        }

        private const int BIG_INT_BUFFER_SIZE = 65;

        [DllImport("reddio", EntryPoint = "sign")]
        private static extern int SignImpl(SignDocument doc, SignResult result);

        [StructLayout(LayoutKind.Sequential)]
        private struct Signature
        {
            [MarshalAs(UnmanagedType.LPStr)]
            public string publicKey;
            [MarshalAs(UnmanagedType.LPStr)]
            public string msgHash;
            [MarshalAs(UnmanagedType.LPStr)]
            public string r;
            [MarshalAs(UnmanagedType.LPStr)]
            public string s;
        }
        [DllImport("reddio", EntryPoint = "verify")]
        private static extern int VerifyImpl(Signature signature, ref bool ok);
        [DllImport("reddio", EntryPoint = "get_public_key")]
        private static extern int GetPublicKey([MarshalAs(UnmanagedType.LPStr)] string privateKey, [MarshalAs(UnmanagedType.LPStr)] StringBuilder publicKey);

        [DllImport("reddio", EntryPoint = "explain")]
        private static extern string ExplainError(int errno);

        private struct TransferMsg
        {
            [MarshalAs(UnmanagedType.LPStr)] public string Amount;
            [MarshalAs(UnmanagedType.LPStr)] public string Nonce;
            [MarshalAs(UnmanagedType.LPStr)] public string SenderVaultId;
            [MarshalAs(UnmanagedType.LPStr)] public string Token;
            [MarshalAs(UnmanagedType.LPStr)] public string ReceiverVaultId;
            [MarshalAs(UnmanagedType.LPStr)] public string ReceiverPublicKey;
            [MarshalAs(UnmanagedType.LPStr)] public string ExpirationTimeStamp;
            [MarshalAs(UnmanagedType.LPStr)] public string? Condition;
        }

        [DllImport("reddio", EntryPoint = "get_transfer_msg_hash")]
        private static extern int GetTransferMsgHash(
            TransferMsg msg,
            [MarshalAs(UnmanagedType.LPStr)] StringBuilder hash
        );

        private struct TransferMsgWithFee
        {
            [MarshalAs(UnmanagedType.LPStr)] public string Amount;
            [MarshalAs(UnmanagedType.LPStr)] public string Nonce;
            [MarshalAs(UnmanagedType.LPStr)] public string SenderVaultId;
            [MarshalAs(UnmanagedType.LPStr)] public string Token;
            [MarshalAs(UnmanagedType.LPStr)] public string ReceiverVaultId;
            [MarshalAs(UnmanagedType.LPStr)] public string ReceiverStarkKey;
            [MarshalAs(UnmanagedType.LPStr)] public string ExpirationTimeStamp;
            [MarshalAs(UnmanagedType.LPStr)] public string FeeToken;
            [MarshalAs(UnmanagedType.LPStr)] public string FeeVaultId;
            [MarshalAs(UnmanagedType.LPStr)] public string FeeLimit;
            [MarshalAs(UnmanagedType.LPStr)] public string? Condition;
        }


        [DllImport("reddio", EntryPoint = "get_transfer_msg_hash_with_fee")]
        private static extern int GetTransferMsgHashWithFee(
            TransferMsgWithFee msg,
            [MarshalAs(UnmanagedType.LPStr)] StringBuilder hash
        );

        private struct LimitOrderMsg
        {
            [MarshalAs(UnmanagedType.LPStr)] public string VaultSell;
            [MarshalAs(UnmanagedType.LPStr)] public string VaultBuy;
            [MarshalAs(UnmanagedType.LPStr)] public string AmountSell;
            [MarshalAs(UnmanagedType.LPStr)] public string AmountBuy;
            [MarshalAs(UnmanagedType.LPStr)] public string TokenSell;
            [MarshalAs(UnmanagedType.LPStr)] public string TokenBuy;
            [MarshalAs(UnmanagedType.LPStr)] public string Nonce;
            [MarshalAs(UnmanagedType.LPStr)] public string ExpirationTimeStamp;
        }
        [DllImport("reddio", EntryPoint = "get_limit_order_msg_hash")]
        private static extern int GetLimitOrderMsgHash(
            LimitOrderMsg msg,
            [MarshalAs(UnmanagedType.LPStr)] StringBuilder hash
        );

        private struct LimitOrderMsgWithFee
        {
            [MarshalAs(UnmanagedType.LPStr)] public string VaultSell;
            [MarshalAs(UnmanagedType.LPStr)] public string VaultBuy;
            [MarshalAs(UnmanagedType.LPStr)] public string AmountSell;
            [MarshalAs(UnmanagedType.LPStr)] public string AmountBuy;
            [MarshalAs(UnmanagedType.LPStr)] public string TokenSell;
            [MarshalAs(UnmanagedType.LPStr)] public string TokenBuy;
            [MarshalAs(UnmanagedType.LPStr)] public string Nonce;
            [MarshalAs(UnmanagedType.LPStr)] public string ExpirationTimeStamp;
            [MarshalAs(UnmanagedType.LPStr)] public string FeeToken;
            [MarshalAs(UnmanagedType.LPStr)] public string FeeVaultId;
            [MarshalAs(UnmanagedType.LPStr)] public string FeeLimit;
        }
        [DllImport("reddio", EntryPoint = "get_limit_order_msg_hash_with_fee")]
        private static extern int GetLimitOrderMsgHashWithFee(
            LimitOrderMsgWithFee msg,
            [MarshalAs(UnmanagedType.LPStr)] StringBuilder hash
        );


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
            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(privateKeyStr.ToString());
        }

        public static BigInteger GetRandomPrivateKey()
        {
            var privateKeyStr = new StringBuilder(BIG_INT_BUFFER_SIZE);

            var errno = GetRandomPrivateKeyImpl(privateKeyStr);
            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(privateKeyStr.ToString());
        }


        public static (BigInteger, BigInteger) Sign(BigInteger privateKey, BigInteger msgHash, BigInteger? seed)
        {
            var privateKeyStr = privateKey.ToString("x");
            var msgHashStr = msgHash.ToString("x");
            string? seedStr = seed?.ToString("x");

            var doc = new SignDocument
            {
                privateKey = privateKeyStr,
                msgHash = msgHashStr,
                seed = seedStr,
            };
            var result = new SignResult
            {
                r = Marshal.AllocHGlobal(BIG_INT_BUFFER_SIZE),
                s = Marshal.AllocHGlobal(BIG_INT_BUFFER_SIZE),
            };

            var errno = SignImpl(doc, result);
            if (errno != 0)
            {
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

            var signature = new Signature
            {
                publicKey = publicKeyStr,
                msgHash = msgHashStr,
                r = rStr,
                s = sStr,
            };
            var errno = VerifyImpl(signature, ref verified);
            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }
            return verified;
        }

        public static BigInteger GetPublicKey(BigInteger privateKey)
        {
            var privateKeyStr = privateKey.ToString("x");
            var publicKeyStr = new StringBuilder(BIG_INT_BUFFER_SIZE);
            var errno = GetPublicKey(privateKeyStr, publicKeyStr);
            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }
            return ParsePositive(publicKeyStr.ToString());
        }

        public static BigInteger GetTransferMsgHash(
            Int64 amount,
            Int64 nonce,
            Int64 senderVaultId,
            BigInteger token,
            Int64 receiverVaultId,
            BigInteger receiverPublicKey,
            Int64 expirationTimeStamp,
            BigInteger? condition
        )
        {
            var hash = new StringBuilder(BIG_INT_BUFFER_SIZE);
            var msg = new TransferMsg();
            msg.Amount = amount.ToString();
            msg.Nonce = nonce.ToString();
            msg.SenderVaultId = senderVaultId.ToString();
            msg.Token = token.ToString("x");
            msg.ReceiverVaultId = receiverVaultId.ToString();
            msg.ReceiverPublicKey = receiverPublicKey.ToString("x");
            msg.ExpirationTimeStamp = expirationTimeStamp.ToString();
            if (condition != null)
            {
                msg.Condition = condition.Value.ToString("x");
            }
            var errno = GetTransferMsgHash(msg, hash);

            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(hash.ToString());
        }


        public static BigInteger GetTransferMsgHashWithFee(
            Int64 amount,
            Int64 nonce,
            Int64 senderVaultId,
            BigInteger token,
            Int64 receiverVaultId,
            BigInteger receiverStarkKey,
            Int64 expirationTimeStamp,
            BigInteger feeToken,
            Int64 feeVaultId,
            Int64 feeLimit,
            BigInteger? condition
        )
        {
            var hash = new StringBuilder(BIG_INT_BUFFER_SIZE);
            var msg = new TransferMsgWithFee();
            msg.Amount = amount.ToString();
            msg.Nonce = nonce.ToString();
            msg.SenderVaultId = senderVaultId.ToString();
            msg.Token = token.ToString("x");
            msg.ReceiverVaultId = receiverVaultId.ToString();
            msg.ReceiverStarkKey = receiverStarkKey.ToString("x");
            msg.ExpirationTimeStamp = expirationTimeStamp.ToString();
            msg.FeeToken = feeToken.ToString("x");
            msg.FeeVaultId = feeVaultId.ToString();
            msg.FeeLimit = feeLimit.ToString();

            if (condition != null)
            {
                msg.Condition = condition.Value.ToString("x");
            }
            var errno = GetTransferMsgHashWithFee(msg, hash);

            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(hash.ToString());
        }


        public static BigInteger GetLimitOrderMsgHash(
            Int64 vaultSell,
            Int64 vaultBuy,
            Int64 amountSell,
            Int64 amountBuy,
            BigInteger tokenSell,
            BigInteger tokenBuy,
            Int64 nonce,
            Int64 expirationTimeStamp
        )
        {
            var hash = new StringBuilder(BIG_INT_BUFFER_SIZE);
            var msg = new LimitOrderMsg();

            msg.VaultSell = vaultSell.ToString();
            msg.VaultBuy = vaultBuy.ToString();
            msg.AmountSell = amountSell.ToString();
            msg.AmountBuy = amountBuy.ToString();
            msg.TokenSell = tokenSell.ToString("x");
            msg.TokenBuy = tokenBuy.ToString("x");
            msg.Nonce = nonce.ToString();
            msg.ExpirationTimeStamp = expirationTimeStamp.ToString();

            var errno = GetLimitOrderMsgHash(msg, hash);

            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(hash.ToString());
        }
        public static BigInteger GetLimitOrderMsgHashWithFee(
            Int64 vaultSell,
            Int64 vaultBuy,
            Int64 amountSell,
            Int64 amountBuy,
            BigInteger tokenSell,
            BigInteger tokenBuy,
            Int64 nonce,
            Int64 expirationTimeStamp,
            BigInteger feeToken,
            BigInteger feeVaultId,
            BigInteger feeLimit
        )
        {
            var hash = new StringBuilder(BIG_INT_BUFFER_SIZE);
            var msg = new LimitOrderMsgWithFee();

            msg.VaultSell = vaultSell.ToString();
            msg.VaultBuy = vaultBuy.ToString();
            msg.AmountSell = amountSell.ToString();
            msg.AmountBuy = amountBuy.ToString();
            msg.TokenSell = tokenSell.ToString("x");
            msg.TokenBuy = tokenBuy.ToString("x");
            msg.Nonce = nonce.ToString();
            msg.ExpirationTimeStamp = expirationTimeStamp.ToString();
            msg.FeeToken = feeToken.ToString("x");
            msg.FeeVaultId = feeVaultId.ToString();
            msg.FeeLimit = feeLimit.ToString();

            var errno = GetLimitOrderMsgHashWithFee(msg, hash);

            if (errno != 0)
            {
                throw new CryptoException(ExplainError(errno));
            }

            return ParsePositive(hash.ToString());
        }
    }
}
