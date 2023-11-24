package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorSpace
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public class ComplexOfFreeModules<
    D : Degree,
    BA : BasisName,
    BV : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>>
(
    public val matrixSpace: MatrixSpace<S, V, M>,
    public val degreeGroup: DegreeGroup<D>,
    public val name: String,
    private val getModule: (degree: D) -> FreeModule<BA, BV, S, V, M>,
    private val getDifferential: (degree: D) -> FreeModuleMap<BA, BV, BV, S, V, M>,
) {
    public val underlyingDGVectorSpace: DGVectorSpace<D, FreeModuleBasisName<BA, BV>, S, V, M> by lazy {
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

    public val dgVectorSpaceWithoutCoeff: DGVectorSpace<D, BV, S, V, M> by lazy {
        val gVectorSpace = GVectorSpace(
            numVectorSpace = this.matrixSpace.numVectorSpace,
            degreeGroup = this.degreeGroup,
            name = this.name,
        ) { degree ->
            this.getModule(degree).vectorSpaceWithoutCoeff
        }
        val differential = GLinearMap(
            source = gVectorSpace,
            target = gVectorSpace,
            degree = this.degreeGroup.fromInt(1),
            matrixSpace = this.matrixSpace,
            name = this.name,
        ) { degree ->
            this.getDifferential(degree).inducedMapWithoutCoeff
        }
        DGVectorSpace(gVectorSpace, differential)
    }
}
