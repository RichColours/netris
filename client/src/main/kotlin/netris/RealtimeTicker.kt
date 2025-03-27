package netris

import java.io.Closeable
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class RealtimeTicker : Ticker, Closeable {

    private val ses = Executors.newSingleThreadScheduledExecutor()

    private var currentScheduledFuture: ScheduledFuture<*>? = null

    @Synchronized
    override fun startPeriodic(interval: Duration, runnable: Runnable) {

        if (currentScheduledFuture != null)
            stop()

        currentScheduledFuture = ses.scheduleAtFixedRate(runnable, 0, interval.toMillis(), TimeUnit.MILLISECONDS)
    }

    /**
     * Deliberately crash if null, as it implies a state bug elsewhere.
     */
    @Synchronized
    override fun stop() {
        currentScheduledFuture!!.cancel(false)
        currentScheduledFuture = null
    }

    override fun close() {

        if (currentScheduledFuture != null)
            throw Exception("Periodic started task has not been stopped - this indicates unclean usage")
        ses.shutdownNow()
    }
}
