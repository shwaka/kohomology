package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

public data class GeneratorOfFreeDGA<D : Degree>(val name: String, val degree: D, val differentialValue: String) {
    public fun toIndeterminate(): Indeterminate<D, StringIndeterminateName> {
        return Indeterminate(
            name = this.name,
            tex = GeneratorOfFreeDGA.convertNameToTex(this.name),
            degree = this.degree,
        )
    }

    public companion object {
        public operator fun invoke(name: String, degree: Int, differentialValue: String): GeneratorOfFreeDGA<IntDegree> {
            return GeneratorOfFreeDGA(name, IntDegree(degree), differentialValue)
        }

        private fun convertNameToTex(name: String): String {
            // v_1_2 -> v_{1,2}
            // v_1_2_3 -> v_{1,2,3}
            val underscoreCount = name.count { it == '_' }
            if (underscoreCount <= 1) {
                return name
            }
            return name
                .replaceFirst("_", "{") // to avoid replacing "_{" with ",{"
                .replace("_", ",")
                .replace("{", "_{") + // to avoid replacing "_{" with ",{"
                "}"
        }
    }
}

@Serializable
private data class SerializableGenerator(val name: String, val degree: Int, val differentialValue: String) {
    fun toGeneratorOfFreeDGA(): GeneratorOfFreeDGA<IntDegree> {
        return GeneratorOfFreeDGA(
            name = this.name,
            degree = IntDegree(this.degree),
            differentialValue = this.differentialValue,
        )
    }

    companion object {
        fun fromGeneratorOfFreeDGA(generatorOfFreeDGA: GeneratorOfFreeDGA<IntDegree>): SerializableGenerator {
            return SerializableGenerator(
                name = generatorOfFreeDGA.name,
                degree = generatorOfFreeDGA.degree.value,
                differentialValue = generatorOfFreeDGA.differentialValue,
            )
        }
    }
}

private object GeneratorSerializer : JsonTransformingSerializer<SerializableGenerator>(SerializableGenerator.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonArray) {
            JsonObject(
                mapOf(
                    "name" to element[0],
                    "degree" to element[1],
                    "differentialValue" to element[2],
                )
            )
        } else {
            element
        }
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        return if (
            (element is JsonObject) &&
            element.containsKey("name") &&
            element.containsKey("degree") &&
            element.containsKey("differentialValue")
        ) {
            val name: JsonElement = element["name"] ?: throw Exception("This can't happen!")
            val degree: JsonElement = element["degree"] ?: throw Exception("This can't happen!")
            val differentialValue: JsonElement = element["differentialValue"] ?: throw Exception("This can't happen!")
            JsonArray(
                listOf(name, degree, differentialValue)
            )
        } else {
            element
        }
    }
}

public fun generatorListToJson(generatorList: List<GeneratorOfFreeDGA<IntDegree>>): String {
    val serializableGeneratorList = generatorList.map {
        SerializableGenerator.fromGeneratorOfFreeDGA(it)
    }
    return Json.encodeToString(ListSerializer(GeneratorSerializer), serializableGeneratorList)
}

public fun jsonToGeneratorList(json: String): List<GeneratorOfFreeDGA<IntDegree>> {
    val serializableGeneratorList = Json.decodeFromString(ListSerializer(GeneratorSerializer), json)
    return serializableGeneratorList.map {
        it.toGeneratorOfFreeDGA()
    }
}
