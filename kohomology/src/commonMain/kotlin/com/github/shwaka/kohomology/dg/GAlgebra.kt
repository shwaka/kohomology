package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.parser.ASTNode
import com.github.shwaka.kohomology.dg.parser.GAlgebraElementASTGrammar
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public interface GAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagmaContext<D, B, S, V, M> {
    public val gAlgebra: GAlgebra<D, B, S, V, M>
    public val unit: GVector<D, B, S, V>
        get() = this.gAlgebra.unit

    public fun GVector<D, B, S, V>.pow(exponent: Int): GVector<D, B, S, V> {
        val unit = this@GAlgebraContext.gAlgebra.unit
        return when {
            exponent == 0 -> unit
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else unit
                half * half * rem
            }
            exponent < 0 -> throw ArithmeticException("Negative power in an algebra is not defined")
            else -> throw Exception("This can't happen!")
        }
    }
    public fun GVectorOrZero<D, B, S, V>.pow(exponent: Int): GVectorOrZero<D, B, S, V> {
        return when (this) {
            is GVector -> this.pow(exponent)
            is ZeroGVector -> when {
                exponent == 0 -> this@GAlgebraContext.gAlgebra.unit
                exponent > 0 -> this@GAlgebraContext.gAlgebra.zeroGVector
                exponent < 0 -> throw ArithmeticException("Negative power in an algebra is not defined")
                else -> throw Exception("This can't happen!")
            }
        }
    }
    public fun GVectorOrZero<D, B, S, V>.toScalar(): S {
        return when (this) {
            is GVector -> this@GAlgebraContext.gAlgebra.convertToScalar(this)
            is ZeroGVector -> zero
        }
    }
    public fun Iterable<GVector<D, B, S, V>>.product(): GVector<D, B, S, V> {
        return this.fold(this@GAlgebraContext.gAlgebra.unit) { acc, x -> acc * x }
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gAlgebra: GAlgebra<D, B, S, V, M>,
        ): GAlgebraContext<D, B, S, V, M> {
            return GAlgebraContextImpl(gAlgebra)
        }
    }
}

private class GAlgebraContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val gAlgebra: GAlgebra<D, B, S, V, M>,
) : GAlgebraContext<D, B, S, V, M>,
    GMagmaContext<D, B, S, V, M> by GMagmaContext(gAlgebra)

public interface GAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagma<D, B, S, V, M> {
    public val unit: GVector<D, B, S, V>
    public val isCommutative: Boolean
    public override val context: GAlgebraContext<D, B, S, V, M>
    public val underlyingGAlgebra: GAlgebra<D, B, S, V, M>

    override fun getIdentity(): GAlgebraMap<D, B, B, S, V, M> {
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }

    public fun convertToScalar(gVector: GVector<D, B, S, V>): S {
        if (gVector.degree.isNotZero()) {
            throw ArithmeticException(
                "Cannot convert $gVector to a scalar since it has non-zero degree ${gVector.degree}"
            )
        }
        return this.divideByGVector(gVector, this.unit)
            ?: throw ArithmeticException(
                "Cannot convert $gVector to a scalar since it is not of a multiple of the unit"
            )
    }

    public fun parse(
        generatorList: List<Pair<String, GVector<D, B, S, V>>>,
        text: String,
    ): GVectorOrZero<D, B, S, V> {
        val astNode: ASTNode = GAlgebraElementASTGrammar.parseToEnd(text)
        return this.getValueFromASTNode(generatorList, astNode)
        // The following was commented out since it printed the message in tests.
        // try {
        //     return grammar.parseToEnd(text)
        // } catch (exception: ParseException) {
        //     val generatorsString = generators.joinToString(", ") { it.first }
        //     println("[Error] Failed to parse text.")
        //     println("  Expected generators are: $generatorsString")
        //     throw exception
        // }
    }

    public fun getIdeal(generatorList: List<GVector<D, B, S, V>>): Ideal<D, B, S, V, M> {
        val generatingSubGVectorSpace = SubGVectorSpace.fromList(
            this.matrixSpace,
            this,
            "IdealGenerator($generatorList)",
            generatorList,
        )
        val idealAsSubGVectorSpace = if (this.isCommutative) {
            // If commutative, then it is enough to consider the image of I⊗A.
            this.multiplication.image(source1Sub = generatingSubGVectorSpace)
        } else {
            // If non-commutative, then we need to consider the image of A⊗I⊗A.
            val rightIdeal = this.multiplication.image(
                source1Sub = generatingSubGVectorSpace,
            )
            this.multiplication.image(
                source2Sub = rightIdeal,
            )
        }
        return Ideal(
            totalGAlgebra = this,
            subGVectorSpace = idealAsSubGVectorSpace,
            generatorList = generatorList,
        )
    }

    public fun getQuotientByIdeal(ideal: SubGVectorSpace<D, B, S, V, M>): QuotGAlgebra<D, B, S, V, M> {
        val quotGVectorSpace = QuotGVectorSpace(
            this.matrixSpace,
            totalGVectorSpace = this,
            quotientGenerator = ideal,
        )
        val multiplication = this.multiplication.induce(
            source1Quot = quotGVectorSpace,
            source2Quot = quotGVectorSpace,
            targetQuot = quotGVectorSpace,
        )
        val unit = quotGVectorSpace.projection(this.unit)
        return QuotGAlgebra(
            this.matrixSpace,
            quotGVectorSpace,
            multiplication,
            unit,
        )
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            gVectorSpace: GVectorSpace<D, B, S, V>,
            multiplication: GBilinearMap<B, B, B, D, S, V, M>,
            unit: GVector<D, B, S, V>,
            isCommutative: Boolean = false,
        ): GAlgebra<D, B, S, V, M> {
            return GAlgebraImpl(matrixSpace, gVectorSpace, multiplication, unit, isCommutative)
        }
    }
}

private class GAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val multiplication: GBilinearMap<B, B, B, D, S, V, M>,
    override val unit: GVector<D, B, S, V>,
    override val isCommutative: Boolean,
) : GAlgebra<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {
    override val context: GAlgebraContext<D, B, S, V, M> = GAlgebraContext(this)
    override val underlyingGAlgebra: GAlgebra<D, B, S, V, M> = this
}

public class InvalidIdentifierException(
    public val identifierName: String,
    public val validIdentifierNames: List<String>,
) : Exception(
    """
        Invalid generator name: $identifierName
        Valid names are: ${validIdentifierNames.joinToString(", ")}
    """.trimIndent()
)

// The function getValueFromASTNode is implemented as an extension function
// since `internal` cannot be applied to default implementation of methods in an interface.
internal fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
GAlgebra<D, B, S, V, M>.getValueFromASTNode(
    generatorList: List<Pair<String, GVector<D, B, S, V>>>,
    astNode: ASTNode,
): GVectorOrZero<D, B, S, V> {
    return when (astNode) {
        is ASTNode.Zero -> this.zeroGVector
        is ASTNode.Identifier -> generatorList.find { it.first == astNode.name }?.second
            ?: throw InvalidIdentifierException(
                identifierName = astNode.name,
                validIdentifierNames = generatorList.map { it.first },
            )
        is ASTNode.NatNumber -> {
            if (astNode.value == 0) {
                this.zeroGVector
            } else {
                val scalar = this.field.fromInt(astNode.value)
                this.context.run {
                    scalar * unit
                }
            }
        }
        is ASTNode.Divide -> {
            val numeratorValue = this.getValueFromASTNode(generatorList, astNode.numerator)
            val denominatorValue = this.getValueFromASTNode(generatorList, astNode.denominator)
            this.context.run {
                numeratorValue * (1 / denominatorValue.toScalar())
            }
        }
        is ASTNode.UnaryMinus -> {
            val valueWithoutMinus = this.getValueFromASTNode(generatorList, astNode.value)
            this.context.run {
                -valueWithoutMinus
            }
        }
        is ASTNode.Power -> {
            val baseValue = this.getValueFromASTNode(generatorList, astNode.base)
            this.context.run {
                baseValue.pow(astNode.exponent)
            }
        }
        is ASTNode.Multiply -> {
            val leftValue = this.getValueFromASTNode(generatorList, astNode.left)
            val rightValue = this.getValueFromASTNode(generatorList, astNode.right)
            this.context.run { leftValue * rightValue }
        }
        is ASTNode.Subtract -> {
            val leftValue = this.getValueFromASTNode(generatorList, astNode.left)
            val rightValue = this.getValueFromASTNode(generatorList, astNode.right)
            this.context.run { leftValue - rightValue }
        }
        is ASTNode.Sum -> {
            val leftValue = this.getValueFromASTNode(generatorList, astNode.left)
            val rightValue = this.getValueFromASTNode(generatorList, astNode.right)
            this.context.run { leftValue + rightValue }
        }
    }
}
