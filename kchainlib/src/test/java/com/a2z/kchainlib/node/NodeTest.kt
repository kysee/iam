package com.a2z.kchainlib.node

import com.a2z.kchainlib.common.toHex
import com.a2z.kchainlib.net.JsonRPCReq
import com.a2z.kchainlib.net.Node
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test

class NodeTest {
    @Test
    fun test_jsonrpc() {
        val jrp = JsonRPCReq("2.0", "1", "method",
            JsonArray(listOf(
                JsonPrimitive("param1"),
                JsonPrimitive(true)
            ))
        )
        val json = jrp.encode()
        println(json)
    }

    @Test
    fun test_query() {
        val node = Node("http://127.0.0.1:26657")
        val resp = node.lastblock()
        println("lastblock: $resp")
    }
}