import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@kotlinx.serialization.ExperimentalSerializationApi
fun jsonToIdealGenerators(json: String): List<String> {
    return Json.decodeFromString<List<String>>(json)
}
