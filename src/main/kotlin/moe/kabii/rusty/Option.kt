package moe.kabii.rusty

/**
 * Repreesnts a non-empty optional value.
 */
class Some<T: Any>(val value: T): Opt<T>()

/**
 * Represents an empty optional value.
 */
object None: Opt<Nothing>()

/**
 * Represents an optional value.
 */
sealed class Opt<out T: Any> {
    /**
     * returns whether this Opt is a Some(T).
     */
    val some = this is Some

    /**
     * returns whether this Opt is a None
     */
    val none = !some

    /**
     * Unwraps the value of a Some(T).
     * @return Inner object T
     * @throws IllegalStateException if the Opt is a None
     */
    fun unwrap(): T = if(this is Some) value else throw IllegalStateException("Unwrapped a None!")

    /**
     * Converts the Opt<T> into a Kotlin T?
     * @return Inner object T if this is a Some(T), otherwise null
     */
    fun orNull(): T? = if(this is Some) value else null

    /**
     * Applies the provided function to the inner value
     * @param mapper Function (T) -> Any to apply to the inner value of the Some(T) if it is present.
     * @return Returns None if this Opt was a None. If this was a Some, then the mapper function is applied to the inner value T. If the function returns null, the map will return a None.
     */
    fun <R : Any> map(mapper: (T) -> R?): Opt<R> = when(this) {
        is Some -> {
            val mapped = mapper(value)
            if(mapped != null) Some(mapped) else None
        }
        is None -> None
    }

    /**
     * Applies the provided function the inner value
     * @param mapper Function (T) -> Any to apply to the inner value of the Some(T) if it is present.
     * @return Returns the value of #map#orNull
     */
    fun <R : Any> mapOrNull(mapper: (T) -> R?): R? = map(mapper).orNull()

    /**
     * Applies the provided predicate function to the inner value
     * @param predicate Function (T) -> Boolean to apply to the inner value of the Some(T) if it is present
     * @return Some(T) if the Opt is a Some(T) and the filter applied to the inner value returns true, otherwise None
     */
    fun filter(predicate: (T) -> Boolean): Opt<T> = if(this is Some && predicate(value)) this else None

    /**
     * @param or The value to return if this Opt is a None`
     * @return The value in this Opt is returned as a Left(T) if this is a Some(T). If this is a None, then the provided value is returned
     */
    fun <R : Any> or(or: R): Either<T, R> = if(this is Some) Left(
        value
    ) else Right(or)

    /**
     * @param fn Function executed on the inner value if this Opt is a Some.
     */
    fun ifSome(fn: (T) -> Unit) {
        if(this is Some) {
            fn(value)
        }
    }

    /**
     * @param fn Function executed if this Opt is a None.
     */
    fun ifNone(fn: () -> Unit) {
        if(this is None) {
            fn()
        }
    }

    /**
     * @param fnSome Function executed on the inner value if this Opt is a Some.
     * @param fnNone Function executed if this Opt is a None.
     */
    fun ifSomeOrElse(fnSome: (T) -> Unit, fnNone: () -> Unit) {
        when(this) {
            is Some -> fnSome(value)
            is None -> fnNone()
        }
    }

    /**
     * @param mapSome Mapper function applied to the inner value if this Opt is a Some.
     * @param mapNone Mapper function applied if this Opt is a None.
     * @return An Either with either the return of mapSome (for a Some) or mapNone (for a None).
     */
    fun <L, R> bimap(mapSome: (T) -> L, mapNone: () -> R): Either<L, R> = when(this) {
        is Some -> Left(mapSome(value))
        is None -> Right(mapNone())
    }

    /**
     * Both mapper functions return the same type, for converting this Opt into an actual value.
     * @param mapSome Mapper function applied to the inner value if this Opt is a Some.
     * @param mapNone Mapper function applied if this Opt is a None.
     * @return Returns a value from the applicable mapping function.
     */
    fun <R> flatMap(mapSome: (T) -> R, mapNone: () -> R): R = when(this) {
        is Some -> mapSome(value)
        is None -> mapNone()
    }

    companion object {
        /**
         * @return Some(T) for a non-null value or None for a null.
         * Opt.some("test") is the same as Some("test")
         */
        fun <T : Any> of(value: T?) = if(value != null) Some(value) else None

        /**
         * @return The None object
         */
        fun none() = None
    }
}