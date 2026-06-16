package tdsl.bridge

import java.io.File

fun main(args: Array<String>) {
    val bridgeDir = locateBridgeDir()
    val examples = bridgeDir.resolve("examples")
    val tdslFile = if (args.isNotEmpty()) File(args[0]) else examples.resolve("program.tdsl")
    val sampleJson = examples.resolve("program.ast.json")
    val service = RascalService(bridgeDir.parentFile)

    val planning: Planning = try {
        if (service.available() && tdslFile.exists()) {
            println("Generando el AST con Rascal desde ${tdslFile.name}...")
            service.planningFromTdsl(tdslFile)
        } else {
            println("Rascal no disponible; uso el AST de muestra ${sampleJson.name}.")
            service.planningFromJsonFile(sampleJson)
        }
    } catch (e: Exception) {
        println("No pude generar con Rascal (${e.message}); uso el AST de muestra.")
        service.planningFromJsonFile(sampleJson)
    }

    println()
    println(report(planning))
}

private fun locateBridgeDir(): File {
    val cwd = File(System.getProperty("user.dir"))
    return when {
        cwd.resolve("examples/program.ast.json").exists() -> cwd
        cwd.resolve("kotlin-app/examples/program.ast.json").exists() -> cwd.resolve("kotlin-app")
        else -> cwd
    }
}
