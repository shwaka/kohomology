package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Printer

public interface FiniteMonoidMap<ES : FiniteMonoidElement, ET : FiniteMonoidElement> : Printable {
    public val source: FiniteMonoid<ES>
    public val target: FiniteMonoid<ET>
    public val values: List<ET>

    public operator fun invoke(monoidElement: ES): ET {
        return FiniteMonoidMap.getValue(this.source, this.values, monoidElement)
    }

    public operator fun <ER : FiniteMonoidElement> times(
        other: FiniteMonoidMap<ER, ES>
    ): FiniteMonoidMap<ER, ET> {
        require(this.source == other.target) {
            "Cannot compose $this and $other: " +
                "$this.source=${this.source} != $other.target=${other.target}"
        }
        val values = other.values.map { this(it) }
        return FiniteMonoidMapImpl(source = other.source, target = this.target, values = values)
    }

    public fun inv(): FiniteMonoidMap<ET, ES> {
        require(this.isBijective()) { "Inverse does not exist" }
        val values = this.target.elements.map { targetElement ->
            this.source.elements.find { this(it) == targetElement } ?: throw Exception("This can't happen!")
        }
        return FiniteMonoidMap(
            source = this.target,
            target = this.source,
            values = values,
        )
    }

    public fun isBijective(): Boolean {
        return this.isInjective() && this.isSurjective()
    }

    public fun isInjective(): Boolean {
        return this.values.distinct().size == this.values.size
    }

    public fun isSurjective(): Boolean {
        return this.values.distinct().size == this.target.size
    }

    public fun checkFiniteMonoidMapAxioms(earlyReturn: Boolean = false) {
        val isFiniteMonoidMap = FiniteMonoidMap.isFiniteMonoidMap(
            sourceElements = this.source.elements,
            targetUnit = this.target.unit,
            multiplySource = this.source::multiply,
            multiplyTarget = this.target::multiply,
            earlyReturn = earlyReturn,
            map = this::invoke,
        )
        if (isFiniteMonoidMap is BooleanWithCause.False) {
            throw IllegalStateException(
                "Axioms for finite monoid map are not satisfied:\n" +
                    isFiniteMonoidMap.cause.joinToString("\n")
            )
        }
    }

    // This is almost the same as FiniteMonoidMapImpl.equals,
    // but is guaranteed to work with any FiniteMonoidMap (other than FiniteMonoidMapImpl).
    public fun equalsAsMap(other: FiniteMonoidMap<ES, ET>): Boolean {
        return this.source == other.source &&
            this.target == other.target &&
            this.values == other.values
    }

    override fun toString(printConfig: PrintConfig): String {
        val p = Printer(printConfig)
        val className = "FiniteMonoidMap"
        val classNameFormatted = when (printConfig.printType) {
            PrintType.TEX -> "\\mathrm{$className}"
            else -> className
        }
        val arrow = when (printConfig.printType) {
            PrintType.TEX -> "\\to "
            else -> "->"
        }
        val valuesString = this.source.elements.joinToString(", ") { element ->
            "${p(element)}$arrow${p(this(element))}"
        }
        return "$classNameFormatted($valuesString)"
    }

    public companion object {
        public operator fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> invoke(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>,
            values: List<ET>,
        ): FiniteMonoidMap<ES, ET> {
            return FiniteMonoidMapImpl(source, target, values)
        }

        public fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> isFiniteMonoidMap(
            sourceElements: List<ES>,
            targetUnit: ET,
            multiplySource: (monoidElement1: ES, monoidElement2: ES) -> ES,
            multiplyTarget: (monoidElement1: ET, monoidElement2: ET) -> ET,
            earlyReturn: Boolean,
            map: (monoidElement: ES) -> ET,
        ): BooleanWithCause {
            val cause = mutableListOf<String>()
            require(sourceElements.isNotEmpty()) {
                "sourceElements must be non-empty, but was empty"
            }
            fun getEarlyReturnValue(cause: List<String>): BooleanWithCause? {
                return if (earlyReturn) {
                    BooleanWithCause.fromCause(
                        listOf(
                            "Not a finite monoid map. One of the reasons:"
                        ) + cause
                    )
                } else {
                    null
                }
            }
            if (map(sourceElements[0]) != targetUnit) {
                cause.add(
                    "f(unit)=${map(sourceElements[0])} is not unit"
                )
                getEarlyReturnValue(cause)?.let { return it }
            }
            for (a in sourceElements) {
                for (b in sourceElements) {
                    val lhs = map(multiplySource(a, b))
                    val rhs = multiplyTarget(map(a), map(b))
                    if (lhs != rhs) {
                        cause.add(
                            "f(ab)=f(a)f(b) is not satisfied for a=$a, b=$b; " +
                                "f(ab) = $lhs and f(a)f(b) = $rhs"
                        )
                        getEarlyReturnValue(cause)?.let { return it }
                    }
                }
            }
            return BooleanWithCause.fromCause(cause)
        }

        public fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> isFiniteMonoidMap(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>,
            values: List<ET>,
            earlyReturn: Boolean = false,
        ): BooleanWithCause {
            require(values.size == source.elements.size) {
                "values.size (${values.size}) is different from source.elements.size (${source.elements.size})"
            }
            return FiniteMonoidMap.isFiniteMonoidMap(
                source.elements,
                target.unit,
                source::multiply,
                target::multiply,
                earlyReturn = earlyReturn,
            ) { monoidElement ->
                FiniteMonoidMap.getValue(source, values, monoidElement)
            }
        }

        private fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> getValue(
            source: FiniteMonoid<ES>,
            values: List<ET>,
            monoidElement: ES,
        ): ET {
            val index = source.elements.indexOf(monoidElement)
            require(index >= 0) {
                "$monoidElement is not an element of $source"
            }
            return values[index]
        }

        public fun <E : FiniteMonoidElement> id(finiteMonoid: FiniteMonoid<E>): FiniteMonoidMap<E, E> {
            return FiniteMonoidMap(
                source = finiteMonoid,
                target = finiteMonoid,
                values = finiteMonoid.elements,
            )
        }

        public fun <E : FiniteMonoidElement> trivialMap(finiteMonoid: FiniteMonoid<E>): FiniteMonoidMap<E, E> {
            return FiniteMonoidMap(
                source = finiteMonoid,
                target = finiteMonoid,
                values = List(finiteMonoid.size) { finiteMonoid.unit },
            )
        }

        public fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> listAllMaps(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>,
        ): List<FiniteMonoidMap<ES, ET>> {
            val enumerator = FiniteMonoidMapEnumerator.Naive
            return enumerator.listAllMaps(source, target)
        }
    }
}

public fun <E : FiniteMonoidElement, EO : FiniteMonoidElement> FiniteMonoid<E>.isIsomorphicTo(
    other: FiniteMonoid<EO>
): Boolean {
    return FiniteMonoidMap.listAllMaps(this, other).any { it.isBijective() }
}

private class FiniteMonoidMapImpl<ES : FiniteMonoidElement, ET : FiniteMonoidElement>(
    override val source: FiniteMonoid<ES>,
    override val target: FiniteMonoid<ET>,
    override val values: List<ET>,
) : FiniteMonoidMap<ES, ET> {
    init {
        require(values.size == source.elements.size) {
            "$values should have the same size with $source"
        }

        for (value in values) {
            require(target.elements.contains(value)) {
                "$value is not contained in $target"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FiniteMonoidMapImpl<*, *>

        if (source != other.source) return false
        if (target != other.target) return false
        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }
}
