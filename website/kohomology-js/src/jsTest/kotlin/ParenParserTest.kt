import kotlin.test.Test
import kotlin.test.assertEquals

class ParenParserTest {
    @Test
    fun removeSurroundingParen() {
        assertEquals("foo", ParenParser.removeSurroundingParen("(foo)"))
    }

    @Test
    fun removeOnlySurroundingParen() {
        assertEquals("(foo)(bar)", ParenParser.removeSurroundingParen("(foo)(bar)"))
    }

    @Test
    fun failingTest() {
        // added to confirm the workflow fails
        assertEquals(1, 2)
    }
}
