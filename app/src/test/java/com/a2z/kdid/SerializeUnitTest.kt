package com.a2z.kdid

import com.a2z.kchainlib.tools.randBytes
import com.a2z.kchainlib.tools.toHex
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SerializeUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @ImplicitReflectionSerializer
    @Test
    fun json_serializer() {
        val subjectId = KDID("kchain", toHex(randBytes(32)))
        val controllerId = KDID("kchain", toHex(randBytes(32)))
        val pubKey = KDIDPubKey(
            subjectId,
            "pubkeytype",
            controllerId,
            PubKeyPem(randBytes(32))
        )



        val pubKeyMaterialModule = SerializersModule { // 1
            polymorphic(PubKeyMaterial::class) { // 2
                PubKeyJwk::class with PubKeyJwk.serializer() // 3
                PubKeyPem::class with PubKeyPem.serializer() // 4
            }
        }
        println(pubKey.encode<KDIDPubKey>(pubKeyMaterialModule))
    }
}