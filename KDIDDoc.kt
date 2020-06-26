package com.example.kdid

import com.example.kdid.tools.JsonBytesAdapter
import com.example.libkchain.tools.toHex
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.security.MessageDigest

interface KDIDMessage {
    fun toJson(): String
}
data class KDIDDoc (
    val id: KDID,
    val pubKey: KDIDPubKey,
    val subject: KDIDSubject ) : KDIDMessage
{
    fun toHashes(): KDIDSubjectHashes {
        val sha256 = MessageDigest.getInstance("SHA-256")
        return KDIDSubjectHashes(
            sha256.digest(subject.birthday.toByteArray()),
            sha256.digest(subject.gender.toByteArray()),
            sha256.digest(subject.name.toByteArray()),
            sha256.digest(subject.phoneNum.toByteArray()),
            sha256.digest(subject.socialNum.toByteArray())
        )
    }

    override fun toJson(): String {
        return Gson().toJson(this)
    }
    companion object {
        fun fromJson(json: String): KDIDDoc {
            return Gson().fromJson(json, KDIDDoc::class.java)
        }
    }
}

data class KDID (
    val method: String,
    val idstring: String ) : KDIDMessage
{
    override fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): KDID {
            return Gson().fromJson(json, KDID::class.java)
        }
    }

    fun did(): String {
        return "did:$method:$idstring"
    }
}

interface PubKey {
    val pubKey: ByteArray
    fun toFormatted(): String
}

data class KDIDPubKey (
    val id: KDID,
    val type: String,
    val controller: KDID,
    val pubKey: PubKey
)
{

    data class PubKeyJwk(override val pubKey: ByteArray) : PubKey {
        override fun toFormatted(): String {
            return "JWK:"+ toHex(pubKey)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PubKeyJwk

            if (!pubKey.contentEquals(other.pubKey)) return false

            return true
        }

        override fun hashCode(): Int {
            return pubKey.contentHashCode()
        }
    }

    data class PubKeyPem(override val pubKey: ByteArray) : PubKey {
        override fun toFormatted(): String {
            return "PEM:"+ toHex(pubKey)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PubKeyJwk

            if (!pubKey.contentEquals(other.pubKey)) return false

            return true
        }

        override fun hashCode(): Int {
            return pubKey.contentHashCode()
        }
    }
}


data class KDIDSubject (
    val name: String,
    val gender: String,
    val birthday: String,
    val phoneNum: String,
    val socialNum: String ): KDIDMessage
{
    override fun toJson(): String {
        return Gson().toJson(this)
    }
    companion object {
        fun fromJson(json: String): KDIDSubject {
            return Gson().fromJson(json, KDIDSubject::class.java)
        }
    }
}

data class KDIDSubjectHashes (
    val birthday: ByteArray,
    val gender: ByteArray,
    val name: ByteArray,
    val phoneNum: ByteArray,
    val socialNum: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        val o = other as KDIDSubjectHashes

        if(!(this.birthday contentEquals o.birthday)) {
            return false
        }
        if(!(this.gender contentEquals o.gender)) {
            return false
        }
        if(!(this.name contentEquals o.name)) {
            return false
        }
        if(!(this.phoneNum contentEquals o.phoneNum)) {
            return false
        }
        if(!(this.socialNum contentEquals o.socialNum)) {
            return false
        }
        return true
    }

    fun toJson(): String {
        val gson = GsonBuilder().registerTypeHierarchyAdapter(ByteArray::class.java, JsonBytesAdapter()).create()
        return gson.toJson(this)
    }

    companion object {
        fun fromJson(json: String): KDIDSubjectHashes {
            val gson = GsonBuilder().registerTypeHierarchyAdapter(ByteArray::class.java, JsonBytesAdapter()).create()
            return gson.fromJson(json, KDIDSubjectHashes::class.java)
        }
    }
}

