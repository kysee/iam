package com.a2z.kchainlib.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class TKeyStore (
    private var prv: ByteArray,
    val pub: ByteArray,
    val pass: String
){
    init {


    }
    companion object {
        fun open(path: String): TKeyStore {
            TODO("implement to read and parse a key file")
        }
    }

    fun save(): Boolean {
        TODO("implement to save key file")
    }

    @Serializable
    data class TKeyFile(
        val version: String,
        val address: ByteArray,
        val pub_key: ByteArray,
        val cp: TCipherParams,
        val kp: TKdfParams
    )

    @Serializable
    data class TCipherParams(
        val algo: String,
        val text: ByteArray,
        val iv: ByteArray,
        val salt: ByteArray
    )

    @Serializable
    data class TKdfParams(
        val algo: String,
        val prf: String,
        val iter: Int,
        val salt: ByteArray,
        val keyLen: Int
    )
}