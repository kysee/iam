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
        val subjectId = KDID("kchain", toHex(randBytes(20)))
        val subject = KDIDSubject(
            "Yongseok Kwon",
            "M",
            "19741205",
            "01033334444",
            "7412050000000"
        )

        val pubKeyId = KDID("kchain", toHex(randBytes(20)))
        val controllerId = KDID("kchain", toHex(randBytes(20)))
        val pubKey = KDIDPubKey(
            id = pubKeyId,
            controller = controllerId,
            //PubKeyPem(randBytes(32))
            publicKeyJwk = PubKeyJwk("ed25519", randBytes(32), "kty", pubKeyId.did())
        )

        val kdiddoc = KDIDDoc(
            subjectId,
            pubKey,
            subject
        )

        val json = kdiddoc.encode<KDIDDoc>()
        println(json)

        val kdiddoc2 = KDIDObject.decode<KDIDDoc>(json)
        println(kdiddoc2)
        assertEquals(kdiddoc, kdiddoc2)
    }
}
