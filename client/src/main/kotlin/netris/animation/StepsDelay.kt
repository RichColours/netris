package netris.animation

class StepsDelay(
    private var nSteps: Long,
    private val next: Animator,
) : Animator {

    override fun step() {
        if (nSteps > 0) {
            nSteps -= 1
        } else {
            next.step()
        }
    }

    override val isComplete get() = if (nSteps == 0L) next.isComplete else false
}
