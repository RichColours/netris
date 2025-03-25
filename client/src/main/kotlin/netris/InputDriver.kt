package netris

import java.util.function.Consumer

interface InputDriver {

    fun clearAndSetReceive(fn: Consumer<Input>)

    fun disconnect()
}
