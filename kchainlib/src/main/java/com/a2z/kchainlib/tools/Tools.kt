package com.a2z.kchainlib.tools

import java.security.SecureRandom

val HEX_CHARS = "0123456789ABCDEF"
fun toHex(byteArray: ByteArray): String {

    val hexChars = CharArray(byteArray.size * 2)
    for (i in byteArray.indices) {
        val v = byteArray[i].toInt() and 0xff
        hexChars[i * 2] = HEX_CHARS[v shr 4]
        hexChars[i * 2 + 1] = HEX_CHARS[v and 0xf]
    }
    return String(hexChars)
}

fun fromHex(hex: String): ByteArray {
    val result = ByteArray(hex.length / 2)

    for (i in 0 until hex.length step 2) {
        val firstIndex = HEX_CHARS.indexOf(hex[i]);
        val secondIndex = HEX_CHARS.indexOf(hex[i + 1]);

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