package com.a2z.kchainlib.account

import com.a2z.kchainlib.crypto.TED25519KeyPair
import com.a2z.kchainlib.crypto.TRawKeyPair
import com.a2z.kchainlib.net.Node
import com.a2z.kchainlib.tools.Tools
import com.a2z.kchainlib.tools.hexToByteArray
import com.a2z.kchainlib.tools.toHex
import com.a2z.kchainlib.trx.Transaction
import com.a2z.kchainlib.trx.TrxTransfer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import org.json.JSONObject
import java.math.BigInteger

class TAssetAccount (
    val keyPair: TRawKeyPair?,
    val address: ByteArray = keyPair!!.address()
) {
    private var nonce: Long = 0
    private var balance: BigInteger = BigInteger.ZERO

    constructor(
        address: String
    ): this(null, address.hexToByteArray())

    constructor(
        pubKey: ByteArray
    ): this(TED25519KeyPair(null, pubKey, "ed25519"))

    fun getNonce(): Long {
        return nonce
    }

    fun getBalance(): BigInteger {
        return balance
    }

    fun sign(tx: Transaction): ByteArray {
        tx.sig = null
        val txbz = tx.encode()

        tx.sig = keyPair!!.sign(txbz)
        return tx.sig!!
    }

    fun query(n:Node = Node.default) {
        n.account(address!!) {
            val jret = JSONObject(it)
            jret.getJSONObject("result")?.let {
                assert( this.address.contentEquals(it.getString("address")!!.hexToByteArray()))
                this.nonce = it.getString("nonce").toLong()
                this.balance = it.getString("balance").toBigInteger()
            }
        }
    }

    @ImplicitReflectionSerializer
    fun transfer(to: ByteArray, amt: BigInteger, gas: BigInteger, n: Node = Node.default): ByteArray {
        val tx = Transaction(
            nonce,
            Tools.currentNanos(),
            address,
            to,
            gas,
            Transaction.ACTION_TRANSFER,
            TrxTransfer(
                BigInteger("10000000000000000"),
                "Hello".toByteArray()
            ).encode<TrxTransfer>(),
            keyPair!!.pub
        )
        return publish(tx, n)
    }

    fun createData(text: ByteArray, authors: Array<ByteArray>? = null, secure: Boolean): TDataAccount {
        TODO("create a data account")
    }


    fun publish(tx: Transaction, n: Node = Node.default): ByteArray {
        sign(tx)
        return n.syncTx(tx.encode())
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