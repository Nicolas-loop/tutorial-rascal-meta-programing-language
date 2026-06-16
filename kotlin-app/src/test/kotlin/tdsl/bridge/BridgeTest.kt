package tdsl.bridge

import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class BridgeTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun sampleFile(): File {
        val candidates = listOf(
            "examples/program.ast.json",
            "kotlin-app/examples/program.ast.json",
            "../examples/program.ast.json"
        )
        return candidates.map(::File).firstOrNull { it.exists() }
            ?: error("no encontré examples/program.ast.json")
    }

    private fun sample(): Planning =
        json.decodeFromString(Planning.serializer(), sampleFile().readText())

    @Test
    fun decodesStructure() {
        val p = sample()
        assertEquals(2, p.people.size)
        assertEquals(3, allTasks(p).size)
        assertEquals(listOf("Alice", "Bob"), p.people.map { it.name })
    }

    @Test
    fun aggregatesPaymentsAndDurations() {
        val p = sample()
        assertEquals(5000, totalEuros(p))
        assertEquals(150, totalMinutes(p))
    }

    @Test
    fun decodesActionVariants() {
        val p = sample()
        val actions = allTasks(p).map { it.action }
        assertEquals(1, actions.filterIsInstance<Paper>().size)
        assertEquals(1, actions.filterIsInstance<Payment>().size)
        assertEquals(1, actions.filterIsInstance<Meeting>().size)
        assertEquals("Demo", actions.filterIsInstance<Meeting>().first().action.topic)
    }
}
