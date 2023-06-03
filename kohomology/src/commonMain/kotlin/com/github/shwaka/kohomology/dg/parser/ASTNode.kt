package com.github.shwaka.kohomology.dg.parser

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.ZeroGVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

// Node of Abstract Syntax Tree
internal sealed interface ASTNode {
    fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
        gAlgebra: GAlgebra<D, B, S, V, M>,
        generators: List<Pair<String, GVector<D, B, S, V>>>
    ): GVectorOrZero<D, B, S, V>

    object Zero : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): ZeroGVector<D, B, S, V> {
            return gAlgebra.zeroGVector
        }
    }

    data class Generator(val name: String) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            return generators.find { it.first == this.name}?.second
                ?: throw Exception("Invalid generator name: ${this.name}")
        }
    }

    data class Fraction(val numerator: Int, val denominator: Int) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            if (this.numerator == 0) {
                return gAlgebra.zeroGVector
            }
            val scalar: S = gAlgebra.field.fromIntPair(this.numerator, this.denominator)
            return gAlgebra.context.run {
                unit * scalar
            }
        }
    }

    data class UnaryMinus(val value: ASTNode) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            return gAlgebra.context.run {
                -this@UnaryMinus.value.getValue(gAlgebra, generators)
            }
        }
    }

    data class Power(val base: ASTNode, val exponent: Int) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            val baseValue = this.base.getValue(gAlgebra, generators)
            return gAlgebra.context.run {
                baseValue.pow(this@Power.exponent)
            }
        }
    }

    data class Multiply(val left: ASTNode, val right: ASTNode) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            val leftValue = this.left.getValue(gAlgebra, generators)
            val rightValue = this.right.getValue(gAlgebra, generators)
            return gAlgebra.context.run {
                leftValue * rightValue
            }
        }
    }

    data class Subtract(val left: ASTNode, val right: ASTNode) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            val leftValue = this.left.getValue(gAlgebra, generators)
            val rightValue = this.right.getValue(gAlgebra, generators)
            return gAlgebra.context.run {
                leftValue - rightValue
            }
        }
    }

    data class Sum(val left: ASTNode, val right: ASTNode) : ASTNode {
        override fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getValue(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            generators: List<Pair<String, GVector<D, B, S, V>>>
        ): GVectorOrZero<D, B, S, V> {
            val leftValue = this.left.getValue(gAlgebra, generators)
            val rightValue = this.right.getValue(gAlgebra, generators)
            return gAlgebra.context.run {
                leftValue + rightValue
            }
        }
    }
}
