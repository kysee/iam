package com.a2z.kchainlib.crypto

import com.a2z.kchainlib.tools.randBytes
import com.google.crypto.tink.subtle.Ed25519Sign
import org.junit.Test

class Ked25519Test {
    @Test
    fun test_constructor() {
        val keypair = Ed25519Sign.KeyPair.newKeyPair()
        val material = keypair.privateKey + keypair.publicKey
        val e1 = KEd25519("ed25519", material)
        val e2 = KEd25519("ed25519", material.copyOf(32), material.copyOfRange(32, 64))

        var text = randBytes(1024*1024)
        var sig = e1.sign(text)
        e1.verify(sig, text)

        text = randBytes(1024*1024)
        sig = e2.sign(text)
        e1.verify(sig, text)
    }
}