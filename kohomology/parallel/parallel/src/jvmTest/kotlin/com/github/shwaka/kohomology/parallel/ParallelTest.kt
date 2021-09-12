package com.github.shwaka.kohomology.parallel

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val parallelTag = NamedTag("Parallel")

class ParallelTest : FreeSpec({
    tags(parallelTag)

    "pmap should return the same result as map" {
        val intList = listOf(1, 4, 2, 3)
        val transform: (Int) -> Int = { it * 10 }
        intList.pmap(transform) shouldBe intList.map(transform)
    }

    "pforEach should do the same as forEach" {
        val intList = listOf(1, 4, 2, 3)
        val list1 = mutableListOf<Int>()
        intList.pforEach { list1.add(it) }
        val list2 = mutableListOf<Int>()
        intList.forEach { list2.add(it) }
        list1 shouldBe list2
    }
})
