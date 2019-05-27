package com.antonpopoff.standparametersgenerator.common

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.core.view.GestureDetectorCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import com.antonpopoff.standparametersgenerator.R

abstract class SwipeableDialog : BaseViewDialogFragment() {

    private lateinit var gestureDetector: GestureDetectorCompat

    private var dialogLayoutAnimator: ViewPropertyAnimator? = null
    private var flingAnimator: FlingAnimation? = null

    private var maxFlingVelocity = 0f
    private var animatingDialogFling = false
    private var animatingDialogFinalization = false

    private lateinit var contentRoot: View

    override fun onAttach(context: Context) {
        super.onAttach(context)
        maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity.toFloat()
        gestureDetector = GestureDetectorCompat(context, GesturesListener())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeableContainer = inflater.inflate(R.layout.dialog_fragment_swipeable_dialog, container, false) as ViewGroup
        contentRoot = (inflater.inflate(layoutId, swipeableContainer, true) as ViewGroup).getChildAt(0)
        return swipeableContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(this::onDialogContainerTouch)
    }

    private fun createFlingAnimator(velocityY: Float) = FlingAnimation(contentRoot, DynamicAnimation.Y).apply {
        setStartVelocity(velocityY)
        setMinValue(0f)
        setMaxValue(contentRoot.height.toFloat())
        setStartValue(contentRoot.y)
        addEndListener { _, _, _, _ -> onFlingAnimationEnd() }
    }

    private fun onDialogContainerTouch(v: View, event: MotionEvent) = if (gestureDetector.onTouchEvent(event)) {
        true
    } else if (event.action == MotionEvent.ACTION_UP && !animatingDialogFling) {
        finalizeDialogPosition()
        false
    } else {
        false
    }

    private fun updateRootY(y: Float) {
        contentRoot.y = y
        ensureRootLayoutInBounds()
    }

    private fun ensureRootLayoutInBounds() {
        if (contentRoot.y < 0) {
            contentRoot.y = 0f
        } else if (contentRoot.y > contentRoot.height) {
            contentRoot.y = contentRoot.height.toFloat()
        }
    }

    private fun finalizeDialogPosition() {
        val finalY = if (contentRoot.y <= contentRoot.height / 2) 0f else contentRoot.height.toFloat()
        animatingDialogFinalization = true
        dialogLayoutAnimator = contentRoot.animate().y(finalY).withEndAction(this::onDialogFinalizationEnd)
    }

    private fun onDialogFinalizationEnd() {
        animatingDialogFinalization = false
        dismissDialogAfterAnimation()
    }

    private fun onFlingAnimationEnd() {
        animatingDialogFling = false
        dismissDialogAfterAnimation()
    }

    private fun dismissDialogAfterAnimation() {
        if (contentRoot.y >= contentRoot.height / 2) {
            dismiss()
        }
    }

    @CallSuper
    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dialogLayoutAnimator?.cancel()
        flingAnimator?.cancel()
    }

    private inner class GesturesListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent) = !animatingDialogFinalization && !animatingDialogFling

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            updateRootY(contentRoot.y - distanceY)
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (Math.abs(velocityY / maxFlingVelocity) >= FLING_POWER_THRESHOLD) {
                animatingDialogFling = true
                flingAnimator = createFlingAnimator(velocityY).apply { start() }
            } else {
                finalizeDialogPosition()
            }

            return true
        }
    }

    companion object {

        private const val FLING_POWER_THRESHOLD = 0.15f
    }
}
