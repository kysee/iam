package com.a2z.kchainlib.trx

import com.a2z.kchainlib.common.Tools
import com.a2z.kchainlib.common.hexToByteArray
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test
import org.junit.Assert.*
import java.math.BigInteger

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ProtoBufUnitTest {

    @ImplicitReflectionSerializer
    @Test
    fun testTrxTransferProtobuf() {
        val tx = TrxTransfer(
            BigInteger("10000000000000000"),
            "Hello".toByteArray()
        )
        val bz = tx.encode<TrxTransfer>()
        assertArrayEquals("0A072386F26FC10000120548656C6C6F".hexToByteArray(), bz)
        val tx2 = TrxPayload.decode<TrxTransfer>(bz)
        assertEquals(tx, tx2)
    }

    @ImplicitReflectionSerializer
    @Test
    fun testTrxDataCreateProtobuf() {
        val authors = arrayOf(
            Tools.randBytes(20),
            Tools.randBytes(20),
            Tools.randBytes(20))
        val txPayload = TrxDataCreate(
            "Hello".toByteArray(),
            authors,
            0
        )
        val bz = txPayload.encode<TrxDataCreate>()
        val tx2 = TrxPayload.decode<TrxDataCreate>(bz)
        assertEquals(txPayload, tx2)

        val tx = Transaction(
            1,
            2,
            Tools.randBytes(20),
            Tools.randBytes(20),
            BigInteger.TEN,
            Transaction.ACTION_DATACREATE,
            bz,
            Tools.randBytes(32)
        )
//        val txbz = tx.encode()
        //println(toHex(txbz))

        val txPayload2 = tx.getPayloadObject<TrxDataCreate>()
        assertEquals(txPayload, txPayload2)
    }
}