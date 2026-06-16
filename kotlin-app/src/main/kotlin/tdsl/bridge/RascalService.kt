package tdsl.bridge

import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.TimeUnit

class RascalService(private val repoRoot: File) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val jar = repoRoot.resolve("rascal-shell-stable.jar")
    private val rascalSrc = repoRoot.resolve("src/main/rascal")

    fun available(): Boolean = jar.exists() && rascalSrc.resolve("Json.rsc").exists()

    fun planningFromTdsl(tdslFile: File): Planning {
        val output = runRascal(tdslFile)
        val jsonStr = extractJson(output)
            ?: throw RuntimeException("No se encontró JSON en la salida de Rascal:\n$output")
        return json.decodeFromString(Planning.serializer(), jsonStr)
    }

    fun planningFromJsonFile(jsonFile: File): Planning =
        json.decodeFromString(Planning.serializer(), jsonFile.readText())

    private fun runRascal(tdslFile: File): String {
        val cmd = listOf(
            "java",
            "-Drascal.projectPath=${rascalSrc.absolutePath}",
            "-jar", jar.absolutePath,
            "Json.rsc", "ast", tdslFile.absolutePath
        )
        val process = ProcessBuilder(cmd)
            .directory(rascalSrc)
            .redirectErrorStream(false)
            .start()
        process.outputStream.close()
        val stdout = process.inputStream.bufferedReader().readText()
        val stderr = process.errorStream.bufferedReader().readText()
        if (!process.waitFor(180, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            throw RuntimeException("Rascal timeout (180s)")
        }
        if (process.exitValue() != 0 && extractJson(stdout) == null) {
            throw RuntimeException("Rascal error (exit ${process.exitValue()}): ${stderr.take(600)}")
        }
        return stdout
    }

    private fun extractJson(output: String): String? {
        val clean = output.replace(Regex("\\x1b\\[[0-9;?]*[a-zA-Z]"), "")
        var startIdx = 0
        while (startIdx < clean.length) {
            val braceIdx = clean.indexOf('{', startIdx)
            if (braceIdx == -1) break
            var depth = 0
            var inString = false
            var escaped = false
            var endIdx = -1
            for (i in braceIdx until clean.length) {
                val ch = clean[i]
                if (escaped) { escaped = false; continue }
                if (ch == '\\' && inString) { escaped = true; continue }
                if (ch == '"') { inString = !inString; continue }
                if (!inString) {
                    if (ch == '{') depth++
                    else if (ch == '}') {
                        depth--
                        if (depth == 0) { endIdx = i; break }
                    }
                }
            }
            if (endIdx != -1) {
                val candidate = clean.substring(braceIdx, endIdx + 1)
                if (candidate.contains("\"node\":\"planning\"")) return candidate
            }
            startIdx = braceIdx + 1
        }
        return null
    }
}
