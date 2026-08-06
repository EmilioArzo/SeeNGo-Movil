package com.emilionavarro.prueba.dispositivos.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * DeviceDocument.Id y RoutineDocument.Id son ObjectId en el backend, así que
 * llegan como objeto JSON en vez de string. Reconstruimos el hex de 24 chars.
 */
class MongoIdAdapter : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String {
        if (json.isJsonPrimitive) return json.asString

        if (json.isJsonObject) {
            val obj = json.asJsonObject
            return try {
                val timestamp = obj.get("timestamp")?.asLong ?: 0L
                val machine   = obj.get("machine")?.asLong ?: 0L
                val pid       = obj.get("pid")?.asLong ?: 0L
                val increment = obj.get("increment")?.asLong ?: 0L

                val hex = "%08x".format(timestamp and 0xFFFFFFFFL) +
                        "%06x".format(machine and 0xFFFFFFL) +
                        "%04x".format(pid and 0xFFFFL) +
                        "%06x".format(increment and 0xFFFFFFL)

                if (hex.length == 24) hex else ""
            } catch (e: Exception) {
                ""
            }
        }
        return ""
    }
}