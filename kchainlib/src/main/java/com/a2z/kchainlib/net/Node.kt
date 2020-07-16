package com.a2z.kchainlib.net

import com.a2z.kchainlib.tools.toHex
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
        val rpcServer = this

        val mURL = URL(rpcServer.nodeUrl)
        (mURL.openConnection() as HttpURLConnection)?.run {
            // optional default is GET
            requestMethod = "POST"
            doOutput = true

            val wr = java.io.OutputStreamWriter(getOutputStream());
            wr.write(req);
            wr.flush();

//            kotlin.io.println("URL : $url")
//            kotlin.io.println("Response Code : $responseCode")

            if(responseCode != HttpURLConnection.HTTP_OK) {
                error("http error: code($responseCode), msg($responseMessage)")
            }

            java.io.BufferedReader(java.io.InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
//                kotlin.io.println("Response : $response")

                val jret = JSONObject(response.toString())
                jret.optJSONObject("error")?.let {
                    error("jsonrpc error: code(${it.getInt("code")}), message(${it.getString("message")})")
                }

                return jret.getJSONObject("result").toString()
            }
        }
    }

    fun lastblock(): String {
        return post(JsonRPCRequest (
            "last_block"
        ).encode())
    }
    fun account(addr: ByteArray): String {
        return post(
            JsonRPCRequest (
                "account",
                arrayOf(addr.toHex())
            ).encode()
        )
    }
    fun syncTx(txbz: ByteArray): String {
        return post (
            JsonRPCRequest (
            "tx_sync",
                arrayOf(txbz.toHex())
            ).encode()
        )
    }
    fun tx(txhash: ByteArray, prove: Boolean) {
        val reqParam = JsonRPCRequest (
            "tx",
            arrayOf(txhash.toHex(), prove.toString())
        ).encode()
        val resp = post(reqParam)
    }
}