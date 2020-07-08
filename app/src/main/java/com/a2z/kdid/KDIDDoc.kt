@file:UseSerializers(PubKeyPemSerializer::class)

package com.a2z.kdid

import com.a2z.kchainlib.BigIntegerSerializer
import com.a2z.kchainlib.tools.toHex
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoBuf
import java.math.BigInteger
import java.security.MessageDigest

open class KDIDObject<T> {
    @ImplicitReflectionSerializer
    inline fun <reified T> encode(context: SerialModule = EmptyModule): String {
        return Json(JsonConfiguration.Stable, context).stringify(serializer(), this as T)
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
    fun did(): String {
        return "did:$method:$idstring"
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
    val publicKeyX: PubKeyMaterial
) : KDIDObject<KDIDPubKey>()

interface PubKeyMaterial {
    val material: ByteArray
    fun toFormatted(): ByteArray
}

@Serializable(with = PubKeyPemSerializer::class)
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
object PubKeyPemSerializer: KSerializer<PubKeyPem> {

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("publicKeyPem", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: PubKeyPem) {
        encoder.encodeSerializableValue(ByteArraySerializer(), obj.material)
    }

    override fun deserialize(decoder: Decoder): PubKeyPem {
        val bz = decoder.decodeSerializableValue(ByteArraySerializer())
        return PubKeyPem(bz)
    }
}

@Serializable
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
