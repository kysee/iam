package com.a2z.kdid

import com.google.crypto.tink.subtle.Hex
import kotlinx.serialization.*

class PubKeyX {
}

@Serializable
data class PubKeyPem(
    val material: ByteArray
) : KDIDObject<PubKeyPem>() {

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

    @Serializer(forClass = PubKeyPem::class)
    companion object : KSerializer<PubKeyPem> {

        override val descriptor: SerialDescriptor =
            PrimitiveDescriptor("PubKeyPem", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, obj: PubKeyPem) {
            encoder.encodeString(Hex.encode(obj.material))
        }

        override fun deserialize(decoder: Decoder): PubKeyPem {
            val hex = decoder.decodeString()
            return PubKeyPem(Hex.decode(hex))
        }
    }
}

@Serializable
data class PubKeyJwk(
    val crv: String,
    val material: ByteArray,
    val kty: String,
    val kid: String
) : KDIDObject<PubKeyJwk>() {

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

    @Serializer(forClass = PubKeyJwk::class)
    companion object : KSerializer<PubKeyJwk> {
        @ImplicitReflectionSerializer
        override val descriptor: SerialDescriptor = SerialDescriptor("publicKeyJwk") {
            element<String>("crv")
            element<ByteArray>("x")
            element<String>("kty")
            element<String>("kid")
        }

        @ImplicitReflectionSerializer
        override fun serialize(encoder: Encoder, value: PubKeyJwk) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeStringElement(descriptor, 0, value.crv)
            compositeOutput.encodeStringElement(descriptor, 1, Hex.encode(value.material))
            compositeOutput.encodeStringElement(descriptor, 2, value.kty)
            compositeOutput.encodeStringElement(descriptor, 3, value.kid)
            compositeOutput.endStructure(descriptor)
        }

        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): PubKeyJwk {
            lateinit var crv: String
            lateinit var xhex: String
            lateinit var kty: String
            lateinit var kid: String

            val dec: CompositeDecoder = decoder.beginStructure(descriptor)

            loop@ while (true) {
                when (val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> crv = dec.decodeStringElement(descriptor, i)
                    1 -> xhex = dec.decodeStringElement(descriptor, i)
                    2 -> kty = dec.decodeStringElement(descriptor, i)
                    3 -> kid = dec.decodeStringElement(descriptor, i)
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return PubKeyJwk(
                crv,
                Hex.decode(xhex),
                kty,
                kid
            )
        }
    }
}
