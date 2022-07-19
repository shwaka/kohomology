import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.GeneratorOfFreeDGA
import com.github.shwaka.kohomology.util.IntAsDegree
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
private data class SerializableGenerator(val name: String, val degree: IntAsDegree, val differentialValue: String)

private object GeneratorSerializer : JsonTransformingSerializer<SerializableGenerator>(SerializableGenerator.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonArray) {
            JsonObject(
                mapOf(
                    "name" to element[0],
                    "degree" to element[1],
                    "differentialValue" to element[2],
                )
            )
        } else {
            element
        }
    }
}

private fun jsonToSerializableGeneratorList(json: String): List<SerializableGenerator> {
    return Json.decodeFromString(ListSerializer(GeneratorSerializer), json)
}

fun jsonToGeneratorList(json: String): List<GeneratorOfFreeDGA<IntDegree>> {
    val serializableGeneratorList = jsonToSerializableGeneratorList(json)
    return serializableGeneratorList.map {
        GeneratorOfFreeDGA(it.name, it.degree, it.differentialValue)
    }
}
