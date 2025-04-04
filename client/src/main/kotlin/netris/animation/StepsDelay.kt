package netris.animation

class StepsDelay(private var nSteps: Long) : Animator {

    override fun step() {
        nSteps -= 1
    }

    override val isComplete: Boolean
        get() = nSteps == 0L
}
