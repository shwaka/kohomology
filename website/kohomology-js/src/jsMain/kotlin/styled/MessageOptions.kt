package styled

@ExperimentalJsExport
@JsExport
class MessageOptionsKt(
    val dgaJson: String?,
    val plainString: String,
)

class MessageOptionsInternal(
    val dgaJson: String? = null
) {
    @ExperimentalJsExport
    fun export(plainString: String): MessageOptionsKt {
        return MessageOptionsKt(
            dgaJson = this.dgaJson,
            plainString = plainString,
        )
    }
}
