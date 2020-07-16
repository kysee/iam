package com.a2z.kchainlib.net

import com.a2z.kchainlib.tools.toHex
import kotlinx.coroutines.*
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

    fun post(req: String, callback: (String)-> Unit): Job {
        val rpcServer = this
        return GlobalScope.launch(Dispatchers.IO) {
            val mURL = URL(rpcServer.nodeUrl)
            (mURL.openConnection() as HttpURLConnection).run {
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

                    callback(response.toString())
                }
            }

        }
    }

    fun lastblock(): String {
        val reqParam = JsonRPCParams (
            "last_block"
        ).encode()
        return post(reqParam)
    }
    fun account(addr: ByteArray, callback: (String)-> Unit) {
        runBlocking {
            val job = post(
                JsonRPCParams (
                    "account",
                    arrayOf(addr.toHex())
                ).encode(),
                callback
            )
        }
    }
    fun syncTx(txbz: ByteArray): ByteArray {
        val reqParam = JsonRPCParams (
            "tx_sync",
            arrayOf(txbz.toHex())
        ).encode()
        val resp = post(reqParam)
        val jret = JSONObject(resp)
        if(jret.getInt("code") != 0) {
            throw Exception(jret.getString("log"))
        }
        return jret.getString("hash").hexToByteArray()
    }
    fun tx(txhash: ByteArray, prove: Boolean) {
        val reqParam = JsonRPCParams (
            "tx",
            arrayOf(txhash.toHex(), prove.toString())
        ).encode()
        val resp = post(reqParam)
    }
}