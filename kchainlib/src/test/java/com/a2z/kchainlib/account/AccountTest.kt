package com.a2z.kchainlib.account

import com.a2z.kchainlib.crypto.TKeyStore
import com.a2z.kchainlib.tools.Tools
import com.a2z.kchainlib.tools.toHex
import kotlinx.serialization.ImplicitReflectionSerializer
import org.json.JSONObject
import org.junit.Test
import java.math.BigInteger

class AccountTest {
    @ImplicitReflectionSerializer
    @Test
    fun test_transfer() {
        val path = "./src/test/java/com/a2z/kchainlib/crypto/keyparam_2C7D67FA63368DBEBB20B90D69313E87021B78F3.json"

        val acct = TAssetAccount(
            TKeyStore.open(path) {
                "1111"
            }
        )
        acct.query()
        with(acct) {
            println(this)
        }

        acct.transfer(
            Tools.randBytes(20),
            BigInteger.TEN,
            BigInteger.TEN).let {
            println("txhash: " + it.toHex())
        }

        acct.query()
        with(acct) {
            println(this)
        }
    }
}

