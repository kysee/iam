package com.a2z.kchainlib.node

import com.a2z.kchainlib.account.AssetAccount
import com.a2z.kchainlib.crypto.TED25519KeyPair
import com.a2z.kchainlib.net.JsonRPCParams
import com.a2z.kchainlib.net.Node
import com.a2z.kchainlib.tools.fromHex
import kotlinx.serialization.json.Json
import org.junit.Test

class NodeTest {
    @Test
    fun test_jsonrpc() {
        val jrp = JsonRPCParams("2.0", "1", "method", arrayOf("params1", "params2"))
        val json = jrp.encode()
        println(json)
    }

    @Test
    fun test_query() {
        val node = Node("http://127.0.0.1:26657")
        var resp = node.lastblock()
        println("lastblock: $resp")
    }
}