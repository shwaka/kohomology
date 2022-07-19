import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

@ExperimentalJsExport
@JsExport
data class ValidationResult(
    val type: String,
    val message: String,
)

@ExperimentalJsExport
@JsExport
fun validateJson(json: String): ValidationResult {
    try {
        FreeDGAWrapper(json)
        return ValidationResult("success", "")
    } catch (e: Exception) {
        val message: String = e.message ?: e.toString()
        return ValidationResult("error", message)
    }
}

@ExperimentalJsExport
@JsExport
fun validateDifferentialValue(
    generatorNames: Array<String>,
    generatorDegrees: Array<Int>,
    differentialValue: String,
    expectedDegree: Int,
): ValidationResult {
    // Can't use Pair for JsExport?
    require(generatorNames.size == generatorDegrees.size) {
        "Size of arrays are different: $generatorNames and $generatorDegrees"
    }
    val indeterminateList = generatorNames.indices.map {
        val name = generatorNames[it]
        val degree = generatorDegrees[it]
        Indeterminate(name, degree)
    }
    val freeGAlgebra = FreeGAlgebra(SparseMatrixSpaceOverRational, indeterminateList)
    val gVector = try {
        freeGAlgebra.parse(differentialValue)
    } catch (e: ParseException) {
        val messageFromException: String = e.message ?: e.toString()
        val message = "Failed to parse the value \"$differentialValue\" of the differential " +
            "with the following error message:\n$messageFromException"
        return ValidationResult("error", message)
    } catch (e: Exception) {
        val message: String = e.message ?: e.toString()
        return ValidationResult("error", message)
    }
    if (gVector is GVector && gVector.degree.value != expectedDegree) {
        val message = "Illegal degree: the degree of $differentialValue is ${gVector.degree.value}, " +
            "but ${expectedDegree - 1}+1=$expectedDegree is expected."
        return ValidationResult("error", message)
    }
    return ValidationResult("success", "")
}
