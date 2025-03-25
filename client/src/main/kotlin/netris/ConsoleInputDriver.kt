package netris

import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import java.util.function.Consumer

class ConsoleInputDriver(
    private val screen: Screen
) : InputDriver {

    private var receiver: Consumer<Input>? = null
    private var pumpingThread: Thread? = null

    @Synchronized
    override fun clearAndSetReceive(fn: Consumer<Input>) {

        receiver = fn

        pumpingThread = Thread({
            pumper()
        }, "InputDriverPump")

        pumpingThread!!.start()
    }

    @Synchronized
    override fun disconnect() {

        if (pumpingThread != null) {

            receiver = null
            pumpingThread!!.interrupt()

            pumpingThread = null
        }
    }

    private fun pumper() {

        while (receiver != null) {

            try {
                val ks = screen.readInput()

                when {
                    ks.keyType == KeyType.Escape -> receiver!!.accept(Input.ESCAPE)
                    ks.keyType == KeyType.ArrowLeft -> receiver!!.accept(Input.LEFT)
                    ks.keyType == KeyType.ArrowRight -> receiver!!.accept(Input.RIGHT)
                    ks.keyType == KeyType.ArrowDown -> receiver!!.accept(Input.DOWN)
                    ks.character == 'a' -> receiver!!.accept(Input.A)
                    ks.character == 'd' -> receiver!!.accept(Input.D)
                }

            } catch (e: InterruptedException) {
                println("Awoken from interruption")
            }
        }
    }
}
