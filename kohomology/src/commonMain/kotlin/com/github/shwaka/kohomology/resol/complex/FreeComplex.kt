package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorSpace
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface FreeComplex<
    D : Degree,
    BA : BasisName,
    BV : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>
    > : Complex<D, BA, FreeModuleBasisName<BA, BV>, S, V, M> {

    override fun getModule(degree: D): FreeModule<BA, BV, S, V, M>
    override fun getDifferential(degree: D): FreeModuleMap<BA, BV, BV, S, V, M>

    override fun getModule(degree: Int): FreeModule<BA, BV, S, V, M> {
        return this.getModule(this.degreeGroup.fromInt(degree))
    }
    override fun getDifferential(degree: Int): FreeModuleMap<BA, BV, BV, S, V, M> {
        return this.getDifferential(this.degreeGroup.fromInt(degree))
    }

    public val tensorWithBaseField: DGVectorSpace<D, BV, S, V, M>

    public companion object {
        public operator fun <D : Degree, BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getModule: (degree: D) -> FreeModule<BA, BV, S, V, M>,
            getDifferential: (degree: D) -> FreeModuleMap<BA, BV, BV, S, V, M>,
        ): FreeComplex<D, BA, BV, S, V, M> {
            return FreeComplexImpl(matrixSpace, degreeGroup, name, getModule, getDifferential)
        }
    }
}

private class FreeComplexImpl<
    D : Degree,
    BA : BasisName,
    BV : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>>
(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val degreeGroup: DegreeGroup<D>,
    override val name: String,
    getModule: (degree: D) -> FreeModule<BA, BV, S, V, M>,
    getDifferential: (degree: D) -> FreeModuleMap<BA, BV, BV, S, V, M>,
) : FreeComplex<D, BA, BV, S, V, M> {
    private val _getModule: (degree: D) -> FreeModule<BA, BV, S, V, M> = getModule
    private val _getDifferential: (degree: D) -> FreeModuleMap<BA, BV, BV, S, V, M> = getDifferential

    override fun getModule(degree: D): FreeModule<BA, BV, S, V, M> {
        return this._getModule(degree)
    }

    override fun getDifferential(degree: D): FreeModuleMap<BA, BV, BV, S, V, M> {
        return this._getDifferential(degree)
    }

    override val underlyingDGVectorSpace: DGVectorSpace<D, FreeModuleBasisName<BA, BV>, S, V, M> by lazy {
        val gVectorSpace = GVectorSpace(
            numVectorSpace = this.matrixSpace.numVectorSpace,
            degreeGroup = this.degreeGroup,
            name = this.name,
        ) { degree ->
            this.getModule(degree).underlyingVectorSpace
        }
        val differential = GLinearMap(
            source = gVectorSpace,
            target = gVectorSpace,
            degree = this.degreeGroup.fromInt(1),
            matrixSpace = this.matrixSpace,
            name = this.name,
        ) { degree ->
            this.getDifferential(degree).underlyingLinearMap
        }
        DGVectorSpace(gVectorSpace, differential)
    }

    override val tensorWithBaseField: DGVectorSpace<D, BV, S, V, M> by lazy {
        val gVectorSpace = GVectorSpace(
            numVectorSpace = this.matrixSpace.numVectorSpace,
            degreeGroup = this.degreeGroup,
            name = this.name,
        ) { degree ->
            this.getModule(degree).tensorWithBaseField
        }
        val differential = GLinearMap(
            source = gVectorSpace,
            target = gVectorSpace,
            degree = this.degreeGroup.fromInt(1),
            matrixSpace = this.matrixSpace,
            name = this.name,
        ) { degree ->
            this.getDifferential(degree).tensorWithBaseField
        }
        DGVectorSpace(gVectorSpace, differential)
    }
}
