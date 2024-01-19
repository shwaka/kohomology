package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorContext
import com.github.shwaka.kohomology.vectsp.VectorContextImpl
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface ModuleContext<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    VectorContext<B, S, V> {
    public val module: Module<BA, B, S, V, M>

    public operator fun Vector<BA, S, V>.times(other: Vector<B, S, V>): Vector<B, S, V> {
        return this@ModuleContext.module.act(this, other)
    }

    public companion object {
        public operator fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            module: Module<BA, B, S, V, M>,
        ): ModuleContext<BA, B, S, V, M> {
            return ModuleContextImpl(module)
        }
    }
}

private class ModuleContextImpl<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val module: Module<BA, B, S, V, M>,
) : ModuleContext<BA, B, S, V, M>,
    VectorContext<B, S, V> by VectorContextImpl(module.underlyingVectorSpace)

public interface Module<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val underlyingVectorSpace: VectorSpace<B, S, V>
    public val coeffAlgebra: Algebra<BA, S, V, M>
    public val context: ModuleContext<BA, B, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
    public val action: BilinearMap<BA, B, B, S, V, M>

    public fun act(a: Vector<BA, S, V>, b: Vector<B, S, V>): Vector<B, S, V> {
        return this.action(a, b)
    }

    public fun generateSubVectorSpaceOverCoefficient(
        generator: List<Vector<B, S, V>>
    ): SubVectorSpace<B, S, V, M> {
        val subVectorSpace = SubVectorSpace(
            matrixSpace = this.matrixSpace,
            totalVectorSpace = this.underlyingVectorSpace,
            generator = generator,
        )
        return this.action.image(source2Sub = subVectorSpace)
    }

    public fun findSmallGenerator(
        generator: List<Vector<B, S, V>>? = null,
        finder: SmallGeneratorFinder = SmallGeneratorFinder.default,
    ): List<Vector<B, S, V>> {
        val generatorNonNull = generator ?: this.underlyingVectorSpace.getBasis()
        return finder.find(this, generatorNonNull)
    }

    public companion object {
        public operator fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            underlyingVectorSpace: VectorSpace<B, S, V>,
            coeffAlgebra: Algebra<BA, S, V, M>,
            action: BilinearMap<BA, B, B, S, V, M>,
        ): Module<BA, B, S, V, M> {
            return ModuleImpl(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
        }
    }
}

private class ModuleImpl<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val underlyingVectorSpace: VectorSpace<B, S, V>,
    override val coeffAlgebra: Algebra<BA, S, V, M>,
    override val action: BilinearMap<BA, B, B, S, V, M>,
) : Module<BA, B, S, V, M> {
    override val context: ModuleContext<BA, B, S, V, M> = ModuleContextImpl(this)
}
