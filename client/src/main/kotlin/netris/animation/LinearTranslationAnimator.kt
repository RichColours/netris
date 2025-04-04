package netris.animation

import netris.model.Coord
import netris.model.MutableCoord
import kotlin.math.roundToInt

class LinearTranslationAnimator(
    private val animateCoord: MutableCoord,
    private val translationDestination: Coord,
    duration: Long,
) : CoordinateAnimator(animateCoord) {

    private val xDiff = (translationDestination.x - animateCoord.x).toDouble() / duration
    private val yDiff = (translationDestination.y - animateCoord.y).toDouble() / duration
    private var remaining = duration
    private var accumulateX = animateCoord.x.toDouble()
    private var accumulateY = animateCoord.y.toDouble()

    override fun step() {

        if (remaining == 1L) {
            animateCoord.x = translationDestination.x
            animateCoord.y = translationDestination.y
        }

        if (remaining > 1) {
            accumulateX += xDiff
            accumulateY +=  yDiff
            animateCoord.x = accumulateX.roundToInt()
            animateCoord.y = accumulateY.roundToInt()
        }

        remaining -= 1L
    }

    override val isComplete: Boolean get() = remaining == 0L

}
