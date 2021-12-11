import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.GeneratorOfFreeDGA
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.model.UseBar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.PrintType
import com.github.shwaka.kohomology.vectsp.Printer
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
@Suppress("UNUSED")
class FreeDGAWrapper(val json: String) {
    private val freeDGAlgebra = run {
        val serializableGeneratorList = jsonToGeneratorList(json)
        val generatorList = serializableGeneratorList.map {
            GeneratorOfFreeDGA(it.name, it.degree, it.differentialValue)
        }
        FreeDGAlgebra(SparseMatrixSpaceOverBigRational, generatorList)
    }
    private val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
    fun dgaInfo(): StyledMessageKt {
        val freeDGAString = this.freeDGAlgebra.toString()
        val degreeString = this.freeDGAlgebra.gAlgebra.indeterminateList.joinToString(", ") {
            "\\deg ${it.name} = ${it.degree}"
        }
        return styledMessage(MessageType.SUCCESS) {
            freeDGAString.math + " with ".normal + degreeString.math
        }.export()
    }
    // cohomology だと js で cohomology_0 に変換されてしまう
    fun computeCohomology(degree: Int): StyledMessageKt {
        val basis = this.freeDGAlgebra.cohomology.getBasis(degree)
        val vectorSpaceString = if (basis.isEmpty()) "0" else {
            val basisString = basis.joinToString(", ") { it.toString() }
            "\\mathbb{Q}\\{$basisString\\}"
        }
        return styledMessage(MessageType.SUCCESS) {
            "H^{$degree} = $vectorSpaceString".math
        }.export()
    }
    fun computeCohomologyUpTo(maxDegree: Int): Array<StyledMessageKt> {
        return computeCohomologyUpTo(this.freeDGAlgebra, maxDegree)
    }
    fun computeCohomologyOfFreeLoopSpaceUpTo(maxDegree: Int): Array<StyledMessageKt> {
        return computeCohomologyUpTo(this.freeLoopSpace, maxDegree)
    }
}

@ExperimentalJsExport
fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyUpTo(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    maxDegree: Int
): Array<StyledMessageKt> {
    val p = Printer(PrintConfig(printType = PrintType.TEX, useBar = UseBar.ONE))
    val messages = mutableListOf(
        styledMessage(MessageType.SUCCESS) {
            "Cohomology of ".normal + freeDGAlgebra.toString().math + " is".normal
        }.export()
    )
    for (degree in 0..maxDegree) {
        val basis = freeDGAlgebra.cohomology.getBasis(degree)
        val vectorSpaceString = if (basis.isEmpty()) "0" else {
            val basisString = basis.joinToString(", ") { p(it) }
            "\\mathbb{Q}\\{$basisString\\}"
        }
        messages.add(
            styledMessage(MessageType.SUCCESS) { "H^{$degree} = $vectorSpaceString".math }.export()
        )
    }
    return messages.toTypedArray()
}
