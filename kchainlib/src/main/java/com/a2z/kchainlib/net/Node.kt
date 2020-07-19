package com.a2z.kchainlib.net

import com.a2z.kchainlib.common.TResult
import com.a2z.kchainlib.common.toHex
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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

    fun post(req: String): TResult<JsonObject> = try {
        val rpcServer = this

        val mURL = URL(rpcServer.nodeUrl)
        (mURL.openConnection() as HttpURLConnection).run {
            // optional default is GET
            requestMethod = "POST"
            doOutput = true

            val wr = java.io.OutputStreamWriter(getOutputStream())
            wr.write(req);
            wr.flush();

//            kotlin.io.println("URL : $url")
//            kotlin.io.println("Response Code : $responseCode")

            if(responseCode != HttpURLConnection.HTTP_OK) {
                return TResult.Error(responseCode, responseMessage)
            }

            java.io.BufferedReader(java.io.InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                //kotlin.io.println("Response : $response")
                val jsonrpcResp = Json.parse(JsonObject.serializer(), response.toString())
                jsonrpcResp.getObjectOrNull("error")?.let {
                    it.getPrimitive("code").int

                    return TResult.Error(
                        it.getPrimitive("code").int,
                        it.getPrimitive("message").content + " - " + it.getPrimitive("data").contentOrNull)
                }

                return TResult.Success(jsonrpcResp.getObject("result"))
            }
        }
    } catch (ex: Exception) {
        TResult.Error(
            message = ex.message?: "unknown error",
            cause = ex
        )
    }

    fun lastblock(): TResult<JsonObject> {
        return post(JsonRPCReq (
            "last_block"
        ).encode())
    }

    fun account(addr: ByteArray): TResult<JsonObject> {
        return post(
            JsonRPCReq (
                "account",
                listOf(JsonPrimitive(addr.toHex()))
            ).encode()
        )
    }

    fun syncTx(txbz: ByteArray): TResult<JsonObject> {
        return post (
            JsonRPCReq (
            "tx_sync",
                listOf(JsonPrimitive(txbz.toHex()))
            ).encode()
        )
    }

    fun tx(txhash: ByteArray, prove: Boolean = true): TResult<JsonObject> {
        return post(
            JsonRPCReq (
                "tx",
                listOf(
                    JsonPrimitive(txhash.toHex()),
                    JsonPrimitive(prove)
                )
            ).encode()
        )
    }
}