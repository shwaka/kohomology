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
    fun toPlainString(): String

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

        override fun toPlainString(): String {
            return when (this.value.stringType) {
                StringType.TEXT -> this.value.content
                StringType.MATH -> "\$${this.value.content}\$"
            }
        }
    }

    class GroupedMath(val valueList: List<StyledStringInternal>) : StyledStringGroup {
        constructor(valueList: List<String>) : this(
            valueList.map { StyledStringInternal(StringType.MATH, it) }
        )

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

        override fun toPlainString(): String {
            val joined = this.valueList.joinToString(" ") { it.content }
            return "\$$joined\$"
        }
    }

    companion object {
        fun text(content: String): StyledStringGroup.Single {
            return StyledStringGroup.Single(
                StyledStringInternal(StringType.TEXT, content)
            )
        }

        fun math(content: String): StyledStringGroup.Single {
            return StyledStringGroup.Single(
                StyledStringInternal(StringType.MATH, content)
            )
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
        val result = mutableListOf(this.groups.first().toPlainString())
        for (i in 1 until this.groups.size) {
            // Avoid $x = 1$$y = 2$, by replacing it with $x = 1$ $y = 2$
            if (this.groups[i - 1].isMath() && this.groups[i].isMath()) {
                result.add(" ")
            }
            result.add(this.groups[i].toPlainString())
        }
        return result.joinToString("")
    }
}

@DslMarker
annotation class StyledStringMarker

@StyledStringMarker
object GroupedMathBuilder {
    val String.math: List<StyledStringInternal>
        get() = listOf(StyledStringInternal(StringType.MATH, this))
}

@StyledStringMarker
object StyledStringListBuilder {
    val String.text: List<StyledStringGroup>
        get() = listOf(StyledStringGroup.text(this))
    val String.math: List<StyledStringGroup>
        get() = listOf(StyledStringGroup.math(this))
    fun groupedMath(block: GroupedMathBuilder.() -> List<StyledStringInternal>): List<StyledStringGroup> {
        val groupedMath = StyledStringGroup.GroupedMath(GroupedMathBuilder.block())
        return listOf(groupedMath)
    }
}

fun styledMessage(messageType: MessageType, block: StyledStringListBuilder.() -> List<StyledStringGroup>): StyledMessageInternal {
    return StyledMessageInternal(messageType, StyledStringListBuilder.block())
}
