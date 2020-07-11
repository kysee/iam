package com.a2z.kchainlib.kdid

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.serializer


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