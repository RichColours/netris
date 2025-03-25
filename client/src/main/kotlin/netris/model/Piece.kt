package netris.model

/**
 * What is the orient of a piece?
 * About what does it rotate?
 */
data class Piece (
    val type: Int,
    val locations: List<Coord>,
    val orientIndex: Int
) {

    fun downOne(): Piece =
        Piece(
            type,
            locations.map {
                Coord(it.x, it.y + 1)
            },
            orientIndex
        )

    fun leftOne(): Piece =
        Piece(
            type,
            locations.map {
                Coord(it.x - 1, it.y)
            },
            orientIndex
        )

    fun rightOne(): Piece =
        Piece(
            type,
            locations.map {
                Coord(it.x + 1, it.y)
            },
            orientIndex
        )

    fun rotateAntiCw(): Piece =
        Piece(
            type,
            locations.map {
                it.intRotate(-90, locations[orientIndex])
            },
            orientIndex
        )

    fun rotationCw(): Piece =
        Piece(
            type,
            locations.map {
                it.intRotate(90, locations[orientIndex])
            },
            orientIndex
        )
}
