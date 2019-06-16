package com.antonpopoff.standparametersgenerator.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.antonpopoff.standparametersgenerator.utils.*

abstract class BottomSheetDialog(context: Context) : Dialog(context) {

    private var dismissing = false
    private val decelerateInterpolator = DecelerateInterpolator()

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

    private fun startDismissAnimation() {
        startTransitionAnimation(0f, 1f, 1f, 0f, DismissAnimationListener(this))
    }

    private fun startShowAnimation() {
        startTransitionAnimation(1f, 0f, 0f, 1f)
    }

    private fun startTransitionAnimation(yFrom: Float, yTo: Float, alphaFrom: Float, alphaTo: Float, listener: Animation.AnimationListener? = null) {
        dialogContentView.startAnimation(createTranslateAnimation(yFrom, yTo, listener))
        containerBackground.startAnimation(createAlphaAnimation(alphaFrom, alphaTo))
    }

    private fun createTranslateAnimation(yFrom: Float, yTo: Float, listener: Animation.AnimationListener?): Animation {
        return selfRelativeTranslateAnimation(0f, 0f, yFrom, yTo).apply {
            setAnimationListener(listener)
            interpolator = decelerateInterpolator
            duration = TRANSITION_ANIM_DURATION
        }
    }

    private fun createAlphaAnimation(alphaFrom: Float, alphaTo: Float): Animation {
        return AlphaAnimation(alphaFrom, alphaTo).apply {
            interpolator = decelerateInterpolator
            duration = TRANSITION_ANIM_DURATION
        }
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

    private class DismissAnimationListener(private val dialog: BottomSheetDialog) : EmptyAnimationListener {

        override fun onAnimationEnd(animation: Animation) {
            dialog.realDismiss()
        }
    }

    abstract fun provideDialogContentView(inflater: LayoutInflater, container: ViewGroup): View

    companion object {

        private const val TRANSITION_ANIM_DURATION = 350L

        private const val DIALOG_BACKGROUND_COLOR = 0x45000000
    }
}
