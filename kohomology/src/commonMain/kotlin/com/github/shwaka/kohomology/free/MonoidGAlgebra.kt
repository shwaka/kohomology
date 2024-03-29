package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GBilinearMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorSpace
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.free.monoid.GMonoid
import com.github.shwaka.kohomology.free.monoid.GMonoidElement
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

private class MonoidGAlgebraFactory<D : Degree, E : GMonoidElement<D>, Mon : GMonoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val degreeGroup: DegreeGroup<D>,
    val monoid: Mon,
    val name: String,
    val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<E, S>
) {
    private val cache: MutableMap<D, VectorSpace<E, S, V>> = mutableMapOf()

    val gVectorSpace: GVectorSpace<D, E, S, V> = GVectorSpace(
        this.matrixSpace.numVectorSpace,
        this.degreeGroup,
        this.name,
        this.getInternalPrintConfig,
        this::listDegreesForAugmentedDegree,
        this.monoid.boundedness,
        this::getVectorSpace,
    )

    val multiplication: GBilinearMap<E, E, E, D, S, V, M> = GBilinearMap(
        this.matrixSpace,
        this.gVectorSpace,
        this.gVectorSpace,
        this.gVectorSpace,
        0,
        "Multiplication(${this.name})",
    ) { p, q -> this.getMultiplication(p, q) }

    val unit: GVector<D, E, S, V> = this.gVectorSpace.fromVector(
        this.getVectorSpace(0).fromBasisName(this.monoid.unit),
        0,
    )

    private fun getBasisNames(degree: D): List<E> {
        return this.monoid.listElements(degree)
    }

    private fun getVectorSpace(degree: D): VectorSpace<E, S, V> {
        return this.cache.getOrPut(degree) {
            VectorSpace(this.matrixSpace.numVectorSpace, this.getBasisNames(degree))
        }
    }

    private fun getVectorSpace(degree: Int): VectorSpace<E, S, V> {
        return this.getVectorSpace(this.degreeGroup.fromInt(degree))
    }

    private fun getMultiplication(p: D, q: D): BilinearMap<E, E, E, S, V, M> {
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

    private fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        return this.monoid.listDegreesForAugmentedDegree(augmentedDegree)
    }
}

public interface MonoidGAlgebra<D : Degree, E : GMonoidElement<D>, Mon : GMonoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GAlgebra<D, E, S, V, M> {
    public val monoid: Mon

    override fun getIdentity(): GAlgebraMap<D, E, E, S, V, M> {
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }

    public companion object {
        public operator fun <D : Degree, E : GMonoidElement<D>, Mon : GMonoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
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

private class MonoidGAlgebraImpl<D : Degree, E : GMonoidElement<D>, Mon : GMonoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: MonoidGAlgebraFactory<D, E, Mon, S, V, M>,
) : MonoidGAlgebra<D, E, Mon, S, V, M>,
    GVectorSpace<D, E, S, V> by factory.gVectorSpace {
    override val matrixSpace: MatrixSpace<S, V, M> = factory.matrixSpace
    override val multiplication: GBilinearMap<E, E, E, D, S, V, M> = factory.multiplication
    override val unit: GVector<D, E, S, V> = factory.unit
    override val isCommutative: Boolean = factory.monoid.isCommutative
    override val monoid: Mon = factory.monoid
    override val context: GAlgebraContext<D, E, S, V, M> = GAlgebraContext(this)
    override val underlyingGVectorSpace: GVectorSpace<D, E, S, V> = factory.gVectorSpace
    override val underlyingGAlgebra: GAlgebra<D, E, S, V, M> = this

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
