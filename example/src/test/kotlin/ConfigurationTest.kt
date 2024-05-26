import dev.limebeck.config.MyConfigNew
import dev.limebeck.config.MyConfigOld
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConfigurationTest {
    @Test
    fun `Test new-style API`() {
        val config = MyConfigNew
        assertEquals("SomeValue", config.stringProp)
        assertEquals(null, config.stringPropNullable)
        assertEquals("null", config.stringPropNullableFilled)
        assertIs<String?>(config.stringPropNullable)
        assertIs<String?>(config.stringPropNullableFilled)
        assertEquals(123, config.intProp)
        assertEquals(123.0, config.doubleProp)
        assertEquals(123, config.longProp)
        assertEquals(true, config.boolProp)
        assertEquals("SomeValue", config.nested.stringProp)
    }

    @Test
    fun `Test old-style API`() {
        val config = MyConfigOld
        assertEquals("SomeValue", config.stringProp)
        assertEquals(123, config.intProp)
        assertEquals(123.0, config.doubleProp)
        assertEquals(123, config.longProp)
        assertEquals(true, config.boolProp)
        assertEquals("SomeValue", config.nested.stringProp)
    }
}
