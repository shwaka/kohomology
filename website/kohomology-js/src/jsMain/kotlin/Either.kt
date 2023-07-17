sealed interface Either<out A, out B> {
    data class Left<out A>(val value: A) : Either<A, Nothing>
    data class Right<out B>(val value: B) : Either<Nothing, B>
}
