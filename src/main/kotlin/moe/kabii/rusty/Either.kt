package moe.kabii.rusty

class Left<out  L>(val value: L) : Either<L, Nothing>() {
    fun unwrap() = value
}
class Right<out R>(val value: R) : Either<Nothing, R>() {
    fun unwrap() = value
}

/**
 * A container representing either a Left(containing a type L) or Right(containing a type R).
 * This is probably not nearly the extent of a proper functional Either.
 */
sealed class Either<out L, out R> {
    /**
     * If this Either is a Left.
     */
    val left = this is Left
    /**
     * If this Either is a Right.
     */
    val right = !left

    /**
     * @param fn Function to be executed if this Either is a Left.
     */
    fun ifLeft(fn: (L) -> Unit) {
        if(this is Left) {
            fn(value)
        }
    }

    /**
     * @param fn Function to be executed if this Either is a Right.
     */
    fun ifRight(fn: (R) -> Unit) {
        if(this is Right) {
            fn(value)
        }
    }

    /**
     * @param mapRight Function to be applied to the inner value if this is a Right.
     * @return An Either as either the original Left or as a Right with the inner value mapped.
     */
    fun <O> mapRight(mapRight: (R) -> O): Either<L, O> = when(this) {
        is Left -> this
        is Right -> Right(mapRight(value))
    }

    /**
     * @param mapLeft Function to be applied to the inner value if this is a Left.
     * @return An Either as either a Left with the inner value mapped or as the original Right.
     */
    fun <O> mapLeft(mapLeft: (L) -> O): Either<O, R> = when(this) {
        is Left -> Left(mapLeft(value))
        is Right -> this
    }

    /**
     * @param mapLeft Function to be applied to the inner value if this is a Left.
     * @param mapRight Function to be applied to the inner value is this is a Right.
     * @return An Either with the applicable mapper function applied.
     */
    fun <OL, OR> bimap(mapLeft: (L) -> OL, mapRight: (R) -> OR) = when(this) {
        is Left -> Left(mapLeft(value))
        is Right -> Right(mapRight(value))
    }

    /**
     * Both mapper functions return the same type, for converting this Either into an actual value with a single type.
     * @param mapLeft Function to be applied to the inner value if this is a Left.
     * @param mapRight Function to be applied to the inner value is this is a Right.
     * @return A value from the applicable mapper function.
     */
    fun <O> flatMap(mapLeft: (L) -> O, mapRight: (R) -> O) = when(this) {
        is Left -> mapLeft(value)
        is Right -> mapRight(value)
    }
}