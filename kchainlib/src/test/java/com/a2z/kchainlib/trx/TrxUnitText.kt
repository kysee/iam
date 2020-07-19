package com.a2z.kchainlib.trx

import com.a2z.kchainlib.common.Tools
import org.junit.Test

class TrxUnitText {
    @Test
    fun test_wait_tx() {
        Transaction.waitFor(Tools.randBytes(32)) {
            println(it)
        }
    }
}