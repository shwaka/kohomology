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
        assertEquals("\$a^2 + b^2 = c^2\$", styledMessageKt.plainString, )
    }

    @Test
    fun exportMultipleMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            "x = \\cos\\theta".math + "y = \\sin\\theta".math
        }.export()
        assertEquals("\$x = \\cos\\theta\$ \$y = \\sin\\theta\$", styledMessageKt.plainString)
    }

    @Test
    fun exportTextAndMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            "Let ".text + "x = 3".math + ".".text
        }.export()
        assertEquals("Let \$x = 3\$.", styledMessageKt.plainString)
    }

    @Test
    fun exportGroupedMath() {
        val styledMessageKt = styledMessage(MessageType.SUCCESS) {
            groupedMath {
                "x + y".math + "=".math + "z".math
            }
        }.export()
        val expected = "\$x + y = z\$"
        assertEquals(expected, styledMessageKt.plainString)
    }
}
