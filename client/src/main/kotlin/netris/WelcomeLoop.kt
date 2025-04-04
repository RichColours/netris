package netris

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import netris.animation.CompletedAnimator
import netris.animation.LinearTranslationAnimator
import netris.animation.SequentialAnimator
import netris.animation.StepsDelay
import netris.model.Coord
import netris.model.MutableCoord
import java.time.Duration
import java.util.concurrent.CountDownLatch
import kotlin.random.Random

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
    private var initialisedScene = false

    private val netrisChars = arrayOf(
        TextCharacter('n', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('e', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('t', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('r', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('i', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('s', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
    )

    private val netrisCoords = arrayOf(
        MutableCoord(0, 0), //all offscreen
        MutableCoord(1, 1),
        MutableCoord(2, 2),
        MutableCoord(3, 3),
        MutableCoord(4, 4),
        MutableCoord(5, 5),
    )

    private val finalLocations = arrayOf(
        Coord(0, 0),
        Coord(0, 0),
        Coord(0, 0),
        Coord(0, 0),
        Coord(0, 0),
        Coord(0, 0),
    )

    private lateinit var netrisAnimator: SequentialAnimator

    // Call on main thread, blocks until complete.
    fun complete() {

        inputDriver.clearAndSetReceive(this::inputReceive)
        ticker.startPeriodic(stepDuration, this::tickHandler)

        completionCountDownLatch.await()
        println("Exited WelcomeLoop latch")

        ticker.stop()
        inputDriver.disconnect()
    }

    private fun initScene() {

        // Scatter the netris to start
        val ts = screen.terminalSize
        for (mc in netrisCoords) {
            mc.x = Random.nextInt(0, ts.columns)
            mc.y = Random.nextInt(0, ts.rows)
        }

        finalLocations.forEachIndexed { index, _ ->
            finalLocations[index] = Coord((ts.columns / 2 - 3) + index, ts.rows / 2)
        }

        netrisAnimator = SequentialAnimator(
            LinearTranslationAnimator(netrisCoords[0], finalLocations[0], Duration.ofMillis(600).dividedBy(stepDuration)),
            LinearTranslationAnimator(netrisCoords[1], finalLocations[1], Duration.ofMillis(500).dividedBy(stepDuration)),
            LinearTranslationAnimator(netrisCoords[2], finalLocations[2], Duration.ofMillis(400).dividedBy(stepDuration)),
            LinearTranslationAnimator(netrisCoords[3], finalLocations[3], Duration.ofMillis(350).dividedBy(stepDuration)),
            LinearTranslationAnimator(netrisCoords[4], finalLocations[4], Duration.ofMillis(300).dividedBy(stepDuration)),
            LinearTranslationAnimator(netrisCoords[5], finalLocations[5], Duration.ofMillis(250).dividedBy(stepDuration)),
            StepsDelay(Duration.ofMillis(1000).dividedBy(stepDuration), CompletedAnimator())
        )
    }

    @Synchronized
    private fun tickHandler() {

        try {
            if (!initialisedScene) {
                initScene()
                initialisedScene = true
            }

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

            //if (coord.x in GameConstants.viewPortXRange && coord.y in GameConstants.viewPortYRange) {
                screen.setCharacter(coord.x, coord.y, netrisChars[index])
            //}
        }

        screen.refresh()
    }
}
