package com.a2z.kchainlib.account

import com.a2z.kchainlib.tools.randBytes
import com.a2z.kchainlib.tools.toHex
import com.a2z.kchainlib.trx.Transaction
import com.a2z.kchainlib.trx.TrxTransfer
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test
import java.math.BigInteger

class AccountTest {
    @ImplicitReflectionSerializer
    @Test
    fun test_transfer() {
        val acct = AssetAccount("2FF2C92CEFD4AD5F220CBF1D4344BC8159C5A3B4")
        assert(acct.query())

        val txhash = acct.transfer(randBytes(20), BigInteger.TEN, BigInteger.TEN)
        println("txhash: " + txhash.toHex())
    }
}

fun ByteArray.toHex(): String {
    return toHex(this)
}