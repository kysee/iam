package com.a2z.kdid.msg

import kotlinx.serialization.*

@Serializable
data class KDID (
    val method: String,
    val idstring: String
) : KDIDObject<KDID>() {
    fun did(): String {
        return "did:$method:$idstring"
    }

    @Serializer(forClass = KDID::class)
    companion object :
        KSerializer<KDID> {
        override fun serialize(encoder: Encoder, obj: KDID) {
            encoder.encodeString(obj.did())
        }

        override fun deserialize(decoder: Decoder): KDID {
            val str = decoder.decodeString()
            val arr = str.split(":")
            return KDID(arr[1], arr[2])
        }
    }
}