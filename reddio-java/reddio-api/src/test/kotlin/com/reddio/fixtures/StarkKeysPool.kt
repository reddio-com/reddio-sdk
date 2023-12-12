package com.reddio.fixtures

import com.reddio.api.v1.DefaultEthereumInteraction
import com.reddio.api.v1.StarkKeys
import com.reddio.crypto.CryptoService
import org.web3j.crypto.Credentials
import java.util.stream.Collectors

/**
 * StarkKeysPool return the usable StarkKeys for testing.
 */
class StarkKeysPool {
    companion object {

        /**
         * Return ths stark private key to sign the payment.
         *
         * From env variable INTEGRATION_TEST_PAYMENT_SIGNER_STARK_PRIVATE_KEY.
         */
        fun paymentSignerStarkPrivateKey(): String {
            val paymentSignerStarkPrivateKey = System.getenv("INTEGRATION_TEST_PAYMENT_SIGNER_STARK_PRIVATE_KEY")
            if (!paymentSignerStarkPrivateKey.isNullOrBlank()) {
                return paymentSignerStarkPrivateKey
            }
            throw FixtureException("The environment variable INTEGRATION_TEST_PAYMENT_SIGNER_STARK_PRIVATE_KEY is not set.")
        }

        /**
         * Generates a random StarkKeys pair.
         */
        fun randomStarkKeys(): StarkKeys {
            val privateKey = CryptoService.getRandomPrivateKey()
            val publicKey = CryptoService.getPublicKey(privateKey)
            return StarkKeys.of("0x" + publicKey.toString(16), "0x" + privateKey.toString(16))
        }

        /**
         * Return the list of StarkKeys from the environment variable.
         *
         * The environment variable INTEGRATION_TEST_ETH_PRIVATE_KEYS should be a comma separated list of stark private keys which own the test assets;
         */
        fun pool(): List<EthAndStarkKeys> {
            val ethPrivateKeys = System.getenv("INTEGRATION_TEST_ETH_PRIVATE_KEYS")
            if (!ethPrivateKeys.isNullOrBlank()) {
                return ethPrivateKeys.split(",").parallelStream().map { ethPrivateKeys ->
                    ethPrivateKeys.trim()
                }.map { ethPrivateKey ->
                    val credential = Credentials.create(ethPrivateKey)
                    Pair(credential.address, ethPrivateKey)
                }.map { pair ->
                    val ethAddress = pair.first
                    val ethPrivateKey = pair.second
                    val starkKeyPair =
                        DefaultEthereumInteraction.getStarkKeys(ethPrivateKey, DefaultEthereumInteraction.SEPOLIA_ID)
                    EthAndStarkKeys(ethAddress, ethPrivateKey, starkKeyPair.starkKey, starkKeyPair.starkPrivateKey)
                }.collect(Collectors.toList())
            }
            throw FixtureException("The environment variable INTEGRATION_TEST_ETH_PRIVATE_KEYS is not set.")
        }

        /**
         * Return one starkKey from the pool, but exclude the given stark keys.
         */
        fun starkKeysFromPoolButExpect(vararg starkKey: String): EthAndStarkKeys {
            starkKey.toList().forEach {
                if (!it.startsWith("0x")) {
                    throw FixtureException("The starkKey should start with 0x, but got $it")
                }
            }

            val exclude = starkKey.toSet()
            return this.pool().stream().filter { !exclude.contains(it.starkKey) }.findAny().orElseThrow {
                FixtureException("No starkKey found in the pool, exclude ${starkKey.toList()}")
            }
        }
    }
}

data class EthAndStarkKeys(
    val ethAddress: String,
    val ethPrivateKey: String,
    val starkKey: String,
    val starkPrivateKey: String
)