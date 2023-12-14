package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

val moduleTag = NamedTag("Module")

class ModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
    val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
    val (x, y) = underlyingVectorSpace.getBasis()
    val (one, t) = coeffAlgebra.getBasis()
    val module = run {
        // Z/2 acting on Q{x, y} by
        //   t*x = y, t*y = x
        val action = ValueBilinearMap(
            source1 = coeffAlgebra,
            source2 = underlyingVectorSpace,
            target = underlyingVectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(x, y), // one*(-)
                listOf(y, x), // t*(-)
            )
        )
        Module(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
    }

    "test action" {
        module.context.run {
            (one * x) shouldBe x
            (one * y) shouldBe y
            (t * x) shouldBe y
            (t * y) shouldBe x
            (t * (x + 2 * y)) shouldBe (y + 2 * x)
        }
    }

    "nested context should work" {
        module.coeffAlgebra.context.run {
            module.context.run {
                ((one + t) * x) shouldBe (x + y)
                ((one - 2 * t) * (3 * x + y)) shouldBe (x - 5 * y)
                ((one + t.pow(3)) * x) shouldBe (x + y)
            }
        }
    }

    "module.findSmallGenerator() should return listOf(x)" {
        val smallGenerator = module.findSmallGenerator()
        smallGenerator shouldHaveSize 1
        smallGenerator shouldBe listOf(x)
    }

    "module.findSmallGenerator(listOf(x)) should return listOf(x)" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(listOf(x))
            smallGenerator shouldHaveSize 1
            smallGenerator shouldBe listOf(x)
        }
    }

    "module.findSmallGenerator(listOf(x+y, x)) should return listOf(x)" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(listOf(x + y, x))
            smallGenerator shouldHaveSize 1
            smallGenerator shouldBe listOf(x)
        }
    }

    "module.findSmallGenerator(listOf(x, x+y)) should return listOf(x)" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(listOf(x, x + y))
            smallGenerator shouldHaveSize 1
            smallGenerator shouldBe listOf(x)
        }
    }

    "module.findSmallGenerator(listOf(2x+y)) should return listOf(2x+y)" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(listOf(2 * x + y))
            smallGenerator shouldHaveSize 1
            smallGenerator shouldBe listOf(2 * x + y)
        }
    }

    "module.findSmallGenerator(listOf(x+y, x-y)) should return listOf(x+y, x-y)" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(listOf(x + y, x - y))
            smallGenerator shouldHaveSize 2
            smallGenerator shouldBe listOf(x + y, x - y)
        }
    }

    "module.findSmallGenerator(emptyList()) should throw IllegalArgumentException" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(emptyList())
            smallGenerator.shouldBeEmpty()
        }
    }

    "module.findSmallGenerator(listOf(x+y)) should throw IllegalArgumentException" {
        module.context.run {
            val smallGenerator = module.findSmallGenerator(listOf(x + y))
            smallGenerator shouldHaveSize 1
            smallGenerator shouldBe listOf(x + y)
        }
    }
})
