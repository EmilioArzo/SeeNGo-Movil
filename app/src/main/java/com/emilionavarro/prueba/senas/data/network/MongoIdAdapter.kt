package com.emilionavarro.prueba.senas.data.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * El backend serializa GestureDocument.Id (tipo ObjectId de MongoDB) como un
 * objeto JSON en vez de un string plano, porque no está declarado como
 * `string` con [BsonRepresentation(BsonType.ObjectId)] (a diferencia de
 * UserDocument). No podemos editar Program.cs, así que reconstruimos aquí
 * el id original de 24 caracteres hex a partir de los mismos campos que
 * componen un ObjectId.
 *
 * NOTA: si esto no arma un id de 24 chars, revisa en Logcat (tag OkHttp,
 * ver GesturesRetrofitClient) los nombres exactos que manda el backend y
 * ajusta los "get(...)" de abajo.
 */
class MongoIdAdapter : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String {
        // Caso normal: ya es un string (id válido, o cualquier otro campo String del DTO)
        if (json.isJsonPrimitive) return json.asString

        // Caso roto: el backend mandó el ObjectId como objeto
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