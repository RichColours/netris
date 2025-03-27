package netris

import java.util.function.Consumer
import java.util.function.Supplier

fun Int.reps(fn: Runnable) = (0..<this).forEach { _ -> fn.run() }

fun Int.repsIndexed(fn: Consumer<Int>) = (0..<this).forEach { it -> fn.accept(it) }

fun <T> Int.of(fn: Supplier<T>) =
    (0..<this).asSequence().map { fn.get() }
