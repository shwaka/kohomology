package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.util.isEven
import com.github.shwaka.kohomology.util.isOdd

public data class DegreeIndeterminate(val name: String, val defaultValue: Int)

public class MultiDegree(
    public val group: MultiDegreeGroup,
    public val constantTerm: Int,
    public val coeffList: IntArray
) : Degree {
    override fun isEven(): Boolean {
        this.coeffList.indices.filter { this.coeffList[it].isOdd() }.let { oddIndices ->
            if (oddIndices.isNotEmpty()) {
                val oddIndeterminateString = oddIndices.joinToString(", ") {
                    this.group.indeterminateList[it].toString()
                }
                throw ArithmeticException(
                    "Cannot determine the parity of $this, since the coefficients of $oddIndeterminateString are odd"
                )
            }
        }
        return this.constantTerm.isEven()
    }

    override fun isZero(): Boolean {
        return (this.constantTerm == 0) && this.coeffList.all { it == 0 }
    }

    override fun isOne(): Boolean {
        return (this.constantTerm == 1) && this.coeffList.all { it == 0 }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiDegree

        if (group != other.group) return false
        if (constantTerm != other.constantTerm) return false
        if (!coeffList.contentEquals(other.coeffList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + constantTerm
        result = 31 * result + coeffList.contentHashCode()
        return result
    }

    override fun toString(): String {
        if (this.coeffList.isEmpty()) {
            return this.constantTerm.toString()
        }
        val stringListForConstantTerm = if (this.constantTerm != 0) {
            listOf("${this.constantTerm}")
        } else {
            emptyList()
        }
        val stringListForCoeff = this.coeffList.indices.mapNotNull {
            when (val coeff = this.coeffList[it]) {
                0 -> null
                1 -> this.group.indeterminateList[it].name
                else -> "$coeff${this.group.indeterminateList[it].name}"
            }
        }
        val stringList = stringListForConstantTerm + stringListForCoeff
        return if (stringList.isEmpty()) {
            "0"
        } else {
            stringList.joinToString(" + ")
        }
    }
}

public data class MultiDegreeGroup(val indeterminateList: List<DegreeIndeterminate>) : AugmentedDegreeGroup<MultiDegree> {
    override val context: AugmentedDegreeContext<MultiDegree> by lazy {
        AugmentedDegreeContext(this)
    }

    val generatorList: List<MultiDegree>
        get() = this.indeterminateList.indices.map { i ->
            val coeffList = List(this.indeterminateList.size) { j -> if (i == j) 1 else 0 }
            this.fromCoefficients(0, coeffList)
        }

    override fun fromInt(n: Int): MultiDegree {
        return MultiDegree(this, n, IntArray(this.indeterminateList.size) { 0 })
    }

    override fun augmentation(degree: MultiDegree): Int {
        return degree.constantTerm + degree.coeffList.indices.map {
            degree.coeffList[it] * this.indeterminateList[it].defaultValue
        }.sum()
    }

    override fun add(degree1: MultiDegree, degree2: MultiDegree): MultiDegree {
        if (degree1.group != this)
            throw IllegalContextException("$degree1 is not an element of $this")
        if (degree2.group != this)
            throw IllegalContextException("$degree2 is not an element of $this")
        val coeffList = IntArray(this.indeterminateList.size) { degree1.coeffList[it] + degree2.coeffList[it] }
        return MultiDegree(this, degree1.constantTerm + degree2.constantTerm, coeffList)
    }

    override fun subtract(degree1: MultiDegree, degree2: MultiDegree): MultiDegree {
        if (degree1.group != this)
            throw IllegalContextException("$degree1 is not an element of $this")
        if (degree2.group != this)
            throw IllegalContextException("$degree2 is not an element of $this")
        val coeffList = IntArray(this.indeterminateList.size) { degree1.coeffList[it] - degree2.coeffList[it] }
        return MultiDegree(this, degree1.constantTerm - degree2.constantTerm, coeffList)
    }

    override fun multiply(degree: MultiDegree, n: Int): MultiDegree {
        if (degree.group != this)
            throw IllegalContextException("$degree is not an element of $this")
        val coeffList = IntArray(this.indeterminateList.size) { degree.coeffList[it] * n }
        return MultiDegree(this, degree.constantTerm * n, coeffList)
    }

    public fun fromCoefficients(constantTerm: Int, coeffList: List<Int>): MultiDegree {
        if (coeffList.size != this.indeterminateList.size)
            throw IllegalArgumentException("The length of $coeffList should be ${this.indeterminateList.size}, but ${coeffList.size} was given")
        return MultiDegree(this, constantTerm, coeffList.toIntArray())
    }

    public fun fromList(coeffList: List<Int>): MultiDegree {
        if (coeffList.size != this.indeterminateList.size + 1)
            throw IllegalArgumentException("The length of $coeffList should be ${this.indeterminateList.size + 1}, but ${coeffList.size} was given")
        return MultiDegree(this, coeffList[0], coeffList.drop(1).toIntArray())
    }

    public fun toList(degree: MultiDegree): List<Int> {
        return listOf(degree.constantTerm) + degree.coeffList.toList()
    }

    override fun listAllDegrees(augmentedDegree: Int): List<MultiDegree> {
        return this.listAllDegreesInternal(augmentedDegree, 0)
    }

    private val listSize: Int = this.indeterminateList.size + 1
    private fun oneAtIndex(index: Int): MultiDegree {
        return this.fromList(List(this.listSize) { if (it == index) 1 else 0 })
    }

    // (augmentedDegree: Int, index: Int) -> List<MultiDegree>
    private val cache: MutableMap<Pair<Int, Int>, List<MultiDegree>> = mutableMapOf()

    private fun listAllDegreesInternal(augmentedDegree: Int, index: Int): List<MultiDegree> {
        if (index < 0 || index > this.listSize)
            throw Exception("This can't happen! (illegal index: $index)")
        if (index == this.listSize) {
            return if (augmentedDegree == 0)
                listOf(this.zero)
            else
                emptyList()
        }
        val cacheKey = Pair(augmentedDegree, index)
        this.cache[cacheKey]?.let { return it }
        // Since 0 <= index < this.listSize,
        // we have 0 < this.listSize
        val newAugmentedDegree = augmentedDegree - this.augmentation(this.oneAtIndex(index))
        val listWithNonZeroAtIndex = if (newAugmentedDegree >= 0) {
            this.listAllDegreesInternal(newAugmentedDegree, index)
                .map { multiDegree ->
                    this.context.run { multiDegree + this@MultiDegreeGroup.oneAtIndex(index) }
                }
        } else emptyList()
        val listWithZeroAtIndex = this.listAllDegreesInternal(augmentedDegree, index + 1)
        val result = listWithNonZeroAtIndex + listWithZeroAtIndex
        this.cache[cacheKey] = result
        return result
    }

    override fun contains(degree: MultiDegree): Boolean {
        return degree.group == this
    }
}

public data class MultiDegreeGroupNormalization(
    val normalizedGroup: MultiDegreeGroup,
    val normalize: MultiDegreeMorphism,
    val unnormalize: MultiDegreeMorphism,
) {
    public companion object {
        public fun from(originalGroup: MultiDegreeGroup): MultiDegreeGroupNormalization {
            val normalizedGroup: MultiDegreeGroup = run {
                val indeterminateList = originalGroup.indeterminateList.map { indeterminate ->
                    DegreeIndeterminate("${indeterminate.name}_", 0)
                }
                MultiDegreeGroup(indeterminateList)
            }
            val normalize: MultiDegreeMorphism = run {
                val size = originalGroup.indeterminateList.size
                val values = (0 until size).map { i ->
                    val sourceIndeterminate = originalGroup.indeterminateList[i]
                    val targetIndeterminateAsDegree = normalizedGroup.fromCoefficients(
                        0,
                        List(size) { if (it == i) 1 else 0 }
                    )
                    normalizedGroup.context.run {
                        targetIndeterminateAsDegree + sourceIndeterminate.defaultValue
                    }
                }
                MultiDegreeMorphism(originalGroup, normalizedGroup, values)
            }
            val unnormalize: MultiDegreeMorphism = run {
                val size = normalizedGroup.indeterminateList.size
                val values = (0 until size).map { i ->
                    val originalDefaultValue = originalGroup.indeterminateList[i].defaultValue
                    val targetIndeterminateAsDegree = originalGroup.fromCoefficients(
                        0,
                        List(size) { if (it == i) 1 else 0 }
                    )
                    originalGroup.context.run {
                        targetIndeterminateAsDegree - originalDefaultValue
                    }
                }
                MultiDegreeMorphism(normalizedGroup, originalGroup, values)
            }
            return MultiDegreeGroupNormalization(normalizedGroup, normalize, unnormalize)
        }
    }
}

public class MultiDegreeMorphism(
    override val source: MultiDegreeGroup,
    override val target: MultiDegreeGroup,
    private val values: List<MultiDegree>,
) : AugmentedDegreeMorphism<MultiDegree, MultiDegree> {
    init {
        if (values.size != source.indeterminateList.size)
            throw IllegalArgumentException("values.size should be equal to source.indeterminateList.size")
    }

    override operator fun invoke(degree: MultiDegree): MultiDegree {
        if (degree !in this.source)
            throw IllegalArgumentException("The degree $degree is not an element of the group ${this.source}")
        return this.target.context.run {
            degree.coeffList.indices.map { i ->
                degree.coeffList[i] * this@MultiDegreeMorphism.values[i]
            }.fold(target.fromInt(degree.constantTerm)) { acc, degree -> acc + degree }
        }
    }
}

public class InclusionFromIntDegreeToMultiDegree(
    override val target: MultiDegreeGroup
) : AugmentedDegreeMorphism<IntDegree, MultiDegree> {
    override val source: AugmentedDegreeGroup<IntDegree> = IntDegreeGroup

    override fun invoke(degree: IntDegree): MultiDegree {
        return this.target.fromInt(degree.value)
    }
}
