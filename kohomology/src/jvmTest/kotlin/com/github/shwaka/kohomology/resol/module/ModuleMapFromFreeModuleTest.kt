package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ModuleMapFromFreeModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational

    val coeffAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val (e, t1, t2) = coeffAlgebra.getBasis()

    val generatingBasisNames1 = listOf("x", "y").map { StringBasisName(it) }
    val freeModule = FreeModule(coeffAlgebra, generatingBasisNames1)
    val (x, y) = freeModule.getGeneratingBasis()

    val generatingBasisNames2 = listOf("u", "v").map { StringBasisName(it) }
    val module = FreeModule(coeffAlgebra, generatingBasisNames2)
    val (u, v) = module.getGeneratingBasis()

    "test with the canonical isomorphism" - {
        val moduleMap = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = freeModule,
            target = module,
            values = listOf(u, v),
        )
        "check values on generating basis" {
            moduleMap(x) shouldBe u
            moduleMap(y) shouldBe v
        }
        "check values on basis with coefficient" {
            coeffAlgebra.context.run {
                val arg = freeModule.context.run { (e + t1) * x }
                val expected = module.context.run { (e + t1) * u }
                moduleMap(arg) shouldBe expected
            }
        }
        "moduleMap.kernel() should be zero" {
            moduleMap.kernel().underlyingVectorSpace.dim shouldBe 0
        }
    }

    "test with a map defined with coeff" - {
        val moduleMap = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = freeModule,
            target = module,
            values = module.context.run {
                coeffAlgebra.context.run {
                    listOf((e + t1 + t2) * u, -t1 * v)
                }
            },
        )
        "check values on generating basis" {
            module.context.run {
                coeffAlgebra.context.run {
                    moduleMap(x) shouldBe (e + t1 + t2) * u
                    moduleMap(y) shouldBe (-t1 * v)
                }
            }
        }
    }

    "test with a map with non-zero kernel" - {
        val moduleMap = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = freeModule,
            target = module,
            values = module.context.run {
                listOf(u, -u)
            },
        )

        "check values" {
            moduleMap(x) shouldBe u
            moduleMap(y) shouldBe module.context.run { -u }
            moduleMap(freeModule.context.run { t1 * x }) shouldBe
                module.context.run { t1 * u }
        }

        "moduleMap.kernel() should be of dim 3" {
            moduleMap.kernel().underlyingVectorSpace.dim shouldBe 3
        }

        "moduleMap.kernel().findSmallGenerator() should be listOf(x+y)" {
            val kernel = moduleMap.kernel()
            val r = kernel.retraction
            val smallGenerator = kernel.findSmallGenerator()
            smallGenerator.shouldHaveSize(1)
            smallGenerator shouldBe freeModule.context.run { listOf(r(x + y)) }
        }
    }

    "test constructor with invalid argument" {
        shouldThrow<IllegalArgumentException> {
            ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
                source = freeModule,
                target = module,
                values = emptyList(),
            )
        }
    }

    "test lift" {
        val moduleMap = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = freeModule,
            target = module,
            values = listOf(u, v),
        )
        val generatingBasisNames3 = listOf("a", "b", "c").map { StringBasisName(it) }
        val module3 = FreeModule(coeffAlgebra, generatingBasisNames3)
        val (a, _, c) = module3.getGeneratingBasis()
        val surjection = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = module3,
            target = module,
            values = listOf(u, module.context.run { zeroVector }, v),
        )

        val lift = moduleMap.liftAlong(surjection)

        lift(x) shouldBe a
        lift(y) shouldBe c
    }
})
