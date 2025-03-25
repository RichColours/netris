package netris

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class RealtimeTicker : Ticker {

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
}
