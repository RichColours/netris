package netris.model

import java.util.function.Supplier

/**
 * A piece is manipulateable until it is ticked() and it cannot go down any further.
 * That is when it is done, moved to done pile and a new one created at the top.
 */
class Board(
    val width: Int,
    val height: Int,
    private val pieceGenerator: Supplier<Piece>,
) {

    private val donePieces = mutableListOf<Piece>()
    private var inPlayPiece: Piece = pieceGenerator.get()

    fun timeTick() {

        val oneDown = inPlayPiece.downOne()

        if (isPieceOffBoard(oneDown, width, height) || doesPieceOverlapOthers(oneDown, donePieces)) {
            // Hit the bottom or overlaps with something
            donePieces += inPlayPiece
            inPlayPiece = pieceGenerator.get()
        } else {
            inPlayPiece = oneDown
        }

    }

    fun tryLeft() {

    }

    fun tryRight() {

    }

    fun tryFallDown() {

    }

    fun toPointMap() = (donePieces + inPlayPiece)
        .map {
            val type = it.type
            it.locations.map {
                Pair(type, Coord(it.x, it.y))
            }
        }
        .flatten()
}
