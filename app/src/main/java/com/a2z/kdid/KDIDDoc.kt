package com.a2z.kdid

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

open class KDIDObject<T> {

    @ImplicitReflectionSerializer
    inline fun <reified T> encode(context: SerialModule = EmptyModule): String {
        return Json(jsonConf, context).stringify(serializer(), this as T)
    }

    companion object {
        val jsonConf: JsonConfiguration = JsonConfiguration(
//            encodeDefaults = true,
//            ignoreUnknownKeys = false,
//            isLenient = false,
//            serializeSpecialFloatingPointValues = false,
//            allowStructuredMapKeys = true,
            prettyPrint = true,
//            unquotedPrint = false,
            indent = "    "
//            useArrayPolymorphism = false,
//            classDiscriminator = JsonConfiguration.defaultDiscriminator
        )

        @ImplicitReflectionSerializer
        inline fun <reified T> decode(d: String, context: SerialModule = EmptyModule) : T {
            return Json(jsonConf, context).parse(serializer(), d)
        }
    }
}


@Serializable
data class KDID (
    val method: String,
    val idstring: String
) : KDIDObject<KDID>() {
    fun did(): String {
        return "did:$method:$idstring"
    }

    @Serializer(forClass = KDID::class)
    companion object : KSerializer<KDID> {
        override fun serialize(encoder: Encoder, obj: KDID) {
            encoder.encodeString(obj.did())
        }

        override fun deserialize(decoder: Decoder): KDID {
            val str = decoder.decodeString()
            val arr = str.split(":")
            return KDID(arr[1], arr[2])
        }
    }
}

@Serializable
data class KDIDSubject (
    val name: String,
    val gender: String,
    val birthday: String,
    val phoneNum: String,
    val socialNum: String
) : KDIDObject<KDIDSubject>()


@Serializable
data class KDIDDoc (
    val id: KDID,
    val publicKey: KDIDPubKey,
    val subject: KDIDSubject
) : KDIDObject<KDIDDoc>()

