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
    private val stringType: StringType,
    private val content: String,
) {
    @ExperimentalJsExport
    fun export(): StyledStringKt {
        return StyledStringKt(this.stringType.typeName, this.content)
    }
}

@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class StyledMessageKt(
    val messageType: String,
    val strings: Array<StyledStringKt>,
)

enum class MessageType(val typeName: String) {
    SUCCESS("success"),
    ERROR("error"),
}

class StyledMessageInternal(
    val messageType: MessageType,
    val strings: List<StyledStringInternal>,
) {
    @ExperimentalJsExport
    fun export(): StyledMessageKt {
        val strings = this.strings.map { it.export() }.toTypedArray()
        return StyledMessageKt(this.messageType.typeName, strings)
    }
}

object StyledStringListBuilder {
    val String.text: List<StyledStringInternal>
        get() = listOf(StyledStringInternal(StringType.TEXT, this))
    val String.math: List<StyledStringInternal>
        get() = listOf(StyledStringInternal(StringType.MATH, this))
}

fun styledMessage(messageType: MessageType, block: StyledStringListBuilder.() -> List<StyledStringInternal>): StyledMessageInternal {
    return StyledMessageInternal(messageType, StyledStringListBuilder.block())
}
