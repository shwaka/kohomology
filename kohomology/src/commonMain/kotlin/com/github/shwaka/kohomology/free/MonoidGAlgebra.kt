package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraImpl
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.free.monoid.Monoid
import com.github.shwaka.kohomology.free.monoid.MonoidElement
import com.github.shwaka.kohomology.free.monoid.Signed
import com.github.shwaka.kohomology.free.monoid.Zero
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

private class MonoidGAlgebraFactory<D : Degree, E : MonoidElement<D>, Mon : Monoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val degreeGroup: DegreeGroup<D>,
    val monoid: Mon,
    val name: String,
    val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<E, S>
) {
    private val cache: MutableMap<D, VectorSpace<E, S, V>> = mutableMapOf()

    private fun getBasisNames(degree: D): List<E> {
        return this.monoid.listElements(degree)
    }

    fun getVectorSpace(degree: D): VectorSpace<E, S, V> {
        this.cache[degree]?.let {
            // if cache exists
            return it
        }
        // if cache does not exist
        val vectorSpace = VectorSpace(this.matrixSpace.numVectorSpace, this.getBasisNames(degree))
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    fun getVectorSpace(degree: Int): VectorSpace<E, S, V> = this.getVectorSpace(this.degreeGroup.fromInt(degree))

    fun getMultiplication(p: D, q: D): BilinearMap<E, E, E, S, V, M> {
        val source1 = this.getVectorSpace(p)
        val source2 = this.getVectorSpace(q)
        val target = this.getVectorSpace(this.degreeGroup.context.run { p + q })
        return ValueBilinearMap(source1, source2, target, this.matrixSpace, this.generateGetValue(target))
    }

    private fun generateGetValue(target: VectorSpace<E, S, V>): (E, E) -> Vector<E, S, V> {
        return { monoidElement1, monoidElement2 ->
            this.monoid.multiply(monoidElement1, monoidElement2).let { signedOrZero ->
                when (signedOrZero) {
                    is Zero -> target.zeroVector
                    is Signed -> {
                        val (monoidElement: E, sign: Sign) = signedOrZero
                        val vectorWithoutSign = target.fromBasisName(monoidElement)
                        when (sign) {
                            Sign.PLUS -> vectorWithoutSign
                            Sign.MINUS -> target.context.run { -vectorWithoutSign }
                        }
                    }
                }
            }
        }
    }

    fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        return this.monoid.listDegreesForAugmentedDegree(augmentedDegree)
    }

    val unitVector: Vector<E, S, V> = this.getVectorSpace(0).fromBasisName(this.monoid.unit)
}

public interface MonoidGAlgebra<D : Degree, E : MonoidElement<D>, Mon : Monoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GAlgebra<D, E, S, V, M> {
    public val monoid: Mon

    public companion object {
        public operator fun <D : Degree, E : MonoidElement<D>, Mon : Monoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            monoid: Mon,
            name: String,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<E, S> = InternalPrintConfig.Companion::default,
        ): MonoidGAlgebra<D, E, Mon, S, V, M> {
            return MonoidGAlgebraImpl(matrixSpace, degreeGroup, monoid, name, getInternalPrintConfig)
        }
    }
}

private class MonoidGAlgebraImpl<D : Degree, E : MonoidElement<D>, Mon : Monoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: MonoidGAlgebraFactory<D, E, Mon, S, V, M>,
) : MonoidGAlgebra<D, E, Mon, S, V, M>,
    GAlgebra<D, E, S, V, M> by GAlgebraImpl(
        factory.matrixSpace,
        factory.degreeGroup,
        factory.name,
        factory::getVectorSpace,
        factory::getMultiplication,
        factory.unitVector,
        listDegreesForAugmentedDegree = factory::listDegreesForAugmentedDegree,
        getInternalPrintConfig = factory.getInternalPrintConfig
    ) {
    override val monoid: Mon = factory.monoid

    constructor(
        matrixSpace: MatrixSpace<S, V, M>,
        degreeGroup: DegreeGroup<D>,
        monoid: Mon,
        name: String,
        getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<E, S> = InternalPrintConfig.Companion::default,
    ) : this(
        MonoidGAlgebraFactory(matrixSpace, degreeGroup, monoid, name, getInternalPrintConfig),
    )
}
