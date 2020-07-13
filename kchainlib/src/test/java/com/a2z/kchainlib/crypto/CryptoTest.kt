package com.a2z.kchainlib.crypto

import com.a2z.kchainlib.tools.randBytes
import com.a2z.kchainlib.tools.toHex
import com.google.crypto.tink.*
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.proto.*
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.Ed25519PrivateKeyManager
import com.google.crypto.tink.signature.SignatureConfig
import com.google.crypto.tink.subtle.Ed25519Sign
import com.google.crypto.tink.subtle.Ed25519Verify
import com.google.crypto.tink.subtle.Hex
import kotlinx.io.ByteArrayOutputStream
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Method
import java.security.GeneralSecurityException
import java.security.MessageDigest


class CryptoTest {
    init {
        // Initialize Tink configuration
        TinkConfig.register()
        SignatureConfig.register()
    }

    fun create_ed25519() {

    }

    @Test
    fun test_keypare_export() {
        // keypair creation
        val keytmpl = Ed25519PrivateKeyManager.rawEd25519Template()
        val keysetHandle = KeysetHandle.generateNew(keytmpl)

        // export encoded keypair
        val prvKeyStream = ByteArrayOutputStream()
        val pubKeyStream = ByteArrayOutputStream()

        CleartextKeysetHandle.write(
            keysetHandle,
            BinaryKeysetWriter.withOutputStream(prvKeyStream)
        )
        CleartextKeysetHandle.write(
            keysetHandle.publicKeysetHandle,
            BinaryKeysetWriter.withOutputStream(pubKeyStream)
        )

        println("encoded prv: " + toHex(prvKeyStream.toByteArray()))
        println("encoded pub: " + toHex(pubKeyStream.toByteArray()))

        // export raw keypair
        val method: Method = keysetHandle.javaClass.getDeclaredMethod("getKeyset")
        method.isAccessible = true
        val keyset = method.invoke(keysetHandle) as Keyset

        val prvKey = Ed25519PrivateKey.parseFrom(keyset.getKey(0).getKeyData().getValue());
        println("raw prv: " + toHex(prvKey.keyValue.toByteArray()))
        println("raw prv: " + toHex(prvKey.publicKey.keyValue.toByteArray()))


        // sign
        val signer = keysetHandle.getPrimitive(PublicKeySign::class.java)
        val text = randBytes(100)
        val sig = signer.sign(text)

        // verify
        val verifier = keysetHandle.publicKeysetHandle.getPrimitive(PublicKeyVerify::class.java)
        try {
            verifier.verify(sig, text)
        } catch(e: Exception) {
            e.printStackTrace()
            assert(false)
        }
    }

    @Test
    fun test_key_import() {
        val ed25519KeyTemplate = Ed25519PrivateKeyManager.rawEd25519Template()

        // keypair creation
        val keysetHandle = KeysetHandle.generateNew(ed25519KeyTemplate)

        // export raw keypair
        val keyset1 = CleartextKeysetHandle.getKeyset(keysetHandle)
        val prvKey1 = Ed25519PrivateKey.parseFrom(keyset1.getKey(0).getKeyData().getValue());
        println("keydata-: " + keyset1.getKey(0).getKeyData())
        println("--- ----: " + toHex(keyset1.getKey(0).getKeyData().getValue().toByteArray()))
        println("raw prv1: " + toHex(prvKey1.keyValue.toByteArray()))
        println("raw pub1: " + toHex(prvKey1.publicKey.keyValue.toByteArray()))

        //
        // import raw keypair
        //
        val rawPrvKey = prvKey1.keyValue.toByteArray()
        val rawPubKey = prvKey1.publicKey.keyValue.toByteArray()

        //    build ed25519publickey/ed25519privatekey
        val ed25519PubKey = Ed25519PublicKey.newBuilder().setKeyValue( ByteString.copyFrom(rawPubKey) ).build()
        val ed25519PrvKey = Ed25519PrivateKey.newBuilder()
            .setKeyValue( ByteString.copyFrom(rawPrvKey) )
            .setPublicKey( ed25519PubKey )
            .build()

        //     build KeyData
        val prvKeyData = KeyData.newBuilder()
            .setTypeUrl(Ed25519PrivateKeyManager.rawEd25519Template().typeUrl) // type.googleapis.com/google.crypto.tink.Ed25519PrivateKey
            .setKeyMaterialType(KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE)
            .setValue( ByteString.copyFrom(ed25519PrvKey.toByteArray()) )
            .build()
        println("KeyData(Prv): " + prvKeyData)

        //     build Keyset.Key & Keyset
        val keysetKey = Keyset.Key.newBuilder()
            .setStatus(KeyStatusType.ENABLED)
            .setOutputPrefixType(OutputPrefixType.RAW)
//            .setKeyId(123)
            .setKeyData(prvKeyData).build()
        val keyset2 = Keyset.newBuilder()
//            .setPrimaryKeyId(124)
            .addKey(keysetKey).build()

        val keysetHandle2 = CleartextKeysetHandle.read(object : KeysetReader {
            override fun readEncrypted(): EncryptedKeyset {
                return EncryptedKeyset.getDefaultInstance()
            }

            override fun read(): Keyset {
                return keyset2
            }
        })



        // sign
        val signer = keysetHandle2.getPrimitive(PublicKeySign::class.java)
        val text = randBytes(100)
        val sig = signer.sign(text)

        // verify
        val verifier = keysetHandle2.publicKeysetHandle.getPrimitive(PublicKeyVerify::class.java)
        try {
            verifier.verify(sig, text)
        } catch(e: Exception) {
            e.printStackTrace()
            assert(false)
        }
    }

    @Test
    fun testEd25519() {
        val plainText = "Text that should be signed to prevent unknown tampering with its content."

        try {
            // GENERATE NEW KEYPAIR
            val keyPair = Ed25519Sign.KeyPair.newKeyPair()
            val prvkey = keyPair.privateKey
            val pubkey = keyPair.publicKey

            println("prvkey: " + prvkey.size + "," + Hex.encode(prvkey))
            println("pubkey: " + pubkey.size + "," + Hex.encode(pubkey))

            val signer = Ed25519Sign(prvkey)
            val sig = signer.sign("hello".toByteArray())

            val verifier = Ed25519Verify(pubkey)
            verifier.verify(sig, "hello".toByteArray())

        } catch (e: GeneralSecurityException) {
            Assert.fail(e.localizedMessage)
        }
    }

    @Test
    fun testHash() {
        try {
            val sha256 = MessageDigest.getInstance("SHA-256")
            val hash = sha256.digest("hello".toByteArray())

            println("hash: " + Hex.encode(hash))
        } catch (e: Exception) {
            Assert.fail(e.localizedMessage)
        }
    }
}