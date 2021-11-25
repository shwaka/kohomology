package com.github.shwaka.kohomology.profile

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class PrimaryMetric(
    val score: Double,
    val scoreUnit: String,
)

@Serializable
data class BenchmarkResult(
    val benchmark: String,
    val primaryMetric: PrimaryMetric,
)

fun findLatest(): File {
    val dir = File("build/reports/benchmarks/main/")
    val benchmarkDirs: List<File> = dir.listFiles { file -> file.isDirectory }!!.toList()
    val latestDir: File = benchmarkDirs.maxByOrNull { benchmarkDir -> benchmarkDir.lastModified() }
        ?: throw Exception("No directory found in $dir")
    return latestDir
}

fun getBenchmarkResults(): List<BenchmarkResult> {
    val latestDir = findLatest()
    val jsonFile = latestDir.resolve("main.json")
    val jsonText: String = jsonFile.readText(Charsets.UTF_8)
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString(jsonText)
}

fun main() {
    val results = getBenchmarkResults()
    println(results)
}
