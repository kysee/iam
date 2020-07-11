package com.a2z.kdid

import com.a2z.kdid.msg.KDID

class KID (val name: String,
           val gender: String,
           val birthday: String,
           val phoneNum: String,
           val socialNum: String
) {

    private lateinit var kdid: KDID

    //
    // user side
    fun publish(): ByteArray {
        TODO()
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

    //
    // verifier side
    fun verify(): Boolean {
        TODO()
    }

    fun requestSubjectInfo(reqInfos: List<String>) {
        TODO()
    }

    fun subjectFields(): List<String> {
        TODO()
    }
}