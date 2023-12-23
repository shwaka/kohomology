package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.StringBasisName

public interface TrivialModuleMapAlongAlgebraMap<
    ES : FiniteMonoidElement,
    ET : FiniteMonoidElement,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMapAlongAlgebraMap<ES, ET, BS, BT, S, V, M> {

    override val source: TrivialModule<ES, BS, S, V, M>
    override val target: TrivialModule<ET, BT, S, V, M>
    override val algebraMap: MonoidRingMap<ES, ET, S, V, M>
    override val moduleMap: TrivialModuleMap<ES, BS, BT, S, V, M>

    public companion object {
        public operator fun <
            ES : FiniteMonoidElement,
            ET : FiniteMonoidElement,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            algebraMap: MonoidRingMap<ES, ET, S, V, M>,
            underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
            source: TrivialModule<ES, BS, S, V, M> = TrivialModule(underlyingLinearMap.source, algebraMap.source),
            target: TrivialModule<ET, BT, S, V, M> = TrivialModule(underlyingLinearMap.target, algebraMap.target),
        ): TrivialModuleMapAlongAlgebraMap<ES, ET, BS, BT, S, V, M> {
            return TrivialModuleMapAlongAlgebraMapImpl(algebraMap, underlyingLinearMap, source, target)
        }

        public operator fun <
            ES : FiniteMonoidElement,
            ET : FiniteMonoidElement,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            monoidMap: FiniteMonoidMap<ES, ET>,
            matrixSpace: MatrixSpace<S, V, M>,
            underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
            source: TrivialModule<ES, BS, S, V, M>? = null,
            target: TrivialModule<ET, BT, S, V, M>? = null,
        ): TrivialModuleMapAlongAlgebraMap<ES, ET, BS, BT, S, V, M> {
            val algebraMap = MonoidRingMap(monoidMap, matrixSpace)
            val sourceNonNull = source ?: TrivialModule(underlyingLinearMap.source, algebraMap.source)
            val targetNonNull = target ?: TrivialModule(underlyingLinearMap.target, algebraMap.target)
            return TrivialModuleMapAlongAlgebraMapImpl(
                algebraMap,
                underlyingLinearMap,
                source = sourceNonNull,
                target = targetNonNull,
            )
        }

        public fun <
            ES : FiniteMonoidElement,
            ET : FiniteMonoidElement,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > baseField(
            algebraMap: MonoidRingMap<ES, ET, S, V, M>,
        ): TrivialModuleMapAlongAlgebraMap<ES, ET, StringBasisName, StringBasisName, S, V, M> {
            val sourceModule = TrivialModule.baseField(algebraMap.source)
            val targetModule = TrivialModule.baseField(algebraMap.target)
            val underlyingLinearMap = LinearMap.fromVectors(
                source = sourceModule.underlyingVectorSpace,
                target = targetModule.underlyingVectorSpace,
                matrixSpace = algebraMap.matrixSpace,
                vectors = targetModule.underlyingVectorSpace.getBasis(),
            )
            return TrivialModuleMapAlongAlgebraMap(
                algebraMap,
                underlyingLinearMap,
                source = sourceModule,
                target = targetModule,
            )
        }
    }
}

private class TrivialModuleMapAlongAlgebraMapImpl<
    ES : FiniteMonoidElement,
    ET : FiniteMonoidElement,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val algebraMap: MonoidRingMap<ES, ET, S, V, M>,
    override val underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
    override val source: TrivialModule<ES, BS, S, V, M> = TrivialModule(underlyingLinearMap.source, algebraMap.source),
    override val target: TrivialModule<ET, BT, S, V, M> = TrivialModule(underlyingLinearMap.target, algebraMap.target),
) : TrivialModuleMapAlongAlgebraMap<ES, ET, BS, BT, S, V, M> {
    override val moduleMap: TrivialModuleMap<ES, BS, BT, S, V, M> by lazy {
        TrivialModuleMap(
            this.underlyingLinearMap,
            source = this.source,
            target = RestrictedTrivialModule(this.target, this.algebraMap)
        )
    }
}
