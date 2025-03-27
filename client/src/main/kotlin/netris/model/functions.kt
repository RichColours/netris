package netris.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class PieceType(
    val coords: List<Coord>,
    val orientIndex: Int,
) {

    Square(listOf(Coord(0, 0), Coord(1, 0), Coord(1, 1), Coord(0, 1)), 0),
    Straight(listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0)), 1),
    Sss(listOf(Coord(2, 0), Coord(1, 0), Coord(1, 1), Coord(0, 1)), 1),
    Tee(listOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(1, 1)), 1),
    Zed(listOf(Coord(0, 0), Coord(1, 0), Coord(1, 1), Coord(2, 1)), 1),
    Ell(listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(0, 1)), 0),
    Jay(listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(2, 1)), 2)
    ;

}

fun pieceOf(pieceType: PieceType) = Piece(pieceType.ordinal, pieceType.coords, pieceType.orientIndex)

fun randomPiece() = pieceOf(
    PieceType.entries[Random.nextInt(0, PieceType.entries.size)]
)

fun doesPieceOverlapOthers(thisPiece: Piece, others: List<List<Int?>>) =
    thisPiece.locations.any { loc ->
        others[loc.y][loc.x] != null
    }

fun isPieceOffBoard(thisPiece: Piece, width: Int, height: Int) =
    thisPiece.locations.any {
        it.x !in (0..<width) || it.y !in (0..<height)
    }

fun Coord.intRotate(deg: Int, origin: Coord): Coord {

    val xDiff = this.x - origin.x
    val yDiff = this.y - origin.y
    val rads = Math.toRadians(deg.toDouble())

    val newX = origin.x + xDiff * cos(rads) - yDiff * sin(rads)
    val newY = origin.y + xDiff * sin(rads) + yDiff * cos(rads)

    return Coord(Math.round(newX).toInt(), Math.round(newY).toInt())
}

data class Fragment(
    val type: Int,
    val location: Coord,
)
