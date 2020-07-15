package com.a2z.kchainlib.crypto

abstract class TRawKeyPair {
    abstract val type: String
    abstract val prv: ByteArray?
    abstract val pub: ByteArray
    abstract fun sign(t: ByteArray): ByteArray
    abstract fun verify(t: ByteArray, s: ByteArray): Boolean
    abstract fun address(): ByteArray
}