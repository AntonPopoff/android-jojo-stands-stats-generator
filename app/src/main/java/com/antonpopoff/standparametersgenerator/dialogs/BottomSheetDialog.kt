package com.antonpopoff.standparametersgenerator.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.widget.FrameLayout
import com.antonpopoff.standparametersgenerator.utils.MATCH_PARENT
import com.antonpopoff.standparametersgenerator.utils.WRAP_CONTENT
import com.antonpopoff.standparametersgenerator.utils.animation.CUBIC_BEZIER_EASY_OUT
import com.antonpopoff.standparametersgenerator.utils.animation.CUBIC_BEZIER_EASY_OUTINT
import com.antonpopoff.standparametersgenerator.utils.animation.EmptyAnimationListener
import com.antonpopoff.standparametersgenerator.utils.animation.selfRelativeTranslateAnimation
import com.antonpopoff.standparametersgenerator.utils.frameLayoutParams

abstract class BottomSheetDialog(context: Context) : Dialog(context) {

    private var dismissing = false

    private lateinit var container: ViewGroup
    private lateinit var containerBackground: View
    private lateinit var dialogContentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        setupDialogViews()
        setContentView(container)
        setupDialogWindow()
    }

    private fun setupDialogViews() {
        container = createContainer()
        containerBackground = createDialogBackgroundView()
        dialogContentView = provideDialogContentView(layoutInflater, container)

        dialogContentView.isClickable = true
        container.addView(containerBackground, frameLayoutParams(MATCH_PARENT, MATCH_PARENT))
        container.addView(dialogContentView, frameLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { gravity = Gravity.BOTTOM })

        onViewAddedToDialog(dialogContentView)
    }

    private fun createContainer() = FrameLayout(context).apply {
        setOnClickListener { dismiss() }
        setOnApplyWindowInsetsListener { v, insets -> onContainerInsets(v, insets) }
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun onContainerInsets(container: View, insets: WindowInsets): WindowInsets {
        if (insets.hasSystemWindowInsets()) {
            container.setPadding(
                    container.paddingLeft,
                    container.paddingTop,
                    container.paddingBottom,
                    insets.systemWindowInsetBottom
            )
        }

        return insets.consumeSystemWindowInsets()
    }

    private fun createDialogBackgroundView() = View(context).apply {
        setBackgroundColor(DIALOG_BACKGROUND_COLOR)
    }

    private fun setupDialogWindow() {
        window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(0)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (handleBackKey(keyCode, event)) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun handleBackKey(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            dismiss()
            true
        } else {
            false
        }
    }

    override fun show() {
        super.show()
        startShowAnimation()
    }

    private fun startShowAnimation() {
        startDialogTransitionAnimation(1f, 0f, 0f, 1f, TRANSITION_ANIM_IN_DURATION, CUBIC_BEZIER_EASY_OUTINT)
    }

    private fun startDismissAnimation() {
        startDialogTransitionAnimation(0f, 1f, 1f, 0f, TRANSITION_ANIM_OUT_DURATION, CUBIC_BEZIER_EASY_OUT, DismissAnimationListener())
    }

    private fun startDialogTransitionAnimation(
            yFrom: Float,
            yTo: Float,
            alphaFrom: Float,
            alphaTo: Float,
            duration: Long,
            interpolator: Interpolator,
            listener: Animation.AnimationListener? = null
    ) {
        dialogContentView.startAnimation(createTranslateAnimation(yFrom, yTo, duration, interpolator, listener))
        containerBackground.startAnimation(createAlphaAnimation(alphaFrom, alphaTo, duration, interpolator))
    }

    private fun createTranslateAnimation(
            yFrom: Float,
            yTo: Float,
            duration: Long,
            interpolator: Interpolator,
            listener: Animation.AnimationListener?
    ) = selfRelativeTranslateAnimation(0f, 0f, yFrom, yTo).also {
        it.setAnimationListener(listener)
        it.interpolator = interpolator
        it.duration = duration
    }

    private fun createAlphaAnimation(
            alphaFrom: Float,
            alphaTo: Float,
            duration: Long,
            interpolator: Interpolator
    ) = AlphaAnimation(alphaFrom, alphaTo).also {
        it.interpolator = interpolator
        it.duration = duration
    }

    override fun dismiss() {
        if (!dismissing) {
            dismissing = true
            startDismissAnimation()
        }
    }

    private fun realDismiss() {
        super.dismiss()
    }

    open fun onDismissed() {}

    open fun onViewAddedToDialog(view: View) {}

    abstract fun provideDialogContentView(inflater: LayoutInflater, container: ViewGroup): View

    private inner class DismissAnimationListener : EmptyAnimationListener {

        override fun onAnimationEnd(animation: Animation) {
            realDismiss()
            onDismissed()
        }
    }

    companion object {

        private const val TRANSITION_ANIM_IN_DURATION = 550L

        private const val TRANSITION_ANIM_OUT_DURATION = 250L

        private const val DIALOG_BACKGROUND_COLOR = 0x30000000
    }
}
