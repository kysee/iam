package com.a2z.kchainlib.kdid

import com.google.crypto.tink.subtle.Hex
import kotlinx.serialization.*

@Serializable
data class KDIDPubKey (
    val id: KDID,
    val controller: KDID,
    val type: String,
    val publicKeyPem: HexKey? = null,
    val publicKeyJwk: Jwk? = null
    ) : KDIDObject<KDIDPubKey>() {

    @Serializer(forClass = KDIDPubKey::class)
    companion object : KSerializer<KDIDPubKey> {
        @ImplicitReflectionSerializer
        override val descriptor: SerialDescriptor = SerialDescriptor("publicKey") {
            element<KDID>("id")
            element<KDID>("controller")
            element<String>("type")
            element<HexKey>("publicKeyPem")
            element<Jwk>("publicKeyJwk")
         }

        @ImplicitReflectionSerializer
        override fun serialize(encoder: Encoder, value: KDIDPubKey) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, KDID.serializer(), value.id)
            compositeOutput.encodeSerializableElement(descriptor, 1, KDID.serializer(), value.controller)
            compositeOutput.encodeStringElement(descriptor, 2, value.type)
            if (value.publicKeyPem != null) {
                compositeOutput.encodeSerializableElement(descriptor, 3, HexKey.serializer(), value.publicKeyPem)
            }
            else if (value.publicKeyJwk != null) {
                compositeOutput.encodeSerializableElement(descriptor, 4, Jwk.serializer(), value.publicKeyJwk)
            }
            compositeOutput.endStructure(descriptor)
        }

        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): KDIDPubKey {
            lateinit var id: KDID
            lateinit var controller: KDID
            lateinit var type: String
            var hexKey: HexKey? = null
            var jwk: Jwk? = null

            val dec: CompositeDecoder = decoder.beginStructure(descriptor)

            loop@ while (true) {
                when (val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> id = dec.decodeSerializableElement(descriptor, i, KDID.serializer())
                    1 -> controller = dec.decodeSerializableElement(descriptor, i, KDID.serializer())
                    2 -> type = dec.decodeStringElement(descriptor, i)
                    3 -> hexKey = dec.decodeSerializableElement(descriptor, i, HexKey.serializer())
                    4 -> jwk = dec.decodeSerializableElement(descriptor, i, Jwk.serializer())
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return KDIDPubKey(
                id ?: throw MissingFieldException("id"),
                controller ?: throw MissingFieldException("controller"),
                type,
                hexKey,
                jwk
            )
        }
    }


    @Serializable
    data class HexKey(
        val material: ByteArray
    ) : KDIDObject<HexKey>() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as HexKey

            if (!material.contentEquals(other.material)) return false

            return true
        }

        override fun hashCode(): Int {
            return material.contentHashCode()
        }

        @Serializer(forClass = HexKey::class)
        companion object : KSerializer<HexKey> {

            override val descriptor: SerialDescriptor =
                PrimitiveDescriptor("PubKeyPem", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, obj: HexKey) {
                encoder.encodeString(Hex.encode(obj.material))
            }

            override fun deserialize(decoder: Decoder): HexKey {
                val hex = decoder.decodeString()
                return HexKey(Hex.decode(hex))
            }
        }
    }

    @Serializable
    data class Jwk(
        val crv: String,
        val material: ByteArray,
        val kty: String,
        val kid: String
    ) : KDIDObject<Jwk>() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Jwk

            if (!material.contentEquals(other.material)) return false

            return true
        }

        override fun hashCode(): Int {
            return material.contentHashCode()
        }

        @Serializer(forClass = Jwk::class)
        companion object : KSerializer<Jwk> {
            @ImplicitReflectionSerializer
            override val descriptor: SerialDescriptor = SerialDescriptor("publicKeyJwk") {
                element<String>("crv")
                element<ByteArray>("x")
                element<String>("kty")
                element<String>("kid")
            }

            @ImplicitReflectionSerializer
            override fun serialize(encoder: Encoder, value: Jwk) {
                val compositeOutput = encoder.beginStructure(descriptor)
                compositeOutput.encodeStringElement(descriptor, 0, value.crv)
                compositeOutput.encodeStringElement(descriptor, 1, Hex.encode(value.material))
                compositeOutput.encodeStringElement(descriptor, 2, value.kty)
                compositeOutput.encodeStringElement(descriptor, 3, value.kid)
                compositeOutput.endStructure(descriptor)
            }

            @ImplicitReflectionSerializer
            override fun deserialize(decoder: Decoder): Jwk {
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
                return Jwk(
                    crv,
                    Hex.decode(xhex),
                    kty,
                    kid
                )
            }
        }
    }

}
