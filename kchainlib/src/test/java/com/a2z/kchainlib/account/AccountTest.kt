package com.a2z.kchainlib.account

import com.a2z.kchainlib.common.TResult
import com.a2z.kchainlib.crypto.TKeyStore
import com.a2z.kchainlib.common.Tools
import com.a2z.kchainlib.common.toHex
import com.a2z.kchainlib.net.Node
import com.a2z.kchainlib.trx.Transaction
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test
import java.math.BigInteger

class AccountTest {
    @ImplicitReflectionSerializer
    @Test
    fun test_create_date() {
        val path = "./src/test/java/com/a2z/kchainlib/crypto/keyparam_2C7D67FA63368DBEBB20B90D69313E87021B78F3.json"
        val acct = TAssetAccount(TKeyStore.open(path)).apply {
            query()
        }

        acct.createData(
            "Hello... DataAccount 1".toByteArray()
        ) {
            "1111"
        }.let {
            when(it) {
                is TResult.Success -> {
                    val txhash = it.value
                    val daddr = Tools.dataAddress( acct.address, txhash)
                    println("txhash        ${txhash.toHex()}")
                    println("data address: ${daddr.toHex()}")

                    Transaction.waitFor(txhash) {
                        when(it) {
                            is TResult.Success -> {
                                println(it)

                                val dacct = TDataAccount(daddr)
                                dacct.query()
                                println(String(dacct.text!!))
                            }
                            is TResult.Error -> println(it)
                        }
                    }
                }
                is TResult.Error -> println(it)
            }
        }


    }
    @ImplicitReflectionSerializer
    @Test
    fun test_transfer() {
//        val path = "./src/test/java/com/a2z/kchainlib/crypto/keyparam_2C7D67FA63368DBEBB20B90D69313E87021B78F3.json"
        val path = "./src/test/java/com/a2z/kchainlib/crypto/keyparam_000.json"

        val acct = TAssetAccount(TKeyStore.open(path)).apply {
            query()
            println(this)
        }

        acct.transfer(
            Tools.randBytes(20),
            BigInteger.TEN,
            BigInteger.TEN) {
            "1111"
        }.let {
            when(it) {
                is TResult.Success -> {
                    println("txhash: ${it.value.toHex()}")

                    loop@ while(true) {
                        when(val txret = Node.default.tx(it.value, true)) {
                            is TResult.Success -> {
                                //println("tx ${txret.value.toString(2)}")
                                break@loop
                            }
                            is TResult.Error -> { // not found tx or other errors
                                println("${txret.code}, ${txret.message}")
                            }
                        }

                        Thread.sleep(1000)
                    }

                }
                is TResult.Error -> println("error: ${it.message}")
            }
        }

        acct.query()
        println(acct)
    }
}

