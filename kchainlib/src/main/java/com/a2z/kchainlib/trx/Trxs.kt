@file:UseSerializers(BigIntegerSerializer::class)
package com.a2z.kchainlib.trx

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.protobuf.ProtoId
import java.math.BigInteger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf

/*
message TrxProto {
  int64 nonce = 1;
  int64 time = 2;
  bytes sender = 3;
  bytes receiver = 4;
  bytes _gas = 5;
  int32 action = 6;
  bytes _payload = 7;
  bytes _pub_key = 8;
  bytes _sig = 9;
}
 */

@Serializable
data class Transaction (
    @ProtoId(1) val nonce: Long,
    @ProtoId(2) val time: Long,
    @ProtoId(3) val sender: ByteArray,
    @ProtoId(4) val receiver: ByteArray,
    @ProtoId(5) val gas: BigInteger,
    @ProtoId(6) val action: Int,
    @ProtoId(7) val payload: ByteArray,
    @ProtoId(8) val pubKey: ByteArray,
    @ProtoId(9) var sig: ByteArray? = null
) {

    companion object {
        const val ACTION_TRANSFER = 1
        const val ACTION_STAKING = 2
        const val ACTION_UNSTAKING = 3
        const val ACTION_GOVPROPOSAL = 4
        const val ACTION_GOVVOTE = 5
        const val ACTION_DATACREATE = 6
        const val ACTION_DATAUPDATE = 7
        const val ACTION_DATAVERIFY = 8

        fun decode(d: ByteArray): Transaction {
            return ProtoBuf().load(serializer(), d)
        }
    }

    fun encode(): ByteArray {
        return ProtoBuf(false).dump(serializer(), this)
    }

    @ImplicitReflectionSerializer
    inline fun <reified T> getPayloadObject(): T? {
        when {
            action == ACTION_TRANSFER && T::class != TrxTransfer::class  -> {
                return null
            }
            action == ACTION_DATACREATE && T::class != TrxDataCreate::class -> {
                return null
            }
        }
        return TrxPayload.decode<T>(this.payload)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (nonce != other.nonce) return false
        if (time != other.time) return false
        if (!sender.contentEquals(other.sender)) return false
        if (!receiver.contentEquals(other.receiver)) return false
        if (gas != other.gas) return false
        if (action != other.action) return false
        if (!payload.contentEquals(other.payload)) return false
        if (!pubKey.contentEquals(other.pubKey)) return false
        if (sig != null) {
            if (other.sig == null) return false
            if (!sig!!.contentEquals(other.sig!!)) return false
        } else if (other.sig != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nonce.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + sender.contentHashCode()
        result = 31 * result + receiver.contentHashCode()
        result = 31 * result + gas.hashCode()
        result = 31 * result + action
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + pubKey.contentHashCode()
        result = 31 * result + (sig?.contentHashCode() ?: 0)
        return result
    }

}

@Serializable
open class TrxPayload<T> {
    @ImplicitReflectionSerializer
    inline fun <reified T> encode(): ByteArray {
        return ProtoBuf(false).dump<T>(serializer(), this as T)
    }

    companion object {
        @ImplicitReflectionSerializer
        inline fun <reified T> decode(d: ByteArray) : T {
            return ProtoBuf().load<T>(serializer(), d)
        }
    }
}

@Serializable
data class TrxTransfer (
    @ProtoId(1) val amount: BigInteger,
    @ProtoId(2) val note: ByteArray
) : TrxPayload<TrxTransfer>() {
    constructor() : this(BigInteger.ZERO, ByteArray(0)) {}


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxTransfer

        if (amount.equals(other)) return false
        if (!note.contentEquals(other.note)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + note.contentHashCode()
        return result
    }


}

@Serializable
data class TrxStake (
    @ProtoId(1) val amount: BigInteger,
    @ProtoId(2) val note: ByteArray
) : TrxPayload<TrxStake>() {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxStake

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

@Serializable
data class TrxUnstake (
    @ProtoId(1) val amount: BigInteger,
    @ProtoId(2) val note: ByteArray
) : TrxPayload<TrxUnstake>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxUnstake

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

/*
message TrxDataCreateProto {
  bytes text = 1;
  repeated bytes _authors = 2;
  int64 expiry_height = 3;
}
*/

@Serializable
data class TrxDataCreate (
    @ProtoId(1) val text: ByteArray,
    @ProtoId(2) val authors: Array<ByteArray>,
    @ProtoId(3) val expiryHeight: Long = 0
) : TrxPayload<TrxDataCreate>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxDataCreate

        if (!text.contentEquals(other.text)) return false
        if (!authors.contentDeepEquals(other.authors)) return false
        if (expiryHeight != other.expiryHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.contentHashCode()
        result = 31 * result + authors.contentDeepHashCode()
        result = 31 * result + expiryHeight.hashCode()
        return result
    }

}

/*
message TrxDataUpdateProto {
  int64 nonce = 1;
  bytes new_text = 2;
  bytes new_owner = 3;
	repeated bytes _new_authors = 4;
  int64 new_expiry_height = 5;
}
*/

@Serializable
data class TrxDataUpdate (
    @ProtoId(1) val nonce: Long,
    @ProtoId(2) val text: ByteArray,
    @ProtoId(3) val owner: ByteArray,
    @ProtoId(4) val authors: List<ByteArray>,
    @ProtoId(5) val expiryHeight: Long
) : TrxPayload<TrxDataUpdate>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrxDataUpdate

        if (nonce != other.nonce) return false
        if (!text.contentEquals(other.text)) return false
        if (!owner.contentEquals(other.owner)) return false
        if (authors != other.authors) return false
        if (expiryHeight != other.expiryHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nonce.hashCode()
        result = 31 * result + text.contentHashCode()
        result = 31 * result + owner.contentHashCode()
        result = 31 * result + authors.hashCode()
        result = 31 * result + expiryHeight.hashCode()
        return result
    }
}

/*
message TrxDataVerifyProto {
	bytes text = 1;
}

 */

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer: KSerializer<BigInteger> {
    override fun serialize(encoder: Encoder, obj: BigInteger) {
        val bz = obj.toByteArray()
        encoder.encodeSerializableValue(ByteArraySerializer(), bz)
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        val bz = decoder.decodeSerializableValue(ByteArraySerializer())
        val bigInt = BigInteger(bz)
        return bigInt
    }
}


