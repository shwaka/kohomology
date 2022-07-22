import com.github.h0tk3y.betterParse.parser.AlternativesFailure
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.NoMatchingToken
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.ZeroGVector
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.GeneratorOfFreeDGA
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

private fun getErrorMessageForPreviousGenerators(
    generatorList: List<GeneratorOfFreeDGA<IntDegree>>
): String {
    val generatorsString = generatorList.joinToString(", ") { it.name }
    return "Please fix errors for generators ($generatorsString) above this."
}

private fun isFailureAtTheBeginning(errorResult: ErrorResult): Boolean {
    return when (errorResult) {
        is AlternativesFailure -> errorResult.errors.all { isFailureAtTheBeginning(it) }
        is NoMatchingToken -> errorResult.tokenMismatch.offset == 0
        else -> false
    }
}

private fun assertDegreeOfDifferentialValue(
    generatorList: List<GeneratorOfFreeDGA<IntDegree>>,
    currentGenerator: GeneratorOfFreeDGA<IntDegree>,
): ValidationResultInternal? {
    val indeterminateList = generatorList.map { generator ->
        Indeterminate(generator.name, generator.degree)
    }
    val freeGAlgebra = try {
        FreeGAlgebra(SparseMatrixSpaceOverRational, indeterminateList)
    } catch (e: Exception) {
        // Usually e is IllegalArgumentException thrown from the initializer block of FreeGAlgebra
        // when indeterminateList contains duplicated indeterminates.
        val message = getErrorMessageForPreviousGenerators(generatorList)
        return ValidationResultInternal.Error(message)
    }
    val differentialValue: String = currentGenerator.differentialValue
    val gVector = try {
        freeGAlgebra.parse(differentialValue)
    } catch (e: ParseException) {
        val message = if (isFailureAtTheBeginning(e.errorResult)) {
            "Failed to parse the value \"$differentialValue\" of the differential. " +
                "No matching token at the beginning."
        } else {
            val messageFromException: String = e.message ?: e.toString()
            "Failed to parse the value \"$differentialValue\" of the differential " +
                "with the following error message:\n$messageFromException"
        }
        return ValidationResultInternal.Error(message)
    } catch (e: Exception) {
        val message: String = e.message ?: e.toString()
        return ValidationResultInternal.Error(message)
    }
    val expectedDegree = currentGenerator.degree.value + 1
    if (gVector is GVector && gVector.degree.value != expectedDegree) {
        val name = currentGenerator.name
        val message = "The degree of d($name) is expected to be deg($name)+1=$expectedDegree, " +
            "but the given value $differentialValue has degree ${gVector.degree.value}."
        return ValidationResultInternal.Error(message)
    }
    return null
}

fun assertSquareOfDifferentialIsZero(
    generatorList: List<GeneratorOfFreeDGA<IntDegree>>,
    currentGenerator: GeneratorOfFreeDGA<IntDegree>,
): ValidationResultInternal? {
    val differentialValue: String = currentGenerator.differentialValue
    val freeDGAlgebra = try {
        FreeDGAlgebra(SparseMatrixSpaceOverRational, generatorList)
    } catch (e: Exception) {
        val message = getErrorMessageForPreviousGenerators(generatorList)
        return ValidationResultInternal.Error(message)
    }
    freeDGAlgebra.context.run {
        return when (val gVector = freeDGAlgebra.gAlgebra.parse(differentialValue)) {
            is ZeroGVector -> null
            is GVector -> {
                val gVectorWhichShouldBeZero = d(gVector)
                when (gVectorWhichShouldBeZero.isZero()) {
                    true -> null
                    false -> ValidationResultInternal.Error(
                        "d(d(${currentGenerator.name})) must be zero, but was $gVectorWhichShouldBeZero"
                    )
                }
            }
        }
    }
}

@ExperimentalJsExport
@JsExport
fun validateDifferentialValueOfTheLast(
    generatorsJson: String,
): ValidationResult {
    // FreeGAlgebra を assertDegreeOfDifferentialValue と assertSquareOfDifferentialIsZero の両方で
    // 生成しているのは無駄な気もするけど、
    // FreeDGAlgebra(SparseMatrixSpaceOverRational, generatorList) をそのまま使うためには仕方ない。
    val generatorList = jsonToGeneratorList(generatorsJson)
    val previousGeneratorList = generatorList.dropLast(1)
    val currentGenerator = generatorList.last()
    assertDegreeOfDifferentialValue(previousGeneratorList, currentGenerator)?.let {
        return it.export()
    }
    assertSquareOfDifferentialIsZero(previousGeneratorList, currentGenerator)?.let {
        return it.export()
    }
    return ValidationResultInternal.Success().export()
}
