import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.GeneratorOfFreeDGA
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.IntAsDegree
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class SerializableGenerator(val name: String, val degree: IntAsDegree, val differentialValue: String)

object GeneratorSerializer : JsonTransformingSerializer<SerializableGenerator>(SerializableGenerator.serializer()) {
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

private fun jsonToGeneratorList(json: String): List<SerializableGenerator> {
    return Json.decodeFromString(ListSerializer(GeneratorSerializer), json)
}

@ExperimentalJsExport
@JsExport
class Text(val type: String, val content: String)

@ExperimentalJsExport
@JsExport
fun computeCohomology(json: String, maxDegree: Int): Array<Text> {
    val serializableGeneratorList = jsonToGeneratorList(json)
    val generatorList = serializableGeneratorList.map {
        GeneratorOfFreeDGA(it.name, it.degree, it.differentialValue)
    }
    val freeDGAlgebra = FreeDGAlgebra(SparseMatrixSpaceOverBigRational, generatorList)
    val lines: MutableList<Text> = mutableListOf(Text("normal", "Computation result:"))
    for (degree in 0..maxDegree) {
        val basis = freeDGAlgebra.cohomology.getBasis(degree)
        val vectorSpaceString = if (basis.isEmpty()) "0" else {
            val basisString = basis.joinToString(", ") { it.toString() }
            "\\mathbb{Q}\\{$basisString\\}"
        }
        // this.props.printlnFun("\\(H^{$degree} = $vectorSpaceString\\)")
        lines.add(Text("math", "H^{$degree} = $vectorSpaceString"))
    }
    return lines.toTypedArray()
}
