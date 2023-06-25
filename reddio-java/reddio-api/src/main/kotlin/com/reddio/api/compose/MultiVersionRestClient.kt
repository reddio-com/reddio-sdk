package com.reddio.api.compose

import com.reddio.api.v1.rest.DefaultReddioRestClient as V1ReddioClient
import com.reddio.api.v3.rest.DefaultReddioRestClient as V3ReddioClient

class MultiVersionRestClient {
    companion object {
        @JvmStatic
        fun v1(): V1Builder {
            return V1Builder
        }

        @JvmStatic
        fun v3(): V3Builder {
            return V3Builder
        }

        final object V1Builder {
            fun mainnet(): V1ReddioClient {
                return V1ReddioClient(V1ReddioClient.MAINNET_API_ENDPOINT)
            }

            fun mainnet(apiKey: String): V1ReddioClient {
                return V1ReddioClient(V1ReddioClient.MAINNET_API_ENDPOINT, apiKey)
            }

            fun testnet(): V1ReddioClient {
                return V1ReddioClient(V1ReddioClient.TESTNET_API_ENDPOINT)
            }

            fun testnet(apiKey: String): V1ReddioClient {
                return V1ReddioClient(V1ReddioClient.TESTNET_API_ENDPOINT, apiKey)
            }
        }

        final object V3Builder {
            fun mainnet(): V3ReddioClient {
                return V3ReddioClient(V3ReddioClient.MAINNET_API_ENDPOINT)
            }

            fun mainnet(apiKey: String): V3ReddioClient {
                return V3ReddioClient(V3ReddioClient.MAINNET_API_ENDPOINT, apiKey)
            }

            fun testnet(): V3ReddioClient {
                return V3ReddioClient(V3ReddioClient.TESTNET_API_ENDPOINT)
            }

            fun testnet(apiKey: String): V3ReddioClient {
                return V3ReddioClient(V3ReddioClient.TESTNET_API_ENDPOINT, apiKey)
            }

        }
    }
}