import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.ZeroGVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.DerivationDGLieAlgebra
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.CyclicModel
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class FreeDGAWrapper(json: String) {
    private val freeDGAlgebra = FreeDGAlgebra.fromList(
        SparseMatrixSpaceOverRational,
        jsonToGeneratorList(json)
    )
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
        val degreeString = this.freeDGAlgebra.indeterminateList.joinToString(", ") {
            "\\deg{${it.name}} = ${it.degree}"
        }
        val differentialString = this.freeDGAlgebra.generatorList.joinToString(", ") {
            val p = Printer(printType = PrintType.TEX)
            this.freeDGAlgebra.context.run {
                "d$it = ${p(d(it))}"
            }
        }
        return arrayOf(
            styledMessage(MessageType.SUCCESS) {
                "(\\Lambda V, d) = ".math + freeDGAString.math
            }.export(),
            styledMessage(MessageType.SUCCESS) {
                degreeString.math
            }.export(),
            styledMessage(MessageType.SUCCESS) {
                differentialString.math
            }.export(),
        )
    }

    fun computationHeader(targetName: String, minDegree: Int, maxDegree: Int): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return computationHeader(targetDGVectorSpace, minDegree, maxDegree).export()
    }

    // cohomology だと js で cohomology_0 に変換されてしまう
    fun computeCohomology(targetName: String, degree: Int): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return computeCohomology(targetDGVectorSpace, degree).export()
    }

    fun computeCohomologyDim(targetName: String, degree: Int): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        val dim = getBasis(targetDGVectorSpace, degree).size
        return styledMessage(MessageType.SUCCESS) {
            "\\mathrm{dim}H^{$degree} = $dim".math
        }.export()
    }

    fun computeCohomologyClass(targetName: String, cocycleString: String, showBasis: Boolean): StyledMessageKt {
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        return if (targetDGVectorSpace is FreeDGAlgebra<*, *, *, *, *>) {
            computeCohomologyClass(targetDGVectorSpace, cocycleString, showBasis).export()
        } else {
            styledMessage(MessageType.ERROR) {
                "Cannot compute class for $targetName".text
            }.export()
        }
    }
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getBasis(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    degree: Int,
): List<GVector<D, SubQuotBasis<B, S, V>, S, V>> {
    return if (dgVectorSpace.degreeGroup is IntDegreeGroup) {
        dgVectorSpace.cohomology.getBasis(degree)
    } else {
        dgVectorSpace.cohomology.getBasisForAugmentedDegree(degree)
    }
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyInternal(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    degree: Int,
): StyledMessageInternal {
    val p = Printer(PrintConfig(printType = PrintType.TEX, showShift = ShowShift.BAR))
    val basis = getBasis(dgVectorSpace, degree)
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
    }
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomology(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    degree: Int,
): StyledMessageInternal {
    return computeCohomologyInternal(dgVectorSpace, degree)
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computationHeader(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    minDegree: Int,
    maxDegree: Int,
): StyledMessageInternal {
    return styledMessage(MessageType.SUCCESS) {
        val printed = if (dgVectorSpace is Printable) {
            val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
            val dgVectorSpaceString = p(dgVectorSpace)
            if (dgVectorSpaceString.startsWith("(") && dgVectorSpaceString.endsWith(")")) {
                "H^n${p(dgVectorSpace)}".math
            } else {
                "H^n(${p(dgVectorSpace)})".math
            }
        } else {
            "H^n($dgVectorSpace)".text
        }
        "Computing ".text + printed + " for ".text +
            "$minDegree \\leq n \\leq $maxDegree".math
    }
}

private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyClass(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycleString: String,
    showBasis: Boolean,
): StyledMessageInternal {
    // Since GAlgebra.parse() is used, this cannot be extended to DGVectorSpace.
    val printer = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S)
    val cocycle: GVectorOrZero<D, Monomial<D, I>, S, V> = try {
        freeDGAlgebra.parse(cocycleString, printer)
    } catch (e: ParseException) {
        return styledMessage(MessageType.ERROR) {
            val generatorsString = freeDGAlgebra.getGeneratorsForParser(printer).joinToString(", ") { it.first }
            "[Error] Parse failed.\n".text +
                "Note: Current generators are $generatorsString\n".text +
                "${e.errorResult}\n".text
        }
    }
    return when (cocycle) {
        is ZeroGVector -> styledMessage(MessageType.SUCCESS) { "The cocycle is zero.".text }
        is GVector -> computeCohomologyClass(freeDGAlgebra, cocycle, showBasis)
    }
}

private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyClass(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycle: GVector<D, Monomial<D, I>, S, V>,
    showBasis: Boolean,
): StyledMessageInternal {
    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
    freeDGAlgebra.context.run {
        if (d(cocycle).isNotZero()) {
            return styledMessage(MessageType.ERROR) {
                p(cocycle).math + " is not a cocycle: ".text + "d(${p(cocycle)}) = ${p(d(cocycle))}".math
            }
        }
        val degree = freeDGAlgebra.degreeGroup.context.run {
            augmentation(cocycle.degree)
        }
        return styledMessage(MessageType.SUCCESS) {
            val cohomologyString = if (showBasis) {
                computeCohomologyInternal(freeDGAlgebra, degree).strings
            } else {
                "H^$degree".math
            }
            "[${p(cocycle)}] = ${p(cocycle.cohomologyClass())} \\in ".math + cohomologyString
        }
    }
}
