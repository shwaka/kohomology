import styled.MessageType
import styled.StyledStringListBuilder.text
import styled.styledMessage
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalJsExport
class StyledMessageTest {
    @Test
    fun exportMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            "a^2 + b^2 = c^2".math
        }.export()
        assertEquals(styledMessageKt.plainString, "\$a^2 + b^2 = c^2\$")
    }

    @Test
    fun exportMultipleMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            "x = \\cos\\theta".math + "y = \\sin\\theta".math
        }.export()
        assertEquals(styledMessageKt.plainString, "\$x = \\cos\\theta\$ \$y = \\sin\\theta\$")
    }

    @Test
    fun exportTextAndMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            "Let ".text + "x = 3".math + ".".text
        }.export()
        assertEquals(styledMessageKt.plainString, "Let \$x = 3\$.")
    }

    @Test
    fun exportGroupedMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            groupedMath {
                "x + y".math + "=".math + "z".math
            }
        }.export()
        val expected = "\$x + y = z\$"
        assertEquals(styledMessageKt.plainString, expected)
    }
}
