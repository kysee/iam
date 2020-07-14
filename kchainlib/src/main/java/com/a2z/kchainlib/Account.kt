package com.a2z.kchainlib

import com.a2z.kchainlib.trx.Transaction

class Account {
    var nonce: Long = 0
    var address: ByteArray? = null

    fun sendTx(tx: Transaction): ByteArray {
        TODO("implement")
    }
}