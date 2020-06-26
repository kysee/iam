package com.example.libkchain

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.protobuf.ProtoId
import java.math.BigInteger
import java.util.*
import kotlinx.serialization.KSerializer as KSerializer1

@Serializable
data class TrxTransferProto (
    @ProtoId(1) val amount: ByteArray,
    @ProtoId(2) val note: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxTransferProto

        if (!amount.contentEquals(other.amount)) return false
        if (!note.contentEquals(other.note)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.contentHashCode()
        result = 31 * result + note.contentHashCode()
        return result
    }
}

@Serializable
data class TrxTransferProtoEx(
    @ProtoId(1) @Serializable(with=BigIntegerSerializer::class) val amount: BigInteger,
    @ProtoId(2) val note: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxTransferProtoEx

        if (amount != other.amount) return false
        if (!note.contentEquals(other.note)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + note.contentHashCode()
        return result
    }
}

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer: KSerializer1<BigInteger> {
    override fun serialize(encoder: Encoder, obj: BigInteger) {
        val bz = obj.toByteArray().toList()
        ListSerializer(Byte.serializer()).serialize(encoder, bz)
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        val bz = decoder.decodeSerializableValue(ListSerializer(Byte.serializer())).toByteArray()
        val bigInt = BigInteger(bz)
        return bigInt
    }
}
