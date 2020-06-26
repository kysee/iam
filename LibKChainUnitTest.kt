package com.example.libkchain

import com.example.libkchain.tools.RandBytes
import com.example.libkchain.tools.toHex
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Test
import org.junit.Assert.*
import java.math.BigInteger

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LibKChainUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testProtobuf() {
        val tx = TrxTransferProtoEx(BigInteger("10000000000000000"), "Hello".toByteArray())
        val proto = ProtoBuf().dump(TrxTransferProtoEx.serializer(), tx)
        println("proto: " + toHex(proto))
    }
}