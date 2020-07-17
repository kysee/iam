package com.a2z.kdid.msg

import com.a2z.kchainlib.crypto.TRawKeyPairED25519
import kotlinx.serialization.*

@Serializable
data class KDIDDoc (
    val id: KDID,
    val publicKey: KDIDPubKey,
    val subject: KDIDSubject
) : KDIDObject<KDIDDoc>()
{
    companion object {
        fun create(
            name: String,
            gender: String,
            birthday: String,
            phoneNum: String,
            socialNum: String
        ): KDIDDoc {
            val keyPair = TRawKeyPairED25519.createKeyPair()

            val did = KDID.create(keyPair.pub)

            return KDIDDoc(
                did,
                KDIDPubKey(
                    did,
                    did,
                    keyPair.type,
                    KDIDPubKey.HexKey(keyPair.pub)
                ),
                KDIDSubject(name, gender, birthday, phoneNum, socialNum)
            )
        }
    }
}

