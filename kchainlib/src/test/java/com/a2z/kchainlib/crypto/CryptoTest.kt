package com.a2z.kchainlib.crypto

import com.a2z.kchainlib.account.TAssetAccount
import com.a2z.kchainlib.tools.Tools
import com.a2z.kchainlib.tools.hexToByteArray
import com.a2z.kchainlib.tools.toHex
import com.google.crypto.tink.*
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.proto.*
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.Ed25519PrivateKeyManager
import com.google.crypto.tink.signature.SignatureConfig
import com.google.crypto.tink.subtle.Ed25519Sign
import com.google.crypto.tink.subtle.Ed25519Verify
import com.google.crypto.tink.subtle.Hex
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class CryptoTest {
    init {
        // Initialize Tink configuration
        TinkConfig.register()
        SignatureConfig.register()
    }

    fun create_ed25519(): KeysetHandle {
        // keypair creation
        return KeysetHandle.generateNew(Ed25519PrivateKeyManager.rawEd25519Template())
    }

    fun sign(text: ByteArray, keysetHandle: KeysetHandle) : ByteArray {
        // sign
        val signer = keysetHandle.getPrimitive(PublicKeySign::class.java)
        return signer.sign(text)
    }

    fun sign_subtle(text: ByteArray, prvKey: ByteArray): ByteArray {
        val signer = Ed25519Sign(prvKey)
        return signer.sign(text)
    }

    fun verify(text: ByteArray, sig: ByteArray, keysetHandle: KeysetHandle): Boolean {
        // verify
        val verifier = keysetHandle.publicKeysetHandle.getPrimitive(PublicKeyVerify::class.java)
        try {
            verifier.verify(sig, text)
        } catch(e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun verify_subtle(text: ByteArray, sig: ByteArray, pubKey: ByteArray): Boolean {
        val verifier = Ed25519Verify(pubKey)
        try {
            verifier.verify(sig, text)
        } catch(e: GeneralSecurityException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun export_raw_keypair(keysetHandle: KeysetHandle): ByteArray {
        // method 1
        val keyset = CleartextKeysetHandle.getKeyset(keysetHandle)
        val prvKey = Ed25519PrivateKey.parseFrom(keyset.getKey(0).getKeyData().getValue());
//        println("keydata-: " + keyset.getKey(0).getKeyData())
//        println("--- ----: " + toHex(keyset.getKey(0).getKeyData().getValue().toByteArray()))
//        println("raw prv1: " + toHex(prvKey.keyValue.toByteArray()))
//        println("raw pub1: " + toHex(prvKey.publicKey.keyValue.toByteArray()))

        return prvKey.keyValue.toByteArray() + prvKey.publicKey.keyValue.toByteArray()

        // method 2
//        //
//        // export encoded keypair
//        val prvKeyStream = ByteArrayOutputStream()
//        val pubKeyStream = ByteArrayOutputStream()
//        CleartextKeysetHandle.write(
//            keysetHandle,
//            BinaryKeysetWriter.withOutputStream(prvKeyStream)
//        )
//        CleartextKeysetHandle.write(
//            keysetHandle.publicKeysetHandle,
//            BinaryKeysetWriter.withOutputStream(pubKeyStream)
//        )
////        println("encoded prv: " + toHex(prvKeyStream.toByteArray()))
////        println("encoded pub: " + toHex(pubKeyStream.toByteArray()))
//
//        //
//        // export raw keypair
//        val method: Method = keysetHandle.javaClass.getDeclaredMethod("getKeyset")
//        method.isAccessible = true
//        val keyset = method.invoke(keysetHandle) as Keyset
//
//        val prvKey = Ed25519PrivateKey.parseFrom(keyset.getKey(0).getKeyData().getValue());
//        println("raw prv: " + toHex(prvKey.keyValue.toByteArray()))
//        println("raw prv: " + toHex(prvKey.publicKey.keyValue.toByteArray()))
//        return prvKey.keyValue.toByteArray() + prvKey.publicKey.keyValue.toByteArray()
    }

    fun import_raw_keypair(rawPrvKey: ByteArray, rawPubKey: ByteArray): KeysetHandle {
        //    build ed25519publickey/ed25519privatekey
        val ed25519PubKey = Ed25519PublicKey.newBuilder().setKeyValue( ByteString.copyFrom(rawPubKey) ).build()
        val ed25519PrvKey = Ed25519PrivateKey.newBuilder()
            .setKeyValue( ByteString.copyFrom(rawPrvKey) )
            .setPublicKey( ed25519PubKey )
            .build()

        ed25519PrvKey.publicKey
        //     build KeyData
        val prvKeyData = KeyData.newBuilder()
            .setTypeUrl(Ed25519PrivateKeyManager.rawEd25519Template().typeUrl) // type.googleapis.com/google.crypto.tink.Ed25519PrivateKey
            .setKeyMaterialType(KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE)
            .setValue( ByteString.copyFrom(ed25519PrvKey.toByteArray()) )
            .build()
//        println("KeyData(Prv): " + prvKeyData)

        //     build Keyset.Key & Keyset
        val keysetKey = Keyset.Key.newBuilder()
            .setStatus(KeyStatusType.ENABLED)
            .setOutputPrefixType(OutputPrefixType.RAW)
//            .setKeyId(123)
            .setKeyData(prvKeyData).build()
        val keyset = Keyset.newBuilder()
//            .setPrimaryKeyId(124)
            .addKey(keysetKey).build()

        return CleartextKeysetHandle.read(object : KeysetReader {
            override fun readEncrypted(): EncryptedKeyset {
                return EncryptedKeyset.getDefaultInstance()
            }

            override fun read(): Keyset {
                return keyset
            }
        })
    }

    @Test
    fun test_key_import() {
        val ed25519KeyTemplate = Ed25519PrivateKeyManager.rawEd25519Template()

        // keypair creation
        val keysetHandle = create_ed25519()

        //
        // export raw keypair
        val rawKeyPair = export_raw_keypair(keysetHandle)
        val rawPrvKey = rawKeyPair.copyOfRange(0, 32)
        val rawPubKey = rawKeyPair.copyOfRange(32, 64)

        //
        // check sign and verify by using raw keys
        val text = Tools.randBytes(1024)
        assert( verify_subtle(text, sign(text, keysetHandle), rawPubKey) )
        assert( verify(text, sign_subtle(text, rawPrvKey), keysetHandle) )

        //
        // import raw keypair
        val keysetHandle2 = import_raw_keypair(rawPrvKey, rawPubKey)

        //
        // check sign and verify by using KeysetHandle
        assert( verify(text, sign(text, keysetHandle), keysetHandle2) )
        assert( verify(text, sign(text, keysetHandle2), keysetHandle) )
    }

    @Test
    fun testEd25519Subtle() {
        val plainText = "Text that should be signed to prevent unknown tampering with its content."

        try {
            // GENERATE NEW KEYPAIR
            val keyPair = Ed25519Sign.KeyPair.newKeyPair()
            val prvkey = keyPair.privateKey
            val pubkey = keyPair.publicKey

//            println("prvkey: " + prvkey.size + "," + Hex.encode(prvkey))
//            println("pubkey: " + pubkey.size + "," + Hex.encode(pubkey))

            val signer = Ed25519Sign(prvkey)
            val sig = signer.sign("hello".toByteArray())

            val verifier = Ed25519Verify(pubkey)
            verifier.verify(sig, "hello".toByteArray())

        } catch (e: GeneralSecurityException) {
            Assert.fail(e.localizedMessage)
        }
    }

    @Test
    fun test_file_read() {
        val path = ".\\src\\test\\java\\com\\a2z\\kchainlib\\crypto\\keyparam_000.json"

        try {
            val keyStore = TKeyStore.open(path) {
                return@open "1112"
            }
            assert(false)
        } catch (e: Exception) {

        }

        val keyStore = TKeyStore.open(path) {
            return@open "1111"
        }

        var acct = TAssetAccount(
            TED25519KeyPair(keyStore.getMaterial())
        )
        acct.query()
        println("$acct")
    }

    @Test
    fun test_read_file_aes_cbc() {
        val path = ".\\src\\test\\java\\com\\a2z\\kchainlib\\crypto\\keyparam_000.json"
        val jret = JSONObject(File(path).readText(Charsets.UTF_8))

        //
        val addr = jret.getString("address").hexToByteArray()
        val pubKey = jret.getString("pub_key")!!.hexToByteArray()
        assert(addr.contentEquals(Tools.address(pubKey)))

        // PBKDF2
        val salt = Base64.getDecoder().decode(
            jret.getJSONObject("kp")!!.getString("ks")
        )!!
        val iter = jret.getJSONObject("kp")!!.getInt("kc")
        val klen = jret.getJSONObject("kp")!!.getInt("kl")

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec("1111".toCharArray(), salt, iter, klen*8)
        val sk = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")


        val encPrvKey = Base64.getDecoder().decode(
            jret.getJSONObject("cp")!!.getString("ct")
        )!!
        val iv = IvParameterSpec(
            Base64.getDecoder().decode(
                jret.getJSONObject("cp")!!.getString("ci")
            )
        )

        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        c.init(Cipher.DECRYPT_MODE, sk, iv)
        val prvKeybz = c.doFinal(encPrvKey)!!
        val prvKey = prvKeybz.sliceArray(iv.iv.size until iv.iv.size + 32)

        assert(prvKeybz.sliceArray(iv.iv.indices).contentEquals(iv.iv))
        assert(prvKeybz.sliceArray(iv.iv.size + 32 until prvKeybz.size).contentEquals(pubKey))
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