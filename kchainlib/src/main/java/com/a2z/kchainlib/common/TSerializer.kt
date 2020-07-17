package com.a2z.kchainlib.common

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ByteArraySerializer
import java.math.BigInteger


//@Serializer(forClass = ByteArray::class)
object THexSerializer : KSerializer<ByteArray> {
    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.encodeString(value.toHex())
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        return decoder.decodeString().hexToByteArray()
    }

    override val descriptor: SerialDescriptor = PrimitiveDescriptor("hex", PrimitiveKind.STRING)
}

//@Serializer(forClass = ByteArray::class)
object TBase64Serializer : KSerializer<ByteArray> {
    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.encodeString(
            com.google.crypto.tink.subtle.Base64.encode(value)
        )
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        return com.google.crypto.tink.subtle.Base64.decode(
            decoder.decodeString()
        )
    }

    override val descriptor: SerialDescriptor = PrimitiveDescriptor("base64", PrimitiveKind.STRING)
}

@Serializer(forClass = BigInteger::class)
object TBigIntegerSerializer: KSerializer<BigInteger> {
    override fun serialize(encoder: Encoder, value: BigInteger) {
        val bz = value.toByteArray()
        encoder.encodeSerializableValue(ByteArraySerializer(), bz)
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        val bz = decoder.decodeSerializableValue(ByteArraySerializer())
        val bigInt = BigInteger(bz)
        return bigInt
    }
}
