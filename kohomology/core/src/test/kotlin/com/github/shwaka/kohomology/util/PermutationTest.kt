package com.github.shwaka.kohomology.util

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

val permutationTag = NamedTag("Permutation")

class PermutationTest : StringSpec({
    tags(permutationTag)
    "Permutations of [1, 2,..., n] should have length n!" {
        for (n in 0..5) {
            val list = (1..n).toList()
            var count = 0
            for ((_, _) in getPermutation(list)) {
                count += 1
            }
            val nFactorial = if (n == 0) 1 else (1..n).reduce { a, b -> a * b }
            count shouldBe nFactorial
        }
    }
})
