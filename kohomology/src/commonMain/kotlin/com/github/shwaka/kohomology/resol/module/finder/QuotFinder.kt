package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.QuotModuleAlongAlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.Vector

public class QuotFinder<
    BAS : BasisName,
    BAT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    AlgS : Algebra<BAS, S, V, M>,
    AlgT : Algebra<BAT, S, V, M>,
    >(
    private val coeffAlgebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    private val sourceCoeffAlgebra: AlgS,
    private val targetCoeffAlgebra: AlgT,
    private val finderOnQuot: SmallGeneratorFinder<BAT, S, V, M, AlgT>,
    private val requireAdditionalGenerator: Boolean,
) : SmallGeneratorFinder<BAS, S, V, M, AlgS> {
    init {
        require(coeffAlgebraMap.source == sourceCoeffAlgebra) {
            "coeffAlgebraMap.source and sourceCoeffAlgebra must be the same"
        }
        require(coeffAlgebraMap.target == targetCoeffAlgebra) {
            "coeffAlgebraMap.target and targetCoeffAlgebra must be the same"
        }
    }

    override fun <B : BasisName> find(module: Module<BAS, B, S, V, M>): List<Vector<B, S, V>> {
        require(module.coeffAlgebra == this.sourceCoeffAlgebra) {
            "Coefficient algebra is expected to be ${this.sourceCoeffAlgebra}, " +
                "but ${module.coeffAlgebra} was given"
        }
        val quotModule = QuotModuleAlongAlgebraMap(module, this.coeffAlgebraMap)
        val quotGenerator = this.finderOnQuot.find(quotModule)
        val maybeGenerator = quotGenerator.map { quotModule.section(it) }
        return if (this.requireAdditionalGenerator) {
            EarlyReturnSelector(this.sourceCoeffAlgebra).select(module, maybeGenerator)
        } else {
            maybeGenerator
        }
    }
}
