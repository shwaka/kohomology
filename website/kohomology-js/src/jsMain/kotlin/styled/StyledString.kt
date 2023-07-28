package styled

@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class StyledStringKt(
    val stringType: String,
    val content: String,
)

enum class StringType(val typeName: String) {
    TEXT("text"),
    MATH("math"),
}

class StyledStringInternal(
    val stringType: StringType,
    val content: String,
) {
    @ExperimentalJsExport
    fun export(): StyledStringKt {
        return StyledStringKt(this.stringType.typeName, this.content)
    }
}
