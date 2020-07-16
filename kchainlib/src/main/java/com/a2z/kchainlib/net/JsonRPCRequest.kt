package com.a2z.kchainlib.net

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


@Serializable
data class JsonRPCRequest(
    val version: String,
    val id: String,
    val method: String,
    val params: Array<String>?
) {
    constructor(
        method: String,
        params: Array<String>?
    ): this("2.0", "dontcare", method, params)

    constructor(
        method: String
    ): this("2.0", "dontcare", method, null)

    fun encode(): String {
        return Json(jsonConf).stringify(JsonRPCRequest.serializer(), this)
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

        fun decode(s: String): JsonRPCRequest {
            return Json(jsonConf).parse(JsonRPCRequest.serializer(), s)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonRPCRequest

        if (version != other.version) return false
        if (id != other.id) return false
        if (method != other.method) return false
        if (params != null) {
            if (other.params == null) return false
            if (!params.contentEquals(other.params)) return false
        } else if (other.params != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + (params?.contentHashCode() ?: 0)
        return result
    }
}