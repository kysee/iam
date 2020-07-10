package com.a2z.kchainlib.crypto

import com.google.crypto.tink.KeyManager
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.KeysetManager
import com.google.crypto.tink.signature.Ed25519PrivateKeyManager
import com.google.crypto.tink.signature.SignatureKeyTemplates
import com.google.crypto.tink.signature.SignatureKeyTemplates.ED25519


class KEd25519 {

    private var pubKey: ByteArray? = null
    private var prvKey: ByteArray? = null

    companion object {
        fun createKeyPair() {
            Ed25519PrivateKeyManager.rawEd25519Template().value

        }
    }


}