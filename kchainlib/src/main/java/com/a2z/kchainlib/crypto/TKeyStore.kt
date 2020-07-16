package com.a2z.kchainlib.crypto

import com.a2z.kchainlib.tools.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class TKeyStore (
    private var prv: ByteArray,
    val pub: ByteArray
){
    init {

    }
    companion object {
        fun open(path: String, getCredential:()-> String): TKeyStore {
            val keyFile = Json.parse(
                TKeyFile.serializer(),
                File(path).readText(Charsets.UTF_8)
            )
            assert(keyFile.address.contentEquals(Tools.address(keyFile.pub_key)))

            // PBKDF2
            val salt = keyFile.kp.ks
            val iter = keyFile.kp.kc
            val klen = keyFile.kp.kl

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(getCredential().toCharArray(), salt, iter, klen*8)
            val sk = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")


            val encPrvKey = keyFile.cp.ct
            val iv = IvParameterSpec( keyFile.cp.ci )

            val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
            c.init(Cipher.DECRYPT_MODE, sk, iv)
            val prvKeybz = c.doFinal(encPrvKey)

            val prvKey = prvKeybz.sliceArray(iv.iv.size until iv.iv.size + 32)

            return TKeyStore(prvKey, keyFile.pub_key)
        }
    }

    fun save(): Boolean {
        TODO("implement to save key file")
    }

    fun getMaterial(): ByteArray {
        return prv + pub
    }

    @Serializable
    data class TKeyFile(
        val version: Int,
        @Serializable(with=THexSerializer::class) val address: ByteArray,
        @Serializable(with=THexSerializer::class) val pub_key: ByteArray,
        val cp: TCipherParams,
        val kp: TKdfParams
    )

    @Serializable
    data class TCipherParams(
        val ca: String,     // algorithm
        @Serializable(with = TBase64Serializer::class) val ct: ByteArray,  // cipher text
        @Serializable(with = TBase64Serializer::class) val ci: ByteArray   // initial vector
    )

    @Serializable
    data class TKdfParams(
        val ka: String,  // key derivation algorithm
        val kh: String,  // hash algorithm
        val kc: Int,     // iteration
        @Serializable(with = TBase64Serializer::class) val ks: ByteArray, // salt
        val kl: Int      // key length in bytes
    )
}

