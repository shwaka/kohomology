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

enum class ValidationResultType(val typeName: String) {
    SUCCESS("success"),
    ERROR("error"),
}

sealed class ValidationResultInternal(
    val type: ValidationResultType,
    val message: String,
) {
    class Success() : ValidationResultInternal(ValidationResultType.SUCCESS, "")
    class Error(message: String) : ValidationResultInternal(ValidationResultType.ERROR, message)

    @ExperimentalJsExport
    fun export(): ValidationResult {
        return ValidationResult(this.type.typeName, this.message)
    }
}

@ExperimentalJsExport
@JsExport
fun validateJson(json: String): ValidationResult {
    try {
        FreeDGAWrapper(json)
        return ValidationResultInternal.Success().export()
    } catch (e: Exception) {
        val message: String = e.message ?: e.toString()
        return ValidationResultInternal.Error(message).export()
    }
}

@ExperimentalJsExport
@JsExport
fun validateDifferentialValue(
    previousGeneratorsJson: String,
    differentialValue: String,
    expectedDegree: Int,
): ValidationResult {
    val generatorList = jsonToGeneratorList(previousGeneratorsJson)
    val indeterminateList = generatorList.map { generator ->
        Indeterminate(generator.name, generator.degree)
    }
    val freeGAlgebra = FreeGAlgebra(SparseMatrixSpaceOverRational, indeterminateList)
    val gVector = try {
        freeGAlgebra.parse(differentialValue)
    } catch (e: ParseException) {
        val messageFromException: String = e.message ?: e.toString()
        val message = "Failed to parse the value \"$differentialValue\" of the differential " +
            "with the following error message:\n$messageFromException"
        return ValidationResultInternal.Error(message).export()
    } catch (e: Exception) {
        val message: String = e.message ?: e.toString()
        return ValidationResultInternal.Error(message).export()
    }
    if (gVector is GVector && gVector.degree.value != expectedDegree) {
        val message = "Illegal degree: the degree of $differentialValue is ${gVector.degree.value}, " +
            "but ${expectedDegree - 1}+1=$expectedDegree is expected."
        return ValidationResultInternal.Error(message).export()
    }
    return ValidationResultInternal.Success().export()
}
