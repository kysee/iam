package com.a2z.kchainlib.crypto

import com.google.crypto.tink.*
import com.google.crypto.tink.proto.*
import com.google.crypto.tink.signature.Ed25519PrivateKeyManager
import com.google.crypto.tink.subtle.Ed25519Sign
import com.google.crypto.tink.subtle.Ed25519Verify
import java.security.GeneralSecurityException
import java.security.MessageDigest


class TED25519KeyPair(
    override val prv: ByteArray?,
    override val pub: ByteArray,
    override val type: String = "ed25519"
) : TRawKeyPair() {

    constructor(material: ByteArray) : this(material.copyOf(32), material.copyOfRange(32, 64), "ed25519-keypair")

    companion object {
        fun createKeyPair(): TED25519KeyPair {
            val keysetHandle = KeysetHandle.generateNew(Ed25519PrivateKeyManager.rawEd25519Template())
            val keyset = CleartextKeysetHandle.getKeyset(keysetHandle)
            val prvKey = Ed25519PrivateKey.parseFrom(keyset.getKey(0).getKeyData().getValue());

            return TED25519KeyPair(
                prvKey.keyValue.toByteArray(),
                prvKey.publicKey.keyValue.toByteArray(),
                "ed25519-keypair"
               )
        }
    }

    override fun sign(text: ByteArray): ByteArray {
        if (prv == null) {
            throw GeneralSecurityException("not found private key")
        }
        val signer = Ed25519Sign(prv)
        return signer.sign(text)
    }

    override fun verify(sig: ByteArray, text: ByteArray): Boolean {
        if (pub == null) {
            throw GeneralSecurityException("not found public key")
        }
        val verifier = Ed25519Verify(pub)
        verifier.verify(sig, text)
        return true
    }

    override fun address(): ByteArray {
        if (pub == null) {
            throw GeneralSecurityException("not found public key")
        }
        val sha256 = MessageDigest.getInstance("SHA-256")
        return sha256.digest(pub).copyOf(20)
    }
}