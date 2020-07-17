package com.a2z.kchainlib.net

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.stringify
import org.json.JSONArray
import org.json.JSONObject


@Serializable
data class JsonRPCReq(
    val version: String,
    val id: String,
    val method: String,
    val params: JsonElement? = null
) {
    constructor(
        method: String,
        params: List<JsonPrimitive>
    ): this("2.0", "dontcare", method, JsonArray((params)))

    constructor(
        method: String
    ): this("2.0", "dontcare", method)

    fun encode(): String {
        return Json(jsonConf).stringify<JsonRPCReq>(serializer(), this)
    }

    companion object {
        private val jsonConf: JsonConfiguration = JsonConfiguration(
//            encodeDefaults = true,
//            ignoreUnknownKeys = false,
//            isLenient = false,
//            serializeSpecialFloatingPointValues = false,
//            allowStructuredMapKeys = true,
            prettyPrint = true,
//            unquotedPrint = false,
            indent = "  "
//            useArrayPolymorphism = false,
//            classDiscriminator = JsonConfiguration.defaultDiscriminator
        )

        fun decode(s: String): JsonRPCReq {
            return Json(jsonConf).parse<JsonRPCReq>(serializer(), s)
        }
    }
}