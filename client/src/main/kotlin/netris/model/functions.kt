package netris.model

import kotlin.random.Random

enum class PieceType(
    val coords: List<Coord>,
    val orientIndex: Int
) {

    Square(listOf(Coord(0, 0), Coord(1, 0), Coord(1, 1), Coord(0, 1)), 0),
    Straight(listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0)), 0),
    Sss(listOf(Coord(1, 0), Coord(2, 0), Coord(0, 1), Coord(1, 1)), 0), // TODO
    Tee(listOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(1, 1)), 0),
    Zed(listOf(Coord(0, 0), Coord(1, 0), Coord(1, 1), Coord(2, 1)), 0),
    Ell(listOf(Coord(0, 1), Coord(1, 1), Coord(2, 1), Coord(3, 1), Coord(3, 0)), 0),
    Jay(listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0), Coord(3, 1)), 0)
    ;

}

fun pieceOf(pieceType: PieceType) = Piece(pieceType.ordinal, pieceType.coords, pieceType.orientIndex)

fun randomPiece() = pieceOf(
    PieceType.entries[Random.nextInt(0, PieceType.entries.size)]
)

fun doesPieceOverlapOthers(thisPiece: Piece, others: List<Piece>) =
    thisPiece.locations.any {
        others.flatMap {
            it.locations
        }.contains(it)
    }

fun isPieceOffBoard(thisPiece: Piece, width: Int, height: Int) =
    thisPiece.locations.any {
        it.x !in (0..<width) || it.y !in (0..<height)
    }
