package com.a2z.kchainlib.account

import com.a2z.kchainlib.common.*
import com.a2z.kchainlib.crypto.TKeyStore
import com.a2z.kchainlib.net.Node
import com.a2z.kchainlib.trx.Transaction
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
                this.nonce = ret.value.getLong("nonce")
                this.balance = ret.value.getString("balance").toBigInteger()
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

    fun createData(text: ByteArray, authors: Array<ByteArray>? = null, secure: Boolean): TDataAccount {
        TODO("create a data account")
    }

    @ImplicitReflectionSerializer
    fun publish(tx: Transaction, n: Node = Node.default, getCredential:()->String): TResult<ByteArray> {
        tx.sig = sign(tx, getCredential)
        return when (val ret = n.syncTx(tx.encode())) {
            is TResult.Success -> {
                if(ret.value.getInt("code") != 0) {
                    return TResult.Error(ret.value.getInt("code"), ret.value.getString("log"))
                }
                return TResult.Success(ret.value.getString("hash").hexToByteArray())
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