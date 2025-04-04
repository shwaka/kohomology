package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause
import com.github.shwaka.kohomology.util.pow

public interface FiniteMonoidMap<ES : FiniteMonoidElement, ET : FiniteMonoidElement> {
    public val source: FiniteMonoid<ES>
    public val target: FiniteMonoid<ET>
    public val values: List<ET>

    public operator fun invoke(monoidElement: ES): ET {
        return FiniteMonoidMap.getValue(this.source, this.values, monoidElement)
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

    public fun checkFiniteMonoidMapAxioms() {
        val isFiniteMonoidMap = FiniteMonoidMap.isFiniteMonoidMap(
            sourceElements = this.source.elements,
            multiplySource = this.source::multiply,
            multiplyTarget = this.target::multiply,
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
            multiplySource: (monoidElement1: ES, monoidElement2: ES) -> ES,
            multiplyTarget: (monoidElement1: ET, monoidElement2: ET) -> ET,
            map: (monoidElement: ES) -> ET,
        ): BooleanWithCause {
            val cause = mutableListOf<String>()
            for (a in sourceElements) {
                for (b in sourceElements) {
                    val lhs = map(multiplySource(a, b))
                    val rhs = multiplyTarget(map(a), map(b))
                    if (lhs != rhs) {
                        cause.add(
                            "f(ab)=f(a)f(b) is not satisfied for a=$a, b=$b; " +
                                "f(ab) = $lhs and f(a)f(b) = $rhs"
                        )
                    }
                }
            }
            return BooleanWithCause.fromCause(cause)
        }

        public fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> isFiniteMonoidMap(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>,
            values: List<ET>,
        ): BooleanWithCause {
            require(values.size == source.elements.size) {
                "values.size (${values.size}) is different from source.elements.size (${source.elements.size})"
            }
            return FiniteMonoidMap.isFiniteMonoidMap(
                source.elements,
                source::multiply,
                target::multiply,
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
            check(source.elements[0] == source.unit)
            check(target.elements[0] == target.unit)
            return FiniteMonoidMap.listAllMapsNaive(source, target)
        }

        // This is very naive implementation and should be used in test.
        internal fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> listAllMapsNaive(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>,
        ): List<FiniteMonoidMap<ES, ET>> {
            check(source.elements[0] == source.unit)
            check(target.elements[0] == target.unit)
            return target.elements.pow(source.elements.size).mapNotNull { values ->
                when (FiniteMonoidMap.isFiniteMonoidMap(source, target, values)) {
                    is BooleanWithCause.True -> FiniteMonoidMap(source, target, values)
                    is BooleanWithCause.False -> null
                }
            }
        }
    }
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
}
