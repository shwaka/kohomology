package styled

@ExperimentalJsExport
@JsExport
class MessageOptionsKt(
    val dgaJson: String?,
)

class MessageOptionsInternal(
    val dgaJson: String? = null
) {
    @ExperimentalJsExport
    fun export(): MessageOptionsKt {
        return MessageOptionsKt(
            dgaJson = this.dgaJson,
        )
    }
}
