package kohomology.field

interface Scalar<S> {
    operator fun plus(other: S): S
    operator fun plus(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this + other.unwrap())
    }
    operator fun minus(other: S): S {
        return this + this.field.fromInteger(-1) * other
    }
    operator fun minus(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this - other.unwrap())
    }
    operator fun times(other: S): S
    operator fun times(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this * other.unwrap())
    }
    fun unwrap(): S
    val field: Field<S>
}

interface Field<S> {
    fun wrap(a: S): Scalar<S>
    fun fromInteger(n: Int): Scalar<S>
}
