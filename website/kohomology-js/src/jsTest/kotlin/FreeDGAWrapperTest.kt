import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalJsExport
class FreeDGAWrapperTest {
    @Test
    fun plainStringForCohomology() {
        val json = """
            [
              ["x", 2, "0"],
              ["y", 3, "x^2"]
            ]
        """.trimIndent()
        val freeDGAWrapper = FreeDGAWrapper(json)
        val actual = freeDGAWrapper.computeCohomology("self", 2)
        val expectedPlainString = "\$H^{2} =\\ \$ \$\\mathbb{Q}\\{\$ \$[x]\$ \$\\}\$"
        assertEquals(actual.plainString, expectedPlainString)
    }
}
