package tdsl.bridge

private fun unitToMinutes(unit: String): Int = when (unit) {
    "min" -> 1
    "hour" -> 60
    "day" -> 1440
    "week" -> 10080
    else -> 0
}

fun durationMinutes(d: Duration): Int = d.amount * unitToMinutes(d.unit)

fun allTasks(p: Planning): List<Task> = p.people.flatMap { it.tasks }

fun totalEuros(p: Planning): Int =
    allTasks(p).sumOf { (it.action as? Payment)?.action?.amount ?: 0 }

fun totalMinutes(p: Planning): Int =
    allTasks(p).flatMap { it.duration }.sumOf(::durationMinutes)

fun actionLabel(a: Action): String = when (a) {
    is Lunch -> "lunch en ${a.action.location}"
    is Meeting -> "meeting \"${a.action.topic}\""
    is Paper -> "report ${a.action.report}"
    is Payment -> "pay ${a.action.amount} euro"
}

fun report(p: Planning): String {
    val sb = StringBuilder()
    sb.appendLine("Planning con ${p.people.size} persona(s) y ${allTasks(p).size} tarea(s)")
    sb.appendLine("-".repeat(48))
    for (person in p.people) {
        sb.appendLine("${person.name}:")
        for (t in person.tasks.sortedByDescending { it.priority }) {
            val dur = t.duration.firstOrNull()?.let { " (${it.amount} ${it.unit})" } ?: ""
            sb.appendLine("  [prio ${t.priority}] ${actionLabel(t.action)}$dur")
        }
    }
    sb.appendLine("-".repeat(48))
    sb.appendLine("Total a pagar: ${totalEuros(p)} euro")
    sb.appendLine("Tiempo total planificado: ${totalMinutes(p)} min")
    return sb.toString().trimEnd()
}
