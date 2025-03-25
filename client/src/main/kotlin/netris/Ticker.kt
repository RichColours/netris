package netris

import java.time.Duration

/**
 * Thread model:
 * Both should tolerate being called from any thread including a thread from
 * within itself which is calling runnable, so should be re-entrant.
 *
 * If from within the runnable then a new start should occur with a new interval after that one completes.
 * If not from within the runnable:
 * * If runnable is running then changes occur on next run
 * * If runnable not running then immediately cancel and upcoming scheduled and start afresh
 */
interface Ticker {

    fun startPeriodic(interval: Duration, runnable: Runnable)

    fun stop()

}