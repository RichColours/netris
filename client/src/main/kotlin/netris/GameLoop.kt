package netris

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import netris.model.Board
import netris.model.randomPiece
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
class GameLoop(
    private val ticker: Ticker,
    private val screen: TerminalScreen,
    private val inputDriver: InputDriver,
) {

    private val boardHeight = 18
    private val boardWidth = 10
    private val board = Board(boardWidth, boardHeight, ::randomPiece)

    private val sideBar = TextCharacter('║', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
    private val bottomLeft = TextCharacter('╚', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
    private val bottomRight = TextCharacter('╝', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
    private val bottomRow = TextCharacter('═', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)

    private val typeSquare = TextCharacter('▒', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK)
    private val typeStraight = TextCharacter('▒', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK)
    private val typeSss = TextCharacter('▒', TextColor.ANSI.BLUE, TextColor.ANSI.BLACK)
    private val typeTee = TextCharacter('▒', TextColor.ANSI.GREEN, TextColor.ANSI.BLACK)
    private val typeZed = TextCharacter('▒', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK)
    private val typeEll = TextCharacter('▒', TextColor.ANSI.RED_BRIGHT, TextColor.ANSI.BLACK)
    private val typeJay = TextCharacter('▒', TextColor.ANSI.BLUE_BRIGHT, TextColor.ANSI.BLACK)

    private val charTypes = listOf(typeSquare, typeStraight, typeSss, typeTee, typeZed, typeEll, typeJay)

    private val completionCountDownLatch = CountDownLatch(1)

    // Call on main thread, blocks until complete.
    fun complete() {

        inputDriver.clearAndSetReceive(this::inputReceive)
        ticker.startPeriodic(Duration.ofMillis(400), this::tickHandler)

        completionCountDownLatch.await()
        println("Exited latch")

        ticker.stop()
        inputDriver.disconnect()
    }

    @Synchronized
    private fun tickHandler() {

        board.timeTick()
        paint()

        if (board.gameOver) {
            ticker.stop()
        }
    }

    @Synchronized
    private fun inputReceive(i: Input) {

        when (i) {

            Input.ESCAPE -> {
                println("Escape")
                completionCountDownLatch.countDown()
            }

            Input.LEFT -> {

                board.tryLeft()
                paint()
            }

            Input.RIGHT -> {

                board.tryRight()
                paint()
            }

            Input.DOWN -> {

                board.tryFallDown()
                paint()
            }

            Input.A -> {
                board.tryRotateAntiCw()
                paint()
            }

            Input.D -> {
                board.tryRotateCw()
                paint()
            }
        }

        paint()
    }

    private fun paint() {

        screen.clear()

        (0..<boardHeight).forEach {
            screen.setCharacter(0, it, sideBar)
            screen.setCharacter(boardWidth + 1, it, sideBar)
        }

        screen.setCharacter(0, boardHeight, bottomLeft)
        screen.setCharacter(boardWidth + 1, boardHeight, bottomRight)

        (0..<boardWidth).forEach {
            screen.setCharacter(1 + it, boardHeight, bottomRow)
        }


        // Draw pieces into board offset x=1
        val xOffset = 1
        val yOffset = 0

        val drawThese = board.toPointMap()

        drawThese.forEach {
            screen.setCharacter(xOffset + it.second.x, yOffset + it.second.y, charTypes[it.first])
        }

        screen.refresh()
    }
}
