@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class StyledStringKt(
    val stringType: String,
    val content: String,
)

enum class StringType(val typeName: String) {
    NORMAL("normal"),
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
    private val messageType: MessageType,
    private val strings: List<StyledStringInternal>,
) {
    @ExperimentalJsExport
    fun export(): StyledMessageKt {
        val strings = this.strings.map { it.export() }.toTypedArray()
        return StyledMessageKt(this.messageType.typeName, strings)
    }
}

object StyledStringListBuilder {
    val String.normal: List<StyledStringInternal>
        get() = listOf(StyledStringInternal(StringType.NORMAL, this))
    val String.math: List<StyledStringInternal>
        get() = listOf(StyledStringInternal(StringType.MATH, this))
    operator fun StyledStringInternal.plus(other: StyledStringInternal): List<StyledStringInternal> {
        return listOf(this, other)
    }
    operator fun List<StyledStringInternal>.plus(other: StyledStringInternal): List<StyledStringInternal> {
        return this + listOf(other)
    }
}

fun styledMessage(messageType: MessageType, block: StyledStringListBuilder.() -> List<StyledStringInternal>): StyledMessageInternal {
    return StyledMessageInternal(messageType, StyledStringListBuilder.block())
}
