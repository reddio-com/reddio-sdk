package com.reddio.fixtures

import com.reddio.api.v1.StarkKeys
import com.reddio.crypto.CryptoService

/**
 * StarkKeysPool return the usable StarkKeys for testing.
 */
class StarkKeysPool {
    companion object {

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
         * The environment variable STARK_PRIVATE_KEYS should be a comma separated list of stark private keys which own the test assets;
         */
        fun pool(): List<StarkKeys> {
            val starkKeys = System.getenv("STARK_PRIVATE_KEYS")
            if (starkKeys != null) {
                return starkKeys.split(",").map { privateKey ->
                    val publicKey = CryptoService.getPublicKey(privateKey.replace("0x", "").toBigInteger(16))
                    StarkKeys.of("0x" + publicKey.toString(16), "0x" + privateKey.replace("0x", ""))
                }
            }
            throw FixtureException("The environment variable STARK_PRIVATE_KEYS is not set.")
        }

        /**
         * Return one starkKey from the pool, but exclude the given stark keys.
         */
        fun starkKeysFromPoolButExpect(vararg starkKey: String): StarkKeys {
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