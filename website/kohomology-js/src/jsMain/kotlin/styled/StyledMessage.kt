package styled

@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class StyledMessageKt(
    val messageType: String,
    val strings: Array<StyledStringKt>,
    val options: MessageOptionsKt,
)

enum class MessageType(val typeName: String) {
    SUCCESS("success"),
    ERROR("error"),
}

class StyledMessageInternal(
    val messageType: MessageType,
    val strings: List<StyledStringInternal>,
    val options: MessageOptionsInternal = MessageOptionsInternal(),
) {
    @ExperimentalJsExport
    fun export(): StyledMessageKt {
        val strings = this.strings.map { it.export() }.toTypedArray()
        return StyledMessageKt(this.messageType.typeName, strings, options.export())
    }

    fun withOptions(newOptions: MessageOptionsInternal): StyledMessageInternal {
        return StyledMessageInternal(
            messageType = this.messageType,
            strings = this.strings,
            options = newOptions,
        )
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
