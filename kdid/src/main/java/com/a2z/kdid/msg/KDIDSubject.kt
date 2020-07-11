package com.a2z.kdid.msg

import kotlinx.serialization.Serializable


@Serializable
data class KDIDSubject (
    val name: String,
    val gender: String,
    val birthday: String,
    val phoneNum: String,
    val socialNum: String
) : KDIDObject<KDIDSubject>()
