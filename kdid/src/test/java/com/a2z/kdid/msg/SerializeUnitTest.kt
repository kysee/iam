package com.a2z.kdid.msg

import com.a2z.kchainlib.common.Tools
import com.a2z.kchainlib.common.toHex
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SerializeUnitTest {

    @ImplicitReflectionSerializer
    @Test
    fun kdiddoc_json_serialization() {
        val subjectId =
            KDID("kchain", Tools.randBytes(20).toHex())
        val subject = KDIDSubject(
            "Yongseok Kwon",
            "M",
            "19741205",
            "01033334444",
            "7412050000000"
        )

        val pubKeyId =
            KDID("kchain", Tools.randBytes(20).toHex())
        val controllerId =
            KDID("kchain", Tools.randBytes(20).toHex())
        val pubKey = KDIDPubKey(
            id = pubKeyId,
            type = "Ed25519VerificationKey2018",
            controller = controllerId,
            publicKeyJwk = KDIDPubKey.Jwk(
                "ed25519",
                Tools.randBytes(32),
                "kty",
                pubKeyId.did()
            )
        )

        val kdiddoc = KDIDDoc(
            subjectId,
            pubKey,
            subject
        )

        val json = kdiddoc.encode<KDIDDoc>()
        val kdiddoc2 = KDIDObject.decode<KDIDDoc>(json)

        assertEquals(kdiddoc, kdiddoc2)
    }
}
