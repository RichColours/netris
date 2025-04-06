package netris

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import netris.animation.*
import netris.model.Coord
import netris.model.MutableCoord
import java.time.Duration
import java.util.concurrent.CountDownLatch
import kotlin.random.Random

class GameOverLoop(
    private val ticker: Ticker,
    private val screen: TerminalScreen,
    private val inputDriver: InputDriver,
) {

    private val completionCountDownLatch = CountDownLatch(1)
    private val stepDuration = Duration.ofMillis(10)
    private var loopComplete = false
    private var initialisedScene = false

    private val netrisChars = arrayOf(
        TextCharacter('G', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('a', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('m', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('e', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter(' ', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('O', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('v', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('e', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
        TextCharacter('r', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK),
    )

    private val gameOverCoords = "game over".mapIndexed {index, it -> MutableCoord(index, 0) }.toTypedArray()

    private var finalLocations = "game over".map { MutableCoord(0, 0) }.toTypedArray()

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

        val ts = screen.terminalSize
        // val centreAt = Coord(ts.columns / 2, ts.rows / 2)

        gameOverCoords.forEachIndexed { index , it ->
            it.x = (ts.columns / 2 - ("game over".length / 2)) + index
            it.y = ts.rows / 2
        }

        tailrec fun generateCoord(): Coord {
            val x = Random.nextInt(-10, ts.columns + 10)
            val y = Random.nextInt(-10, ts.rows + 20)
            return if (x in (0..< ts.columns) && (y in (0..< ts.rows)))
                generateCoord()
            else Coord(x, y)
        }

        // Create the gameover exploded positions:
        // From the orient in centre of screen (not top-left) all destinations must be offscreen
        finalLocations.forEachIndexed { index, _ ->
            val offScreenCoord = generateCoord()
            finalLocations[index].x = offScreenCoord.x
            finalLocations[index].y = offScreenCoord.y
        }

        val explodeDuration = 600L

        netrisAnimator = SequentialAnimator(
            StepsDelay(Duration.ofMillis(1000).dividedBy(stepDuration), CompletedAnimator()),
            ParallelAnimator(
                LinearTranslationAnimator(gameOverCoords[0], finalLocations[0], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[1], finalLocations[1], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[2], finalLocations[2], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[3], finalLocations[3], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[4], finalLocations[4], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[5], finalLocations[5], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[6], finalLocations[6], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[7], finalLocations[7], Duration.ofMillis(explodeDuration).dividedBy(stepDuration)),
                LinearTranslationAnimator(gameOverCoords[8], finalLocations[8], Duration.ofMillis(explodeDuration).dividedBy(stepDuration))
            )
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

        gameOverCoords.forEachIndexed { index, coord ->
            screen.setCharacter(coord.x, coord.y, netrisChars[index])
        }

        screen.refresh()
    }
}
