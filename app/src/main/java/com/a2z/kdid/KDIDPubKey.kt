package com.a2z.kdid

import com.google.crypto.tink.subtle.Hex
import kotlinx.serialization.*

@Serializable
data class KDIDPubKey (
    val id: KDID,
    val controller: KDID,
    val publicKeyPem: PubKeyPem? = null,
    val publicKeyJwk: PubKeyJwk? = null
    ) : KDIDObject<KDIDPubKey>() {

    @Serializer(forClass = KDIDPubKey::class)
    companion object : KSerializer<KDIDPubKey> {
        @ImplicitReflectionSerializer
        override val descriptor: SerialDescriptor = SerialDescriptor("publicKey") {
            element<KDID>("id")
            element<KDID>("controller")
            element<String>("type")
            element<PubKeyPem>("publicKeyPem")
            element<PubKeyJwk>("publicKeyJwk")
         }

        @ImplicitReflectionSerializer
        override fun serialize(encoder: Encoder, value: KDIDPubKey) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, KDID.serializer(), value.id)
            compositeOutput.encodeSerializableElement(descriptor, 1, KDID.serializer(), value.controller)
            if (value.publicKeyPem != null) {
                compositeOutput.encodeStringElement(descriptor, 2, "PubKeyPem")
                compositeOutput.encodeSerializableElement(descriptor, 3, PubKeyPem.serializer(), value.publicKeyPem)
            }
            else if (value.publicKeyJwk != null) {
                compositeOutput.encodeStringElement(descriptor, 2, "PubKeyJwk")
                compositeOutput.encodeSerializableElement(descriptor, 4, PubKeyJwk.serializer(), value.publicKeyJwk)
            }
            compositeOutput.endStructure(descriptor)
        }

        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): KDIDPubKey {
            lateinit var id: KDID
            lateinit var controller: KDID
            lateinit var type: String
            var pubKeyPem: PubKeyPem? = null
            var pubKeyJwk: PubKeyJwk? = null

            val dec: CompositeDecoder = decoder.beginStructure(descriptor)

            loop@ while (true) {
                when (val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> id = dec.decodeSerializableElement(descriptor, i, KDID.serializer())
                    1 -> controller = dec.decodeSerializableElement(descriptor, i, KDID.serializer())
                    2 -> type = dec.decodeStringElement(descriptor, i)
                    3 -> pubKeyPem = dec.decodeSerializableElement(descriptor, i, PubKeyPem.serializer())
                    4 -> pubKeyJwk = dec.decodeSerializableElement(descriptor, i, PubKeyJwk.serializer())
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return KDIDPubKey(
                id ?: throw MissingFieldException("id"),
                controller ?: throw MissingFieldException("controller"),
                pubKeyPem,
                pubKeyJwk
            )
        }
    }
}
