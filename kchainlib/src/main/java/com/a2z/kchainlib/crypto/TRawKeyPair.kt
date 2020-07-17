package com.a2z.kchainlib.crypto

abstract class TRawKeyPair {
    abstract val type: String
    abstract val prv: ByteArray?
    abstract val pub: ByteArray
    abstract fun sign(text: ByteArray): ByteArray
    abstract fun verify(sig: ByteArray, text: ByteArray): Boolean
    abstract fun address(): ByteArray
}