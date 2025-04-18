import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalJsExport
class ValidateDifferentialValueOfTheLastTest {
    @Test
    fun validJson() {
        val json = """
            [
              { "name": "x", "degree": 2, "differentialValue": "0" },
              { "name": "y", "degree": 3, "differentialValue": "x^2" }
            ]
        """.trimIndent()
        val validationResult = validateDifferentialValueOfTheLast(json)
        assertEquals(validationResult, ValidationResultInternal.Success().export())
    }

    @Test
    fun emptyName() {
        val json = """
            [
              { "name": "", "degree": 2, "differentialValue": "0" },
              { "name": "y", "degree": 3, "differentialValue": "0" }
            ]
        """.trimIndent()
        val validationResult = validateDifferentialValueOfTheLast(json)
        val expected = ValidationResultInternal.NotApplicable("Identifier name must be non-empty.").export()
        assertEquals(validationResult, expected)
    }
}
