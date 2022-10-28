package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

public class SetNumVector<S : Scalar> private constructor(
    public val valueSet: Set<Int>,
    override val field: Field<S>,
    override val dim: Int,
) : NumVector<S> {
    public companion object {
        public operator fun <S : Scalar> invoke(
            valueSet: Set<Int>,
            field: Field<S>,
            dim: Int,
        ): SetNumVector<S> {
            require(field.characteristic == 2) {
                "field ($field) for SetNumVector must have characteristic 2"
            }
            require(valueSet.size <= dim) {
                "valueSet.size (=${valueSet.size}) must be smaller than or equal to dim (=$dim)"
            }
            return SetNumVector(valueSet, field, dim)
        }
    }

    override fun isZero(): Boolean {
        return this.valueSet.isEmpty()
    }

    override fun toString(): String {
        return "SetNumVector(valueSet=$valueSet, field=$field, dim=$dim)"
    }

    override fun toList(): List<S> {
        return (0 until this.dim).map { index ->
            if (this.valueSet.contains(index)) {
                field.one
            } else {
                field.zero
            }
        }
    }

    override fun toMap(): Map<Int, S> {
        return this.valueSet.map { index -> Pair(index, field.one) }.toMap()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SetNumVector<*>

        if (valueSet != other.valueSet) return false
        if (field != other.field) return false
        if (dim != other.dim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = valueSet.hashCode()
        result = 31 * result + field.hashCode()
        result = 31 * result + dim
        return result
    }
}
