package com.a2z.kchainlib.net

import com.a2z.kchainlib.tools.fromHex
import com.a2z.kchainlib.tools.randBytes
import com.a2z.kchainlib.tools.toHex
import com.a2z.kchainlib.trx.Transaction
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.content
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Node (
    val nodeUrl: String
) {
    companion object {
        var default: Node = Node("http://localhost:26657")
            private set(value) {
                field = value
            }
        fun setDefaultNode(n: Node) {
            default = n
        }
    }

    fun post(req: String): String {

        val mURL = URL(this.nodeUrl)
        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "POST"
            doOutput = true

            val wr = java.io.OutputStreamWriter(getOutputStream());
            wr.write(req);
            wr.flush();

//            kotlin.io.println("URL : $url")
//            kotlin.io.println("Response Code : $responseCode")

            java.io.BufferedReader(java.io.InputStreamReader(inputStream)).use {
                val response = java.lang.StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
//                kotlin.io.println("Response : $response")

                return response.toString()
            }
        }
    }

    fun lastblock(): String {
        val reqParam = JsonRPCParams (
            "last_block"
        ).encode()
        return post(reqParam)
    }
    fun account(addr: ByteArray): String {
        return post(JsonRPCParams (
            "account",
            arrayOf(toHex(addr))
        ).encode())
    }
    fun syncTx(txbz: ByteArray): ByteArray {
        val reqParam = JsonRPCParams (
            "tx_sync",
            arrayOf(toHex(txbz))
        ).encode()
        val resp = post(reqParam)
        val jret = JSONObject(resp)
        if(jret.getInt("code") != 0) {
            throw Exception(jret.getString("log"))
        }
        return fromHex(jret.getString("hash"))
    }
    fun tx(txhash: ByteArray, prove: Boolean) {
        val reqParam = JsonRPCParams (
            "tx",
            arrayOf(toHex(txhash), prove.toString())
        ).encode()
        val resp = post(reqParam)
    }
}