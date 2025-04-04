package netris

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import netris.animation.LinearTranslationAnimator
import netris.animation.SequentialAnimator
import netris.animation.StepsDelay
import netris.model.Coord
import netris.model.GameConstants
import netris.model.MutableCoord
import java.time.Duration
import java.util.concurrent.CountDownLatch

/**
 * Thread model:
 *
 * complete is run by outer controller
 *
 * ticker and inputDriver both push actions onto here independently
 * need to paint on both events
 * paint is fast and can run ON those threads
 *
 *
 */
class WelcomeLoop(
    private val ticker: Ticker,
    private val screen: TerminalScreen,
    private val inputDriver: InputDriver,
) {

    private val completionCountDownLatch = CountDownLatch(1)
    private val stepDuration = Duration.ofMillis(10)
    private var loopComplete = false

    private val netrisChars = arrayOf(
        TextCharacter('n', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('e', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('t', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('r', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('i', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('s', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
    )

    private val netrisCoords = arrayOf(
        MutableCoord(0, GameConstants.viewPortYMax + 1), //all offscreen
        MutableCoord(1, GameConstants.viewPortYMax + 1),
        MutableCoord(2, GameConstants.viewPortYMax + 1),
        MutableCoord(3, GameConstants.viewPortYMax + 1),
        MutableCoord(4, GameConstants.viewPortYMax + 1),
        MutableCoord(5, GameConstants.viewPortYMax + 1),
    )

    private val netrisAnimator = SequentialAnimator(
        LinearTranslationAnimator(netrisCoords[0], Coord(0, 0), Duration.ofMillis(600).dividedBy(stepDuration)),
        LinearTranslationAnimator(netrisCoords[1], Coord(1, 0), Duration.ofMillis(500).dividedBy(stepDuration)),
        LinearTranslationAnimator(netrisCoords[2], Coord(2, 0), Duration.ofMillis(400).dividedBy(stepDuration)),
        LinearTranslationAnimator(netrisCoords[3], Coord(3, 0), Duration.ofMillis(350).dividedBy(stepDuration)),
        LinearTranslationAnimator(netrisCoords[4], Coord(4, 0), Duration.ofMillis(300).dividedBy(stepDuration)),
        LinearTranslationAnimator(netrisCoords[5], Coord(5, 0), Duration.ofMillis(250).dividedBy(stepDuration)),
        StepsDelay(Duration.ofMillis(2000).dividedBy(stepDuration))
    )

    // Call on main thread, blocks until complete.
    fun complete() {

        inputDriver.clearAndSetReceive(this::inputReceive)
        ticker.startPeriodic(stepDuration, this::tickHandler)



        completionCountDownLatch.await()
        println("Exited WelcomeLoop latch")

        ticker.stop()
        inputDriver.disconnect()
    }

    @Synchronized
    private fun tickHandler() {

        try {

            netrisAnimator.step()
            loopComplete = netrisAnimator.isComplete

            paint()

            if (loopComplete) {
                completionCountDownLatch.countDown()
            }

        } catch (e: Exception) {
            throw e
        }
    }

    @Synchronized
    private fun inputReceive(i: Input) {

        when (i) {

            Input.DOWN -> {
                println("D")
                completionCountDownLatch.countDown()
            }

            else -> {
                println("Unhandled input - press down")
            }
        }

    }

    private fun paint() {

        screen.clear()

        netrisCoords.forEachIndexed { index, coord ->

            if (coord.x in GameConstants.viewPortXRange && coord.y in GameConstants.viewPortYRange) {
                screen.setCharacter(coord.x, coord.y, netrisChars[index])
            }
        }

        screen.refresh()
    }
}
