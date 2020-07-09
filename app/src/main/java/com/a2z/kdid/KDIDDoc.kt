package com.a2z.kdid

import com.google.crypto.tink.subtle.Hex
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

open class KDIDObject<T> {
    @ImplicitReflectionSerializer
    inline fun <reified T> encode(context: SerialModule = EmptyModule): String {
        var conf: JsonConfiguration = JsonConfiguration.Stable

        return Json(conf, context).stringify(serializer(), this as T)
    }

    companion object {
        @ImplicitReflectionSerializer
        inline fun <reified T> decode(d: String, context: SerialModule = EmptyModule) : T {
            return Json(JsonConfiguration.Stable, context).parse(serializer(), d)
        }
    }
}


@Serializable
data class KDID (
    val method: String,
    val idstring: String
) : KDIDObject<KDID>() {
    public fun did(): String {
        return "did:$method:$idstring"
    }

    @Serializer(forClass = KDID::class)
    companion object : KSerializer<KDID> {
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

@Serializable
data class KDIDSubject (
    val name: String,
    val gender: String,
    val birthday: String,
    val phoneNum: String,
    val socialNum: String
) : KDIDObject<KDIDSubject>()


@Serializable
data class KDIDDoc (
    val id: KDID,
    val publicKey: KDIDPubKey,
    val subject: KDIDSubject
) : KDIDObject<KDIDDoc>()

@Serializable
data class KDIDPubKey (
    val id: KDID,
    val type: String,
    val controller: KDID,
    @SerialName("PubKeyPem")
    val publicKeyX: PubKeyMaterial
) : KDIDObject<KDIDPubKey>()


interface PubKeyMaterial {
    val material: ByteArray
    fun toFormatted(): ByteArray
}


data class PubKeyPem(
    override val material: ByteArray
) : PubKeyMaterial, KDIDObject<PubKeyPem>() {

    override fun toFormatted(): ByteArray {
        return material
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PubKeyPem

        if (!material.contentEquals(other.material)) return false

        return true
    }

    override fun hashCode(): Int {
        return material.contentHashCode()
    }
}

@Serializer(forClass = PubKeyPem::class)
object PubKeyPemSerializer : KSerializer<PubKeyPem> {

//    override val descriptor: SerialDescriptor =
//        PrimitiveDescriptor("PubKeyPem", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: PubKeyPem) {
        encoder.encodeString(Hex.encode(obj.material))
    }

    override fun deserialize(decoder: Decoder): PubKeyPem {
        val hex = decoder.decodeString()
        return PubKeyPem(Hex.decode(hex))
    }
}


data class PubKeyJwk(
    val crv: String,
    @SerialName("x") override val material: ByteArray,
    val kty: String,
    val kid: String
) : PubKeyMaterial, KDIDObject<PubKeyJwk>() {

    override fun toFormatted(): ByteArray {
        return material
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PubKeyJwk

        if (!material.contentEquals(other.material)) return false

        return true
    }

    override fun hashCode(): Int {
        return material.contentHashCode()
    }
}
