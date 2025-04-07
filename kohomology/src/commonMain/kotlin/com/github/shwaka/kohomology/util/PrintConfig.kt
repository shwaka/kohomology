package com.github.shwaka.kohomology.util

import kotlin.reflect.KClass

public interface PrintConfigEntry

public data class CopiedNamePrintConfig(
    val showShift: ShowShift = ShowShift.S_WITH_DEGREE
) : PrintConfigEntry
// The init block in an object is not executed immediately.
// It will be executed when it is accessed.
// https://kotlinlang.org/docs/object-declarations.html#behavior-difference-between-object-declarations-and-expressions
@Suppress("UNUSED")
private val registerCopiedNamePrintConfig = PrintConfig.registerDefault(CopiedNamePrintConfig())

/**
 * Determines how to print [com.github.shwaka.kohomology.model.CopiedName].
 */
public enum class ShowShift {
    BAR, S, S_WITH_DEGREE
}

public data class PrintConfig(
    val printType: PrintType,
    val beforeSign: String,
    val afterSign: String,
    val afterCoeff: String,
    val entries: Map<KClass<out PrintConfigEntry>, PrintConfigEntry>,
) {
    public constructor(
        printType: PrintType = PrintType.PLAIN,
        beforeSign: String = " ",
        afterSign: String = " ",
        afterCoeff: String = " ",
        buildEntries: EntriesBuilder.() -> Unit = {},
    ) : this(
        printType, beforeSign, afterSign, afterCoeff,
        EntriesBuilder().apply { buildEntries() }.entries,
    )

    public inline fun <reified T : PrintConfigEntry> get(): T {
        (this.entries[T::class] as? T)?.let { return it }
        return PrintConfig.getDefault<T>()
    }

    public companion object {
        public val default: PrintConfig = PrintConfig(PrintType.PLAIN)

        public val defaultEntries: MutableMap<KClass<out PrintConfigEntry>, PrintConfigEntry> = mutableMapOf()

        public inline fun <reified T : PrintConfigEntry> registerDefault(entry: T) {
            require(!this.defaultEntries.containsKey(T::class)) {
                "Already registered: ${T::class}"
            }
            println("default value registered for ${T::class}")
            this.defaultEntries[T::class] = entry
        }

        public inline fun <reified T : PrintConfigEntry> getDefault(): T {
            if (!this.defaultEntries.containsKey(T::class)) {
                throw NoSuchElementException("Default value not registered for ${T::class}")
            }
            return (this.defaultEntries[T::class] as? T)
                ?: throw NoSuchElementException("Failed to cast to ${T::class}")
        }
    }
}

public class EntriesBuilder {
    public val entries: MutableMap<KClass<out PrintConfigEntry>, PrintConfigEntry> = mutableMapOf()

    public inline fun <reified T : PrintConfigEntry> register(entry: T) {
        require(!this.entries.containsKey(T::class)) {
            "Already registered: ${T::class}"
        }
        this.entries[T::class] = entry
    }
}
