package com.a2z.kchainlib.tools

import java.security.SecureRandom

val HEX_CHARS = "0123456789ABCDEF"

fun ByteArray.toHex(): String {
    val hexChars = CharArray(this.size * 2)
    for (i in this.indices) {
        val v = this[i].toInt() and 0xff
        hexChars[i * 2] = HEX_CHARS[v shr 4]
        hexChars[i * 2 + 1] = HEX_CHARS[v and 0xf]
    }
    return String(hexChars)
}

fun String.hexToByteArray(): ByteArray {
    val result = ByteArray(this.length / 2)

    for (i in 0 until this.length step 2) {
        val firstIndex = HEX_CHARS.indexOf(this[i]);
        val secondIndex = HEX_CHARS.indexOf(this[i + 1]);

        val octet = firstIndex.shl(4).or(secondIndex)
        result.set(i.shr(1), octet.toByte())
    }

    return result
}

fun randBytes(n: Int): ByteArray {
    val random = SecureRandom()
    val bytes = ByteArray(n)
    random.nextBytes(bytes)
    return bytes
}