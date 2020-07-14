package com.a2z.kchainlib.crypto

import com.google.crypto.tink.*
import com.google.crypto.tink.proto.*
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.Ed25519PrivateKeyManager
import com.google.crypto.tink.signature.SignatureKeyTemplates
import com.google.crypto.tink.signature.SignatureKeyTemplates.ED25519
import com.google.crypto.tink.subtle.Ed25519Sign
import com.google.crypto.tink.subtle.Ed25519Verify
import java.security.GeneralSecurityException


class KEd25519 (
    private val type: String,
    private val keyPair: ByteArray
){
    private var prvKey: ByteArray? = null
    private var pubKey: ByteArray? = null


    init {
        assert(keyPair.size == 64)
        prvKey = keyPair.copyOf(32)
        pubKey = keyPair.copyOfRange(32, 64)
    }

    constructor(
        type: String,
        prvKey: ByteArray,
        pubKey: ByteArray
    ): this(type, prvKey + pubKey)

    companion object {
        fun createKeyPair(): KEd25519 {
            val keysetHandle = KeysetHandle.generateNew(Ed25519PrivateKeyManager.rawEd25519Template())
            val keyset = CleartextKeysetHandle.getKeyset(keysetHandle)
            val prvKey = Ed25519PrivateKey.parseFrom(keyset.getKey(0).getKeyData().getValue());
            return KEd25519(Ed25519PrivateKeyManager.rawEd25519Template().typeUrl,
                prvKey.keyValue.toByteArray() + prvKey.publicKey.keyValue.toByteArray())

        }
    }

    fun getType(): String {
        return this.type
    }

    fun getPubKey(): ByteArray {
        return this.pubKey!!
    }

    fun sign(text: ByteArray): ByteArray {
        if (prvKey == null) {
            throw GeneralSecurityException("not found private key")
        }
        val signer = Ed25519Sign(prvKey)
        return signer.sign(text)
    }

    fun verify(sig: ByteArray, text: ByteArray) {
        if (pubKey == null) {
            throw GeneralSecurityException("not found public key")
        }
        val verifier = Ed25519Verify(pubKey)
        verifier.verify(sig, text)
    }
}