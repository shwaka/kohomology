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

@ExperimentalJsExport
private fun assertDegreeOfDifferentialValue(
    generatorList: List<GeneratorOfFreeDGA<IntDegree>>,
    differentialValue: String,
    expectedDegree: Int,
): ValidationResultInternal? {
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
        return ValidationResultInternal.Error(message)
    } catch (e: Exception) {
        val message: String = e.message ?: e.toString()
        return ValidationResultInternal.Error(message)
    }
    if (gVector is GVector && gVector.degree.value != expectedDegree) {
        val message = "Illegal degree: the degree of $differentialValue is ${gVector.degree.value}, " +
            "but ${expectedDegree - 1}+1=$expectedDegree is expected."
        return ValidationResultInternal.Error(message)
    }
    return null
}

@ExperimentalJsExport
fun assertSquareOfDifferentialIsZero(
    generatorList: List<GeneratorOfFreeDGA<IntDegree>>,
    differentialValue: String,
): ValidationResultInternal? {
    val freeDGAlgebra = try {
        FreeDGAlgebra(SparseMatrixSpaceOverRational, generatorList)
    } catch (e: Exception) {
        val generatorsString = generatorList.joinToString(", ") { it.name }
        val message = "Please fix errors for generators ($generatorsString) above this."
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
                        "d($differentialValue) must be zero, but was $gVectorWhichShouldBeZero"
                    )
                }
            }
        }
    }
}

@ExperimentalJsExport
@JsExport
fun validateDifferentialValue(
    previousGeneratorsJson: String,
    differentialValue: String,
    expectedDegree: Int,
): ValidationResult {
    // FreeGAlgebra を assertDegreeOfDifferentialValue と assertSquareOfDifferentialIsZero の両方で
    // 生成しているのは無駄な気もするけど、
    // FreeDGAlgebra(SparseMatrixSpaceOverRational, generatorList) をそのまま使うためには仕方ない。
    val generatorList = jsonToGeneratorList(previousGeneratorsJson)
    assertDegreeOfDifferentialValue(generatorList, differentialValue, expectedDegree)?.let {
        return it.export()
    }
    assertSquareOfDifferentialIsZero(generatorList, differentialValue)?.let {
        return it.export()
    }
    return ValidationResultInternal.Success().export()
}
