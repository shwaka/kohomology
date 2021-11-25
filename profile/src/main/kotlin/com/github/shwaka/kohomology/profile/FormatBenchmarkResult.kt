package com.github.shwaka.kohomology.profile

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

// Classes to deserialize output from kotlinx.benchmark
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

// Classes to serialize results for github-action-benchmark
@Serializable
data class BenchmarkOutput(
    val name: String,
    val unit: String,
    val value: Double,
) {
    companion object {
        fun fromResult(result: BenchmarkResult): BenchmarkOutput {
            return BenchmarkOutput(
                name = result.benchmark,
                unit = result.primaryMetric.scoreUnit,
                value = result.primaryMetric.score,
            )
        }
    }
}

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

fun generateOutputJson(results: List<BenchmarkResult>): String {
    val json = Json {
        prettyPrint = true
    }
    val outputs: List<BenchmarkOutput> = results.map { BenchmarkOutput.fromResult(it) }
    return json.encodeToString(outputs)
}

fun main() {
    val results = getBenchmarkResults()
    println(generateOutputJson(results))
}
