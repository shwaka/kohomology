package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace
import mu.KotlinLogging

private class MonoidGAlgebraFactory<D : Degree, E : MonoidElement<D>, Mon : Monoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val degreeGroup: DegreeGroup<D>,
    val monoid: Mon,
    val name: String,
    val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<E, S>
) {
    private val cache: MutableMap<D, VectorSpace<E, S, V>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    private fun getBasisNames(degree: D): List<E> {
        return this.monoid.listElements(degree)
    }

    fun getVectorSpace(degree: D): VectorSpace<E, S, V> {
        this.cache[degree]?.let {
            // if cache exists
            this.logger.debug { "cache found for ${this.monoid}[$degree]" }
            return it
        }
        // if cache does not exist
        this.logger.debug { "cache not found for ${this.monoid}[$degree], create new instance" }
        val vectorSpace = VectorSpace(this.matrixSpace.numVectorSpace, this.getBasisNames(degree))
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    fun getVectorSpace(degree: Int): VectorSpace<E, S, V> = this.getVectorSpace(this.degreeGroup.fromInt(degree))

    fun getMultiplication(p: D, q: D): BilinearMap<E, E, E, S, V, M> {
        val source1 = this.getVectorSpace(p)
        val source2 = this.getVectorSpace(q)
        val target = this.getVectorSpace(this.degreeGroup.context.run { p + q })
        val valueList = source1.basisNames.map { monoidElement1 ->
            source2.basisNames.map { monoidElement2 ->
                this.monoid.multiply(monoidElement1, monoidElement2).let { maybeZero ->
                    when (maybeZero) {
                        is Zero -> target.zeroVector
                        is NonZero -> {
                            val (monoidElement: E, sign: Sign) = maybeZero.value
                            val vectorWithoutSign = target.fromBasisName(monoidElement)
                            when (sign) {
                                1 -> vectorWithoutSign
                                -1 -> target.context.run { -vectorWithoutSign }
                                else -> throw Exception("This can't happen!")
                            }
                        }
                    }
                }
            }
        }
        return BilinearMap(source1, source2, target, this.matrixSpace, valueList)
    }

    fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        return this.monoid.listDegreesForAugmentedDegree(augmentedDegree)
    }

    val unitVector: Vector<E, S, V> = this.getVectorSpace(0).fromBasisName(this.monoid.unit)
}

open class MonoidGAlgebra<D : Degree, E : MonoidElement<D>, Mon : Monoid<D, E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: MonoidGAlgebraFactory<D, E, Mon, S, V, M>,
) : GAlgebra<D, E, S, V, M>(
    factory.matrixSpace,
    factory.degreeGroup,
    factory.name,
    factory::getVectorSpace,
    factory::getMultiplication,
    factory.unitVector,
    listDegreesForAugmentedDegree = factory::listDegreesForAugmentedDegree,
    getInternalPrintConfig = factory.getInternalPrintConfig
) {
    val monoid: Mon = factory.monoid

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
