package com.a2z.kchainlib.account

import com.a2z.kchainlib.common.*
import com.a2z.kchainlib.crypto.TKeyStore
import com.a2z.kchainlib.net.Node
import com.a2z.kchainlib.trx.Transaction
import com.a2z.kchainlib.trx.TrxDataCreate
import com.a2z.kchainlib.trx.TrxTransfer
import kotlinx.serialization.ImplicitReflectionSerializer
import org.json.JSONObject
import java.math.BigInteger

class TAssetAccount (
    val ks: TKeyStore,
    val address: ByteArray = Tools.address(ks.pub)
) {
    private var nonce: Long = 0
    private var balance: BigInteger = BigInteger.ZERO

    companion object {
        fun create(getCredential: () -> String): TAssetAccount {
            return TAssetAccount(
                TKeyStore.create(getCredential)
            )
        }
    }

    fun getNonce(): Long {
        return nonce
    }

    fun getBalance(): BigInteger {
        return balance
    }

    fun sign(tx: Transaction, getCredential:()->String): ByteArray {
        tx.sig = null
        val txbz = tx.encode()

        return ks.sign(txbz, getCredential)
    }

    fun query(n:Node = Node.default): TResult.Error? {
        return when (val ret = n.account(address)) {
            is TResult.Success -> {
                this.nonce = ret.value.getPrimitive("nonce").long
                this.balance = ret.value.getPrimitive("balance").content.toBigInteger()
                null
            }
            is TResult.Error -> ret
        }.exhaustive
    }

    @ImplicitReflectionSerializer
    fun transfer(
        to: ByteArray,
        amt: BigInteger, gas: BigInteger,
        note: String? = null,
        n: Node = Node.default,
        getCredential:()->String
    ): TResult<ByteArray> {

        val tx = Transaction(
            nonce+1,
            Tools.currentNanos(),
            address,
            to,
            gas,
            Transaction.ACTION_TRANSFER,
            TrxTransfer(
                amt,
                note?.toByteArray()
            ).encode<TrxTransfer>(),
            ks.pub
        )
        return publish(tx, n, getCredential)
    }

    @ImplicitReflectionSerializer
    fun createData(
        text: ByteArray,
        authors: Array<ByteArray>? = null,
        secure: Boolean = false,
        n: Node = Node.default,
        getCredential:()->String
    ): TResult<ByteArray> {
        val tx = Transaction(
            nonce + 1,
            Tools.currentNanos(),
            address,
            address,
            BigInteger.TEN,
            Transaction.ACTION_DATACREATE,
            TrxDataCreate (
                text,
                authors?: arrayOf(address)
            ).encode<TrxDataCreate>(),
            ks.pub
        )
        return publish(tx, n, getCredential)
    }

    // if success, return TResult<ByteArray>.Success(tx hash)
    // if fail in processing tx, return TResult.Error
    // if other errors, return TResult.Error
    @ImplicitReflectionSerializer
    fun publish(tx: Transaction, n: Node = Node.default, getCredential:()->String): TResult<ByteArray> {
        tx.sig = sign(tx, getCredential)
        return when (val ret = n.syncTx(tx.encode())) {
            is TResult.Success -> {
                if(ret.value.getPrimitive("code").int != 0) {
                    return TResult.Error(ret.value.getPrimitive("code").int, ret.value.getPrimitive("log").content)
                }
                return TResult.Success(ret.value.getPrimitive("hash").content.hexToByteArray())
            }
            is TResult.Error -> ret
        }
    }

    override fun toString(): String {
        return this.toStringIntent()
    }

    fun toStringIntent(intent: String = " "): String {
        return String.format("""{
$intent address: %s
$intent nonce: $nonce
$intent balance: $balance
}""", address.toHex())
    }
}