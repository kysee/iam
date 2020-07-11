package com.a2z.kchainlib.kdid

import kotlinx.serialization.*

@Serializable
data class KDIDDoc (
    val id: KDID,
    val publicKey: KDIDPubKey,
    val subject: KDIDSubject
) : KDIDObject<KDIDDoc>()

