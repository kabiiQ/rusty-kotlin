package moe.kabii.rusty

/**
 *  A container representing a successful value.
 */
class Ok<T>(val value: T): Result<T, Nothing>()

/**
 * A container repreesnting a failed value.
 * Err is an open class which can be used for representing any type of more specific error containing any values.
 */
open class Err<E: Any>(val value: E): Result<Nothing, E>()

class ThrowableErr(value: Throwable): Err<Throwable>(value)

/**
 * A container representing either an Ok(T) or an Err(E) value.
 */
sealed class Result<out T, out E: Any> {
    /**
     * Returns whether this Result is an Ok.
     */
    val ok = this is Ok

    /**
     * Returns whether this Result is an Err.
     */
    val err = !ok

    /**
     * @return The inner value contained by this Ok.
     * @throws IllegalStateException If this Result was an Err.
     */
    fun unwrap(): T = if(this is Ok) value else throw IllegalStateException("Unwrapped an Err!")

    /**
     * @param fn Function to be executed if this Result is an Ok.
     */
    fun ifSuccess(fn: (T) -> Unit) {
        if(this is Ok) {
            fn(value)
        }
    }

    /**
     * @param fn Function to be executed if this Result is an Err.
     */
    fun ifErr(fn: (err: E) -> Unit) {
        if(this is Err) {
            fn(value)
        }
    }

    /**
     * @param mapper Mapper function (T) -> Any to apply to the inner value if this is an Ok.
     * @return A Result as either the original Err or as an Ok with the inner value mapped.
     */
    fun <R> mapOk(mapper: (T) -> R): Result<R, E> = when(this) {
        is Ok -> Ok(mapper(value))
        is Err -> this
    }

    /**
     * @param predicate Function (T)->Boolean to test the inner value against, if this is an Ok.
     * @param err Err to return if the Ok does not fufill the predicate or if this Result was an Err.
     * @return An Ok if this was an Ok AND the inner value fulfills the predicate function, otherwise returns an Err with the provided value.
     */
    fun <ER: Any> filter(err: ER, predicate: (T) -> Boolean): Result<T, ER> = when(this) {
        is Ok -> if(predicate(value)) this else Err(err)
        is Err -> Err(err)
    }

    /**
     * Both mapper functions return the same type, for converting this Result into an actual value with a single type.
     * @param mapperOk Mapper function applied to the inner value if this Result is an Ok.
     * @param mapperErr Mapper function applied to the inner value if this Result is an Err.
     * @return Returns a value from the applicable mapper function.
     */
    fun <R> flatMap(mapperOk: (T) -> R, mapperErr: (E) -> R): R = when(this) {
        is Ok -> mapperOk(value)
        is Err -> mapperErr(value)
    }

    /**
     * @return Returns the inner value if this is an Ok, otherwise returns null.
     */
    fun orNull(): T? = when(this) {
        is Ok -> value
        is Err -> null
    }
}