package com.a2z.kchainlib

import com.a2z.kchainlib.tools.randBytes
import com.a2z.kchainlib.trx.TrxDataCreate
import com.a2z.kchainlib.trx.TrxPayload
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Assert
import org.junit.Test

class JsonUnitTest {
    @Test
    @ImplicitReflectionSerializer
    fun testTrxDataCreateProtobuf() {
        val authors = arrayOf(randBytes(20), randBytes(20), randBytes(20))
        val tx = TrxDataCreate(
            "Hello".toByteArray(),
            authors,
            0
        )
        val bz = tx.encode<TrxDataCreate>()
        val tx2 = TrxPayload.decode<TrxDataCreate>(bz)
        Assert.assertEquals(tx, tx2)
    }
}