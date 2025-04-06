package netris

import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val terminal = DefaultTerminalFactory().createTerminal()
            val screen = TerminalScreen(terminal)

            screen.startScreen()
            screen.cursorPosition = null

            val ticker = RealtimeTicker()
            val inputDriver = ConsoleInputDriver(screen)

            ticker.use {

                val welcome = WelcomeLoop(ticker, screen, inputDriver)
                welcome.complete()

                val gameLoop = GameLoop(ticker, screen, inputDriver)
                gameLoop.complete()

                val gameOverLoop = GameOverLoop(ticker, screen, inputDriver)
                gameOverLoop.complete()
            }

            screen.stopScreen(true)
        }
    }
}
