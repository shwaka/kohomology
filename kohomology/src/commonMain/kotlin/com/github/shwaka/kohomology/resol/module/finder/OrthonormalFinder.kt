package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.asSubVectorSpace

public class OrthonormalFinder<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    Alg : Algebra<BA, S, V, M>,
    >(
    private val coeffAlgebra: Alg,
    private val orthonormalSystem: List<Vector<BA, S, V>>,
) : SmallGeneratorFinder<BA, S, V, M, Alg> {
    init {
        coeffAlgebra.context.run {
            for (e in orthonormalSystem) {
                require(coeffAlgebra.contains(e)) {
                    "$e is not an element of $coeffAlgebra"
                }
                require(e * e == e) {
                    "Elements in orthonormalSystem must be idempotent, but $e * $e = ${e * e}"
                }
            }
            for (e1 in orthonormalSystem) {
                for (e2 in orthonormalSystem) {
                    if (e1 != e2) {
                        require((e1 * e2).isZero()) {
                            "Elements in orthonormalSystem must be orthogonal, but $e1 * $e2 = ${e1 * e2} != 0"
                        }
                    }
                }
            }
            for (e in orthonormalSystem) {
                for (v in coeffAlgebra.getBasis()) {
                    require(e * v == v * e) {
                        "Elements in orthonormalSystem must be in center, " +
                            "but $e * $v = ${e * v} != ${v * e} = $v * $e"
                    }
                }
            }
            require(orthonormalSystem.sum() == unit) {
                "Sum of elements in orthonormalSystem must be the unit, but was ${orthonormalSystem.sum()}"
            }
        }
    }

    override fun <B : BasisName> find(module: Module<BA, B, S, V, M>): List<Vector<B, S, V>> {
        val basisList = this.orthonormalSystem.map { this.findBasisFor(module, it) }
        // orthonormalSystem is non-empty since its sum is non-zero.
        // So basisList is also non-empty
        val n = basisList.maxOfOrNull { it.size } ?: throw Exception("This can't happen!")
        val result = mutableListOf<Vector<B, S, V>>()
        for (i in 0 until n) {
            val elements: List<Vector<B, S, V>> = basisList.map { basis ->
                basis.getOrElse(i) {
                    module.underlyingVectorSpace.zeroVector
                }
            }
            val sum = module.context.run {
                elements.sum()
            }
            result.add(sum)
        }
        return result
    }

    private fun <B : BasisName> findBasisFor(
        module: Module<BA, B, S, V, M>,
        idempotent: Vector<BA, S, V>,
    ): List<Vector<B, S, V>> {
        val matrixSpace = module.matrixSpace
        val idempotentSpan = SubVectorSpace(
            matrixSpace = matrixSpace,
            totalVectorSpace = this.coeffAlgebra,
            generator = listOf(idempotent),
        )
        val subVectorSpace = module.action.image(
            source1Sub = idempotentSpan,
            source2Sub = module.underlyingVectorSpace.asSubVectorSpace(matrixSpace),
        )
        return subVectorSpace.getBasis().map { subVectorSpace.inclusion(it) }
    }
}
