package com.a2z.kchainlib

import com.a2z.kchainlib.tools.toHex
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class Ex {
    public val a : Int = 0
    public val b : Long = 0

    fun Ex.test() {
        println("this is test function")
    }
}

fun Ex.test2() {
    println("this: " + this)
    println("this is test function")
}

class AnyTest {
    @Test
    fun first_test() {
        val ex1 = Ex()
        val ex2 = Ex::class

        println("class: " + Ex::class)
        println("object1: " + ex1)
        println("object2: " + ex2)

        ex1.test2()
    }
}