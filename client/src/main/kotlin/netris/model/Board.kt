package netris.model

import netris.of
import java.util.function.Supplier

/**
 * A piece is manipulateable until it is ticked() and it cannot go down any further.
 * That is when it is done, moved to done pile and a new one created at the top.
 */
class Board(
    private val width: Int,
    private val height: Int,
    private val pieceGenerator: Supplier<Piece>,
) {

    private val doneFragments = mutableListOf<MutableList<Int?>>()
        .also { v ->
            (0..<height).forEach { _ ->
                v.add(mutableListOf<Int?>()
                    .also { l ->
                        (0..<width).forEach { l.add(null) }
                    })
            }
        }

    private var inPlayPiece: Piece = newCentralisedPiece()
    private var score = 0
    var gameOver = false

    fun timeTick() {

        val oneDown = inPlayPiece.downOne()

        if (isPieceOffBoard(oneDown, width, height) || doesPieceOverlapOthers(oneDown, doneFragments)) {
            // Hit the bottom or overlaps with something

            inPlayPiece.toFragments().forEach {
                if (doneFragments[it.location.y][it.location.x] != null)
                    throw Exception("Serious issue, this slot should be null to receive the new fragment")
                doneFragments[it.location.y][it.location.x] = it.type
            }

            // Do row deletion and scoring
            val scoringRows = (0..<height).flatMap { rowIndex ->
                if (doneFragments[rowIndex].all { it != null })
                    listOf(rowIndex)
                else
                    emptyList()
            }

            val newBlankRows = scoringRows.size.of { newBlankRow() }.toList()

            val keepNonFullRows = doneFragments.filterIndexed { index, _ -> index !in scoringRows }

            val newFragments: List<MutableList<Int?>> = (newBlankRows + keepNonFullRows).toMutableList()

            if (scoringRows.isNotEmpty())
                println("test")

            doneFragments.clear()
            doneFragments.addAll(newFragments)

            score += scoringRows.size

            inPlayPiece = newCentralisedPiece()

            if (doesPieceOverlapOthers(inPlayPiece, doneFragments)) {
                // New piece is already jammed
                // Game over
                gameOver = true
            }

        } else {
            inPlayPiece = oneDown
        }
    }

    fun tryLeft() = trySingleStepMove(inPlayPiece.leftOne())

    fun tryRight() = trySingleStepMove(inPlayPiece.rightOne())

    fun tryRotateAntiCw() = trySingleStepMove(inPlayPiece.rotateAntiCw())

    fun tryRotateCw() = trySingleStepMove(inPlayPiece.rotationCw())

    private fun trySingleStepMove(tryNewPiece: Piece) {

        if (isPieceOffBoard(tryNewPiece, width, height) || doesPieceOverlapOthers(tryNewPiece, doneFragments)) {
            // Cannot move it
            // No change
        } else {
            inPlayPiece = tryNewPiece
        }
    }

    fun tryFallDown() {

        while (true) {
            val downOne = inPlayPiece.downOne()

            if (isPieceOffBoard(downOne, width, height) || doesPieceOverlapOthers(downOne, doneFragments)) {
                // Cannot move it
                // No change
                break
            } else {
                inPlayPiece = downOne
            }
        }
    }

    fun toPointMap(): List<Pair<Int, Coord>> {

        val donePieces = doneFragments.mapIndexed { y, ints ->
            ints.flatMapIndexed { x, i ->
                if (i == null) emptyList() else listOf(i to Coord(x, y))
            }
        }.flatten()

        val inPlay = inPlayPiece.toFragments()
            .map { it.type to Coord(it.location.x, it.location.y) }

        return donePieces + inPlay
    }

    private fun newBlankRow() = width.of { null as Int? }.toMutableList()

    private fun newCentralisedPiece(): Piece {
        val p = pieceGenerator.get()

        return (0..<(width / 2) - 2)
            .fold(p) { a, b -> a.rightOne() }
    }
}
