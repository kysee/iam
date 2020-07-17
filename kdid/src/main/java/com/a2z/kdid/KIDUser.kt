package com.a2z.kdid

import com.a2z.kchainlib.account.TAssetAccount
import com.a2z.kchainlib.common.TResult
import com.a2z.kchainlib.common.Tools
import com.a2z.kchainlib.crypto.TKeyStore
import com.a2z.kchainlib.crypto.TRawKeyPairED25519
import com.a2z.kchainlib.trx.Transaction
import com.a2z.kchainlib.trx.TrxDataCreate
import com.a2z.kchainlib.trx.TrxPayload
import com.a2z.kdid.msg.KDID
import com.a2z.kdid.msg.KDIDDoc
import kotlinx.serialization.ImplicitReflectionSerializer
import java.math.BigInteger

class KIDUser (val name: String,
               val gender: String,
               val birthday: String,
               val phoneNum: String,
               val socialNum: String
) {

    private lateinit var kdid: KDID

    @ImplicitReflectionSerializer
    fun publish(): ByteArray? {
        val doc = KDIDDoc.create(name, gender, birthday, phoneNum, socialNum)
        val encoded = doc.encode<KDIDDoc>()

        val authors = arrayOf(Tools.randBytes(20), Tools.randBytes(20), Tools.randBytes(20))
        val payload = TrxDataCreate(
            encoded.toByteArray(),
            authors,
            0
        )
//        val bz = payload.encode<TrxDataCreate>()
//        val tx2 = TrxPayload.decode<TrxDataCreate>(bz)

        val sender = TAssetAccount(
            TKeyStore.create() {
                "1111"
            }
        )
        val tx = Transaction(
            sender.getNonce(),
            0,
            sender.address,
            sender.address,
            BigInteger.TEN,
            Transaction.ACTION_DATACREATE,
            payload.encode<TrxDataCreate>(),
            doc.publicKey.getRawPubKey()!!
        )

        // return txhash
        val ret = sender.publish(tx) {
            "1111"
        }
        return when (ret) {
            is TResult.Success -> ret.value
            is TResult.Error -> null
        }
    }

    fun isPublished(): Boolean {
        return false
    }

    fun update() {
        TODO()
    }

    fun revoke(): Boolean {
        TODO()
    }

    fun allow() {
        TODO()
    }
}