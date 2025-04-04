package netris

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor

class Drawing {

    companion object {

        val sideBar = TextCharacter('║', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
        val bottomLeft = TextCharacter('╚', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
        val bottomRight = TextCharacter('╝', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
        val bottomRow = TextCharacter('═', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
    }
}