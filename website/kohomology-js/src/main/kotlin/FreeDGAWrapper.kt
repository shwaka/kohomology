import com.github.h0tk3y.betterParse.parser.ParseException
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
import com.github.shwaka.kohomology.model.CyclicModel
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.Rational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

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
    private val freeDGAlgebra = FreeDGAlgebra.fromList(
        SparseMatrixSpaceOverRational,
        jsonToGeneratorList(json)
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

    fun setIdeal(idealJson: String) {
        val dgIdeal = this.createIdeal(idealJson)
        this.dgIdeal = dgIdeal
        this.quotDGAlgebra = this.freeDGAlgebra.getQuotientByIdeal(dgIdeal)
    }

    fun tryCreateIdeal(idealJson: String) {
        // for validation
        this.createIdeal(idealJson)
    }

    fun dgaInfo(): Array<StyledMessageKt> {
        return getDGAInfo(this.freeDGAlgebra, "V")
    }

    fun idealInfo(): StyledMessageKt {
        val dgIdeal = this.dgIdeal ?: throw Exception("Ideal is not set")
        val generatorString = dgIdeal.generatorList.joinToString(", ")
        return styledMessage(MessageType.SUCCESS) {
            "($generatorString)".math
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

    fun computeMinimalModel(targetName: String, isomorphismUpTo: Int): Array<StyledMessageKt> {
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
            isomorphismUpTo = isomorphismUpTo
        )
        return getDGAInfo(minimalModel.freeDGAlgebra, "W")
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

private fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
getCocycle(
    freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    cocycleString: String,
): Either<StyledMessageInternal, GVectorOrZero<D, Monomial<D, I>, S, V>> {
    // Since GAlgebra.parse() and FreeGAlgebra.getGeneratorsForParser() are used,
    // the first argument needs to be FreeDGAlgebra and cannot be generalized to DGVectorSpace.
    return try {
        Either.Right(freeDGAlgebra.parse(cocycleString))
    } catch (e: ParseException) {
        val message = styledMessage(MessageType.ERROR) {
            val generatorsString = freeDGAlgebra.getGeneratorsForParser().joinToString(", ") { it.first }
            "[Error] Parse failed.\n".text +
                "Note: Current generators are $generatorsString\n".text +
                "${e.errorResult}\n".text
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
    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
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
                computeCohomologyInternal(dgAlgebra, degree).strings
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
    val p = Printer(printType = PrintType.TEX)
    val freeDGAString = p(freeDGAlgebra)
    val degreeString = freeDGAlgebra.indeterminateList.joinToString(", ") {
        "\\deg{${p(it.name)}} = ${it.degree}"
    }
    val differentialString = freeDGAlgebra.generatorList.joinToString(", ") {
        freeDGAlgebra.context.run {
            "d${p(it)} = ${p(d(it))}"
        }
    }
    return arrayOf(
        styledMessage(MessageType.SUCCESS) {
            "(\\Lambda $generatingVectorSpaceName, d) = ".math + freeDGAString.math
        }.export(),
        styledMessage(MessageType.SUCCESS) {
            degreeString.math
        }.export(),
        styledMessage(MessageType.SUCCESS) {
            differentialString.math
        }.export(),
    )
}
