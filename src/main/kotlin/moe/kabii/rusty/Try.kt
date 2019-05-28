package moe.kabii.rusty

/**
 * A container for a lazily attempted function which may throw an error. Inner value is computed on the first access of the result.
 */
class Try<R: Any>(f: () -> R) {
    /**
     * The result of the function in this Try, represented as a Result(T, KotlinErr(Throwable)).
     */
    val result by lazy {
        try {
            Ok(f())
        } catch(t: Throwable) {
            ThrowableErr(t)
        }
    }

    /**
     * Unwraps the value nested in the Result within this Try.
     * @throws IllegalStateException If the Result was an Err. Prefer type checking on result.
     */
    fun unwrap(): R = result.unwrap()
}