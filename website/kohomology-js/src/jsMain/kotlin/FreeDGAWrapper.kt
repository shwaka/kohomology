import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGIdeal
import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.QuotDGAlgebra
import com.github.shwaka.kohomology.dg.ZeroGVector
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.parser.KohomologyParseException
import com.github.shwaka.kohomology.free.DerivationDGLieAlgebra
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.MinimalModel
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.SparseMatrix
import com.github.shwaka.kohomology.linalg.SparseNumVector
import com.github.shwaka.kohomology.model.CopiedNamePrintConfig
import com.github.shwaka.kohomology.model.CyclicModel
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.model.ShowShift
import com.github.shwaka.kohomology.specific.Rational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import styled.*

typealias MyDGIdeal = DGIdeal<
    IntDegree,
    Monomial<IntDegree, StringIndeterminateName>,
    Rational,
    SparseNumVector<Rational>, SparseMatrix<Rational>
    >

typealias MyQuotDGAlgebra = QuotDGAlgebra<
    IntDegree,
    Monomial<IntDegree, StringIndeterminateName>,
    Rational,
    SparseNumVector<Rational>,
    SparseMatrix<Rational>
    >

@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class FreeDGAWrapper(json: String) {
    private val freeDGAlgebra = FreeDGAlgebra.fromJson(
        SparseMatrixSpaceOverRational,
        json
    )
    private val freeLoopSpace by lazy { FreeLoopSpace.withShiftDegree(freeDGAlgebra) }
    private val cyclicModel by lazy { CyclicModel(freeDGAlgebra) }
    private val derivationLieAlgebra by lazy { DerivationDGLieAlgebra(freeDGAlgebra) }
    private var dgIdeal: MyDGIdeal? = null
    private var quotDGAlgebra: MyQuotDGAlgebra? = null

    private fun getDGVectorSpace(name: String): DGVectorSpace<*, *, *, *, *> {
        return when (name) {
            "self" -> this.freeDGAlgebra
            "freeLoopSpace" -> this.freeLoopSpace
            "cyclic" -> this.cyclicModel
            "derivation" -> this.derivationLieAlgebra
            "idealQuot" -> this.quotDGAlgebra ?: throw Exception("ideal is not set")
            else -> throw Exception("Invalid name: $name")
        }
    }

    fun tryParseIdealGeneratorString(generatorString: String) {
        // for validation
        this.freeDGAlgebra.parse(generatorString)
    }

    @kotlinx.serialization.ExperimentalSerializationApi
    private fun createIdeal(idealJson: String): DGIdeal<
        IntDegree,
        Monomial<IntDegree, StringIndeterminateName>,
        Rational,
        SparseNumVector<Rational>, SparseMatrix<Rational>
        > {
        val generators = jsonToIdealGenerators(idealJson).map { generatorString ->
            this.freeDGAlgebra.parse(generatorString)
        }.mapNotNull { gVectorOrZero ->
            // filterIsInstance<GVector<...>>() may be more suitable,
            // but this requires long type arguments
            when (gVectorOrZero) {
                is GVector -> gVectorOrZero
                is ZeroGVector -> null
            }
        }
        return this.freeDGAlgebra.getDGIdeal(generators)
    }

    @kotlinx.serialization.ExperimentalSerializationApi
    fun setIdeal(idealJson: String) {
        val dgIdeal = this.createIdeal(idealJson)
        this.dgIdeal = dgIdeal
        this.quotDGAlgebra = this.freeDGAlgebra.getQuotientByIdeal(dgIdeal)
    }

    @kotlinx.serialization.ExperimentalSerializationApi
    fun tryCreateIdeal(idealJson: String) {
        // for validation
        this.createIdeal(idealJson)
    }

    fun dgaInfo(): Array<StyledMessageKt> {
        return getDGAInfo(this.freeDGAlgebra, "V")
    }

    fun idealInfo(): StyledMessageKt {
        val dgIdeal = this.dgIdeal ?: throw Exception("Ideal is not set")
        val prefix = "I = "
        if (dgIdeal.generatorList.isEmpty()) {
            return styledMessage(MessageType.SUCCESS) {
                "${prefix}0".math
            }.export()
        }
        val generatorString = dgIdeal.generatorList.joinToString(", ")
        return styledMessage(MessageType.SUCCESS) {
            "$prefix($generatorString)".math
        }.export()
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
        return when (val targetDGVectorSpace = this.getDGVectorSpace(targetName)) {
            is FreeDGAlgebra<*, *, *, *, *> ->
                computeCohomologyClass(targetDGVectorSpace, cocycleString, showBasis).export()
            is QuotDGAlgebra<*, *, *, *, *> -> @Suppress("UNCHECKED_CAST") {
                targetDGVectorSpace as MyQuotDGAlgebra
                computeCohomologyClassInQuotient(
                    this.freeDGAlgebra,
                    targetDGVectorSpace,
                    cocycleString,
                    showBasis
                ).export()
            }
            else -> styledMessage(MessageType.ERROR) {
                "Cannot compute class for $targetName".text
            }.export()
        }
    }

    fun computeMinimalModel(
        targetName: String,
        isomorphismUpTo: Int,
        reportProgress: (
            currentIsomorphismUpTo: Int,
            targetIsomorphismUpTo: Int,
            currentNumberOfGenerators: Int,
        ) -> Unit
    ): Array<StyledMessageKt> {
        if (targetName == "freeLoopSpace") {
            return arrayOf(
                styledMessage(MessageType.ERROR) {
                    "Minimal model of free loop space is currently not supported.".text
                }.export()
            )
        }
        val targetDGVectorSpace = this.getDGVectorSpace(targetName)
        if (targetDGVectorSpace !is DGAlgebra<*, *, *, *, *>) {
            return arrayOf(
                styledMessage(MessageType.ERROR) {
                    "Minimal model can be computed only for DGAs.".text
                }.export()
            )
        }
        if (targetDGVectorSpace.degreeGroup != IntDegreeGroup) {
            return arrayOf(
                styledMessage(MessageType.ERROR) {
                    "Internal error! This can't happen!".text
                }.export()
            )
        }
        @Suppress("UNCHECKED_CAST")
        targetDGVectorSpace as DGAlgebra<IntDegree, *, *, *, *>
        val minimalModel = MinimalModel.of(
            targetDGAlgebra = targetDGVectorSpace,
            isomorphismUpTo = isomorphismUpTo,
        ) { progress ->
            reportProgress(
                progress.currentIsomorphismUpTo,
                progress.targetIsomorphismUpTo,
                progress.currentNumberOfGenerators,
            )
        }
        return arrayOf(
            styledMessage(MessageType.SUCCESS) {
                val p = Printer(PrintType.TEX)
                "The minimal model of ".text +
                    p(targetDGVectorSpace).math + " is".text
            }.export()
        ) + getDGAInfo(minimalModel.freeDGAlgebra, "W")
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

// To allow line breaks after `separator`, each element forms a single StyledStringInternal.
// Ref: "displayMode" in https://katex.org/docs/options.html:
// > In inline mode, KaTeX allows line breaks after outermost relations (like = or <)
// > or binary operators (like + or \times), the same as TeX.
private fun <T> List<T>.joinToStyledMathString(
    separator: String,
    transform: (T) -> String,
): List<StyledStringGroup> {
    if (this.isEmpty()) {
        return emptyList()
    }
    return this.dropLast(1).map {
        StyledStringGroup.math(transform(it) + separator)
    } + listOf(
        StyledStringGroup.math(
            transform(this.last()) // Don't add `separator` for the last element.
        )
    )
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeCohomologyInternal(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
    degree: Int,
): StyledMessageInternal {
    val p = Printer(printType = PrintType.TEX) { register(CopiedNamePrintConfig(ShowShift.BAR)) }
    val basis = getBasis(dgVectorSpace, degree)
    // val vectorSpaceString = if (basis.isEmpty()) "0" else {
    //     val basisString = basis.joinToString(", ") { p(it) }
    //     "\\mathbb{Q}\\{$basisString\\}"
    // }
    // return styledMessage(MessageType.SUCCESS) {
    //     "H^{$degree} = $vectorSpaceString".math // これだと "," の部分で改行されない
    // }.export()
    return styledMessage(MessageType.SUCCESS) {
        val vectorSpace: List<StyledStringGroup> = if (basis.isEmpty()) {
            "0".math
        } else {
            "\\mathbb{Q}\\{".math +
                basis.joinToStyledMathString(",\\ ") { p(it) } +
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
    val options: MessageOptionsInternal = if (dgVectorSpace is FreeDGAlgebra<*, *, *, *, *>) {
        MessageOptionsInternal(
            dgaJson = dgVectorSpace.toJson(),
        )
    } else {
        MessageOptionsInternal()
    }
    return styledMessage(MessageType.SUCCESS) {
        val p = Printer(printType = PrintType.TEX) { register(CopiedNamePrintConfig(ShowShift.BAR)) }
        val dgVectorSpaceWithoutParen: String = ParenParser.removeSurroundingParen(p(dgVectorSpace))
        val printed = "H^n($dgVectorSpaceWithoutParen)".math
        "Computing ".text + printed + " for ".text +
            "$minDegree \\leq n \\leq $maxDegree".math
    }.withOptions(options)
}

private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
getCocycle(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycleString: String,
): Either<StyledMessageInternal, GVectorOrZero<D, Monomial<D, I>, S, V>> {
    // Since GAlgebra.parse() and FreeGAlgebra.getGeneratorsForParser() are used,
    // the first argument needs to be FreeDGAlgebra and cannot be generalized to DGVectorSpace.
    return try {
        Either.Right(freeDGAlgebra.parse(cocycleString))
    } catch (e: KohomologyParseException) {
        val message = styledMessage(MessageType.ERROR) {
            val generatorsString = freeDGAlgebra.getGeneratorsForParser().joinToString(", ") { it.first }
            "[Error] Parse failed.\n".text +
                "Note: Current generators are $generatorsString\n".text +
                "${e.getErrorResult()}\n".text
        }
        Either.Left(message)
    }
}

private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
computeCohomologyClass(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycleString: String,
    showBasis: Boolean,
): StyledMessageInternal {
    return when (val cocycleOrMessage = getCocycle(freeDGAlgebra, cocycleString)) {
        is Either.Left -> cocycleOrMessage.value
        is Either.Right -> when (val cocycle = cocycleOrMessage.value) {
            is ZeroGVector -> styledMessage(MessageType.SUCCESS) { "The cocycle is zero.".text }
            is GVector -> computeCohomologyClass(freeDGAlgebra, cocycle, showBasis)
        }
    }
}

private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
computeCohomologyClassInQuotient(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    quotDGAlgebra: QuotDGAlgebra<D, Monomial<D, I>, S, V, M>,
    cocycleString: String,
    showBasis: Boolean,
): StyledMessageInternal {
    return when (val cocycleOrMessage = getCocycle(freeDGAlgebra, cocycleString)) {
        is Either.Left -> cocycleOrMessage.value
        is Either.Right -> when (val cocycle = cocycleOrMessage.value) {
            is ZeroGVector -> styledMessage(MessageType.SUCCESS) { "The cocycle is zero.".text }
            is GVector -> {
                val proj = quotDGAlgebra.projection
                val quotCocycle = proj(cocycle)
                computeCohomologyClass(quotDGAlgebra, quotCocycle, showBasis)
            }
        }
    }
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
computeCohomologyClass(
    dgAlgebra: DGAlgebra<D, B, S, V, M>,
    cocycle: GVector<D, B, S, V>,
    showBasis: Boolean,
): StyledMessageInternal {
    val degreeGroup = dgAlgebra.degreeGroup
    if (degreeGroup !is AugmentedDegreeGroup) {
        return styledMessage(MessageType.ERROR) {
            "Internal error: degreeGroup is not AugmentedDegreeGroup.".text
        }
    }
    val p = Printer(printType = PrintType.TEX) { register(CopiedNamePrintConfig(ShowShift.BAR)) }
    dgAlgebra.context.run {
        if (d(cocycle).isNotZero()) {
            return styledMessage(MessageType.ERROR) {
                p(cocycle).math + " is not a cocycle: ".text + "d(${p(cocycle)}) = ${p(d(cocycle))}".math
            }
        }
        val degree = degreeGroup.context.run {
            augmentation(cocycle.degree)
        }
        return styledMessage(MessageType.SUCCESS) {
            val cohomologyString = if (showBasis) {
                computeCohomologyInternal(dgAlgebra, degree).groups
            } else {
                "H^$degree".math
            }
            "[${p(cocycle)}] = ${p(cocycle.cohomologyClass())} \\in ".math + cohomologyString
        }
    }
}

@ExperimentalJsExport
private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDGAInfo(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    generatingVectorSpaceName: String,
): Array<StyledMessageKt> {
    val dgaJson: String = freeDGAlgebra.toJson()
    val p = Printer(printType = PrintType.TEX)
    return arrayOf(
        styledMessage(MessageType.SUCCESS) {
            "(\\Lambda $generatingVectorSpaceName, d) = ".math +
                "(\\Lambda(".math +
                freeDGAlgebra.indeterminateList.joinToStyledMathString(",\\ ") { p(it) } +
                "), d)".math
        }.withOptions(MessageOptionsInternal(dgaJson = dgaJson)).export(),
        styledMessage(MessageType.SUCCESS) {
            freeDGAlgebra.indeterminateList.joinToStyledMathString(",\\ ") {
                "\\deg{${p(it.name)}} = ${it.degree}"
            }
        }.export(),
        styledMessage(MessageType.SUCCESS) {
            freeDGAlgebra.context.run {
                freeDGAlgebra.generatorList.joinToStyledMathString(",\\ ") {
                    "d${p(it)} = ${p(d(it))}"
                }
            }
        }.export(),
    )
}
