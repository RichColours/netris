package netris.animation

class ParallelAnimator(
    private vararg val animators: Animator,
) : Animator {

    private var i = 0

    override fun step() {

        animators.forEach {
            if (!it.isComplete)
                it.step()
        }
    }

    override val isComplete: Boolean get() = animators.all { it.isComplete }
}
