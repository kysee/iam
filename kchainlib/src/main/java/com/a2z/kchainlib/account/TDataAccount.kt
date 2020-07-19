package com.a2z.kchainlib.account

import android.os.Build
import androidx.annotation.RequiresApi
import com.a2z.kchainlib.common.TResult
import com.a2z.kchainlib.common.exhaustive
import com.a2z.kchainlib.net.Node
import java.util.*

class TDataAccount(
    val address: ByteArray
) {
    var text: ByteArray? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun query(n: Node = Node.default): TResult.Error? {
        return when (val ret = n.account(address)) {
            is TResult.Success -> {
                this.text = Base64.getDecoder().decode(ret.value.getPrimitive("text").content)
                null
            }
            is TResult.Error -> ret
        }.exhaustive
    }
}