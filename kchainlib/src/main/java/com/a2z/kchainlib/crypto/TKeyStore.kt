package com.a2z.kchainlib.crypto

import com.a2z.kchainlib.common.*
import com.a2z.kchainlib.common.Tools
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


@Serializable
class TKeyStore (
    val version: Int,
    @Serializable(with=THexSerializer::class)
    val address: ByteArray,
    @Serializable(with=THexSerializer::class)
    @SerialName("pub_key")
    val pub: ByteArray,
    private val cp: TCipherParams,
    private val kp: TKdfParams,
    val type: String = "ed25519"     // asymm. key type
) {
    companion object {
        fun create(getCredential: () -> String): TKeyStore {
            val newKeyPair = TRawKeyPairED25519.createKeyPair()

            return TKdfParams (
                ka = "pbkdf2",
                kh = "sha256",
                kc = Tools.randInt(20000..50000),
                ks = Tools.randBytes(32),
                kl = 32 // bytes
            ).run {
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val spec = PBEKeySpec(getCredential().toCharArray(), ks, kc, kl*8)
                val sk = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

                // encryption
                val c = Cipher.getInstance("AES/CBC/PKCS7Padding")
                c.init(Cipher.ENCRYPT_MODE, sk, IvParameterSpec( Tools.randBytes(32) ))
                val ciphertext = c.doFinal(c.iv + newKeyPair.prv!!)

                TKeyStore(
                    version = 2,
                    address = Tools.address(newKeyPair.pub),
                    pub = newKeyPair.pub,
                    cp = TCipherParams(
                        "aes-cbc",
                        ciphertext,
                        c.iv
                    ),
                    kp = this
                )
            }
        }

        fun open(path: String): TKeyStore {
            val keyParams = Json.parse<TKeyStore>(
                serializer(),
                File(path).readText(Charsets.UTF_8)
            )
            assert(keyParams.address.contentEquals(Tools.address(keyParams.pub)))

            return keyParams
        }
    }

    fun save(path: String) {
        Json.stringify<TKeyStore>(serializer(), this).let {
            File(path).writeText(it)
        }
    }

    private fun extractPrv(getCredential: () -> String): ByteArray {
        // PBKDF2
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(getCredential().toCharArray(), kp.ks, kp.kc, kp.kl*8)
        val sk = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        c.init(Cipher.DECRYPT_MODE, sk, IvParameterSpec( cp.ci ))
        val prvbz = c.doFinal(cp.ct)
        val _iv = prvbz.sliceArray(cp.ci.indices)
        val _prv = prvbz.sliceArray(cp.ci.size until cp.ci.size + 32)
        val _pub = prvbz.sliceArray(cp.ci.size + _prv.size until prvbz.size)

        assert(_iv.contentEquals(cp.ci))
        assert(_pub.contentEquals(pub))

        return _prv
    }

    fun sign(text: ByteArray, getCredential: () -> String): ByteArray {
        return extractPrv(getCredential).let {
            return when(type) {
                "ed25519" -> {
                    TRawKeyPairED25519(it, pub).sign(text)
                }
                else -> error("not supported key type")
            }
        }
    }

    fun verify(sig: ByteArray, text: ByteArray): Boolean {
        return when(type) {
            "ed25519" -> {
                TRawKeyPairED25519(null, pub).verify(sig, text)
            }
            else -> error("not supported key type")
        }
    }

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

