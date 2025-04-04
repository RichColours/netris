package netris.animation

class SequentialAnimator(
    private vararg val animators: Animator,
) : Animator {

    private var i = 0

    override fun step() {

        while (i < animators.size && animators[i].isComplete)
            i++

        if (i < animators.size && !animators[i].isComplete)
            animators[i].step()

        while (i < animators.size && animators[i].isComplete)
            i++
    }

    override val isComplete: Boolean get() {
        return i == animators.size
    }
}
