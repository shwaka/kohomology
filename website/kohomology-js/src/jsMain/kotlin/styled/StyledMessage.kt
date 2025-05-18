package styled

@ExperimentalJsExport
@JsExport
@Suppress("UNUSED")
class StyledMessageKt(
    val messageType: String,
    val strings: Array<StyledStringKt>,
    val plainString: String,
    val options: MessageOptionsKt,
)

sealed interface StyledStringGroup {
    fun toList(): List<StyledStringInternal>
    fun isMath(): Boolean

    @ExperimentalJsExport
    fun export(): List<StyledStringKt> {
        return this.toList().map { it.export() }
    }


    class Single(val value: StyledStringInternal) : StyledStringGroup {
        override fun toList(): List<StyledStringInternal> {
            return listOf(this.value)
        }

        override fun isMath(): Boolean {
            return (this.value.stringType == StringType.MATH)
        }
    }

    class GroupedMath(val valueList: List<StyledStringInternal>) : StyledStringGroup {
        init {
            require(valueList.all { it.stringType == StringType.MATH }) {
                "GroupedMath accepts only StringType.MATH"
            }
        }

        override fun toList(): List<StyledStringInternal> {
            return this.valueList
        }

        override fun isMath(): Boolean {
            return true
        }
    }
}

enum class MessageType(val typeName: String) {
    SUCCESS("success"),
    ERROR("error"),
}

class StyledMessageInternal(
    val messageType: MessageType,
    val groups: List<StyledStringGroup>,
    val options: MessageOptionsInternal = MessageOptionsInternal(),
) {
    constructor(
        messageType: MessageType,
        strings: List<StyledStringInternal>,
        options: MessageOptionsInternal = MessageOptionsInternal(),
    ) : this(messageType, strings.map { StyledStringGroup.Single(it) }, options)

    @ExperimentalJsExport
    fun export(): StyledMessageKt {
        val strings: Array<StyledStringKt> = this.groups.flatMap { it.export() }.toTypedArray()
        val plainString = this.getPlainString()
        return StyledMessageKt(
            messageType = this.messageType.typeName,
            strings = strings,
            plainString = plainString,
            options = options.export(plainString),
        )
    }

    fun getStrings(): List<StyledStringInternal> {
        return this.groups.flatMap { it.toList() }
    }

    fun withOptions(newOptions: MessageOptionsInternal): StyledMessageInternal {
        return StyledMessageInternal(
            messageType = this.messageType,
            groups = this.groups,
            options = newOptions,
        )
    }

    private fun getPlainString(): String {
        if (this.groups.isEmpty()) {
            return ""
        }
        val space = StyledStringInternal(StringType.TEXT, " ")
        val stringsWithSpaces: MutableList<StyledStringInternal> = this.groups.first().toList().toMutableList()
        for (i in 1 until this.groups.size) {
            // Avoid $x = 1$$y = 2$, by replacing it with $x = 1$ $y = 2$
            if (this.groups[i - 1].isMath() && this.groups[i].isMath()) {
                stringsWithSpaces.add(space)
            }
            stringsWithSpaces.addAll(this.groups[i].toList())
        }
        return stringsWithSpaces.joinToString("") { styledString ->
            when (styledString.stringType) {
                StringType.TEXT -> styledString.content
                StringType.MATH -> "\$${styledString.content}\$"
            }
        }
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
