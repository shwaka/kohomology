import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.ZeroGVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.DerivationDGLieAlgebra
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.GeneratorOfFreeDGA
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.CyclicModel
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift
import com.github.shwaka.kohomology.vectsp.BasisName
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
        FreeDGAlgebra(SparseMatrixSpaceOverRational, generatorList)
    }
    private val freeLoopSpace by lazy { FreeLoopSpace.withShiftDegree(freeDGAlgebra) }
    private val cyclicModel by lazy { CyclicModel(freeDGAlgebra) }
    private val derivationLieAlgebra by lazy { DerivationDGLieAlgebra(freeDGAlgebra) }

    private fun getDGVectorSpace(name: String): DGVectorSpace<*, *, *, *, *> {
        return when (name) {
            "self" -> this.freeDGAlgebra
            "freeLoopSpace" -> this.freeLoopSpace
            "cyclic" -> this.cyclicModel
            "derivation" -> this.derivationLieAlgebra
            else -> throw Exception("Invalid name: $name")
        }
    }

    fun dgaInfo(): Array<StyledMessageKt> {
        val freeDGAString = this.freeDGAlgebra.toString()
        val degreeString = this.freeDGAlgebra.gAlgebra.indeterminateList.joinToString(", ") {
            "\\deg ${it.name} = ${it.degree}"
        }
        val differentialString = this.freeDGAlgebra.gAlgebra.generatorList.joinToString(", ") {
            this.freeDGAlgebra.context.run {
                "d$it = ${d(it)}"
            }
        }
        return arrayOf(
            styledMessage(MessageType.SUCCESS) {
                "(\\wedge V, d) = ".math + freeDGAString.math
            }.export(),
            styledMessage(MessageType.SUCCESS) {
                degreeString.math
            }.export(),
            styledMessage(MessageType.SUCCESS) {
                differentialString.math
            }.export(),
        )
    }

    fun computationHeader(targetName: String): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return computationHeader(targetDGVectorSpace)
    }

    // cohomology だと js で cohomology_0 に変換されてしまう
    fun computeCohomology(targetName: String, degree: Int): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return computeCohomology(targetDGVectorSpace, degree)
    }

    fun computeCohomologyUpTo(targetName: String, minDegree: Int, maxDegree: Int): Array<StyledMessageKt> {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return computeCohomologyUpTo(targetDGVectorSpace, minDegree, maxDegree)
    }

    fun computeCohomologyClass(targetName: String, cocycleString: String): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return if (targetDGVectorSpace is FreeDGAlgebra<*, *, *, *, *>) {
            computeCohomologyClass(targetDGVectorSpace, cocycleString)
        } else {
            styledMessage(MessageType.ERROR) {
                "Cannot compute class for $targetName".text
            }.export()
        }
    }
}

@ExperimentalJsExport
fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomology(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    degree: Int,
): StyledMessageKt {
    val p = Printer(PrintConfig(printType = PrintType.TEX, showShift = ShowShift.BAR))
    val basis = if (dgVectorSpace.degreeGroup is IntDegreeGroup) {
        dgVectorSpace.cohomology.getBasis(degree)
    } else {
        dgVectorSpace.cohomology.getBasisForAugmentedDegree(degree)
    }
    // val vectorSpaceString = if (basis.isEmpty()) "0" else {
    //     val basisString = basis.joinToString(", ") { p(it) }
    //     "\\mathbb{Q}\\{$basisString\\}"
    // }
    // return styledMessage(MessageType.SUCCESS) {
    //     "H^{$degree} = $vectorSpaceString".math // これだと "," の部分で改行されない
    // }.export()
    return styledMessage(MessageType.SUCCESS) {
        val vectorSpace: List<StyledStringInternal> = if (basis.isEmpty()) {
            "0".math
        } else {
            // katex は + などの二項演算の部分でしか改行してくれない (See displayMode in https://katex.org/docs/options.html)
            // "," の部分で改行できるように要素を分ける
            "\\mathbb{Q}\\{".math +
                basis.dropLast(1).map { "${p(it)},\\ ".math }.flatten() +
                p(basis.last()).math + // 最後の要素だけは "," を追加しない
                "\\}".math
        }
        "H^{$degree} =\\ ".math + vectorSpace
    }.export()
}

@ExperimentalJsExport
fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computationHeader(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
): StyledMessageKt {
    return styledMessage(MessageType.SUCCESS) {
        val printed = if (dgVectorSpace is Printable) {
            val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
            p(dgVectorSpace).math
        } else {
            dgVectorSpace.toString().text
        }
        "Cohomology of ".text + printed + " is".text
    }.export()
}

@ExperimentalJsExport
fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyUpTo(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    minDegree: Int,
    maxDegree: Int,
): Array<StyledMessageKt> {
    val messages = mutableListOf(computationHeader(dgVectorSpace))
    for (degree in minDegree..maxDegree) {
        messages.add(computeCohomology(dgVectorSpace, degree))
    }
    return messages.toTypedArray()
}

@ExperimentalJsExport
fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyClass(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycleString: String
): StyledMessageKt {
    // Since GAlgebra.parse() is used, this cannot be extended to DGVectorSpace.
    val cocycle: GVectorOrZero<D, Monomial<D, I>, S, V> = freeDGAlgebra.gAlgebra.parse(cocycleString)
    return when (cocycle) {
        is ZeroGVector -> styledMessage(MessageType.SUCCESS) { "The cocycle is zero.".text }.export()
        is GVector -> computeCohomologyClass(freeDGAlgebra, cocycle)
    }
}

@ExperimentalJsExport
fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyClass(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycle: GVector<D, Monomial<D, I>, S, V>
): StyledMessageKt {
    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
    freeDGAlgebra.context.run {
        if (d(cocycle).isNotZero()) {
            return styledMessage(MessageType.ERROR) {
                p(cocycle).math + " is not a cocycle: ".text + "d(${p(cocycle)}) = ${p(d(cocycle))}".math
            }.export()
        }
        return styledMessage(MessageType.SUCCESS) {
            "[${p(cocycle)}] = ${p(cocycle.cohomologyClass())}".math
        }.export()
    }
}
