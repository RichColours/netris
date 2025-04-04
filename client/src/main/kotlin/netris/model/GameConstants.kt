package netris.model

class GameConstants {

    companion object {

        val boardWidth = 10
        val boardHeight = 18

        val viewPortTotalWidth = 20
        val viewPortTotalHeight = 20

        val viewPortXMax = viewPortTotalWidth - 1
        val viewPortYMax = viewPortTotalHeight - 1

        val viewPortXRange = (0 .. viewPortXMax)
        val viewPortYRange = (0 .. viewPortYMax)

    }
}