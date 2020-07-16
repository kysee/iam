package com.a2z.kchainlib

import com.a2z.kchainlib.tools.toHex
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class Ex(
    val a: Int = 0,
    val b: Int = 0
) {
    fun Ex.test() {
        println("this is test function")
    }
}

fun Ex.test2() {
    println("this: " + this)
    println("this is test function")
}

fun <T> T.name(block: (T) -> String): String {
    return block(this)
}

fun <T, R> babyOf(receiver: T, block: T.(String) -> R): R {
    return receiver.block("str param")
}

class AnyTest {
    @Test
    fun ext_fun_test() {
        val ex1 = Ex()
        val name = ex1.name {
            return@name "i am something"
        }
        println(name)

        val b = babyOf(ex1) {
            this
        }
        println("parent: ${ex1.a}, ${ex1.b}")
        println("baby: ${b.a}, ${b.b}")
    }
}