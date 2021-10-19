package com.github.shwaka.kohomology

import java.io.File

fun generateComponentN(max: Int, rootDir: File) {
    val definitions = "package com.github.shwaka.kohomology.util.list\n\n" + (6..max).joinToString("\n\n") { n ->
        """
            /** Returns ${n}th element from the list. */
            public operator fun <T> List<T>.component$n(): T = this[${n - 1}]
        """.trimIndent()
    } + "\n"
    val tests = run {
        """
            package com.github.shwaka.kohomology.util.list

            import io.kotest.core.NamedTag
            import io.kotest.core.spec.style.FreeSpec
            import io.kotest.matchers.shouldBe

            val componentNTag = NamedTag("ComponentN")

            class ComponentNTest : FreeSpec({
                tags(componentNTag)

                val stringList = (0..$max).map { "foo${'$'}it" }
        """.trimIndent() +
            "\n\n" +
            (6..max).joinToString("\n\n") { n ->
                """
                    |    "test component$n" {
                    |        stringList.component$n() shouldBe "foo${n - 1}"
                    |    }
                """.trimMargin()
            } +
            "\n})\n"
    }
    val imports = "import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports\n\n" +
        (6..max).joinToString("\n") { n ->
            "import com.github.shwaka.kohomology.util.list.component$n"
        }

    val packageDir = "kotlin/com/github/shwaka/kohomology/util/list"
    val mainFile = rootDir.resolve("src/commonMain/$packageDir/componentN.kt")
    val testFile = rootDir.resolve("src/jvmTest/$packageDir/ComponentNTest.kt")

    // print to mainFile
    mainFile.parentFile.mkdirs()
    mainFile.writeText(definitions)

    // print to testFile
    testFile.parentFile.mkdirs()
    testFile.writeText(tests)

    // print imports
    println(imports)

    // print messages
    println(
        """

            Copy the above code to import the extension function 'componentN()'.

            The following files are edited:
                $mainFile
                $testFile
        """.trimIndent()
    )
}
