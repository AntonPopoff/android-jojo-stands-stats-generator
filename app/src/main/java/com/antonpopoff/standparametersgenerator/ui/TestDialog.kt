package com.antonpopoff.standparametersgenerator.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.utils.EmptyAnimationListener

class TestDialog(context: Context) : Dialog(context) {

    private val decelerateInterpolator = DecelerateInterpolator()
    private val dismissAnimationListener = DismissAnimationListener(this)

    private var root: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        root = layoutInflater.inflate(R.layout.dialog_fragment_edit_diagram_color, null) as ViewGroup
        setContentView(root)

        root?.setOnApplyWindowInsetsListener { v, insets ->
            if (insets.hasInsets()) {
                root!!.setPadding(root!!.paddingLeft, root!!.paddingTop, root!!.paddingRight, insets.systemWindowInsetBottom)
                root!!.forceLayout()
            }
            insets.consumeSystemWindowInsets()
        }

        root?.setOnClickListener {
            val translateAnimation = TranslateAnimation(0, 0f, 0, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 1f)
            translateAnimation.interpolator = decelerateInterpolator
            translateAnimation.duration = 350

            val alphaAnimation = AlphaAnimation(1f, 0f)
            alphaAnimation.interpolator = decelerateInterpolator
            alphaAnimation.duration = 350

            alphaAnimation.setAnimationListener(dismissAnimationListener)

            val contentView = root?.getChildAt(1)
            val backgroundView = root?.getChildAt(0)

            contentView?.startAnimation(translateAnimation)
            backgroundView?.startAnimation(alphaAnimation)
        }

        window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            setWindowAnimations(0)
            setBackgroundDrawable(null)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun show() {
        super.show()

        val translateAnimation = TranslateAnimation(0, 0f, 0, 0f, TranslateAnimation.RELATIVE_TO_SELF, 1f, TranslateAnimation.RELATIVE_TO_SELF, 0f)
        translateAnimation.interpolator = decelerateInterpolator
        translateAnimation.duration = 350

        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.interpolator = decelerateInterpolator
        alphaAnimation.duration = 350

        val contentView = root?.getChildAt(1)
        val backgroundView = root?.getChildAt(0)

        contentView?.startAnimation(translateAnimation)
        backgroundView?.startAnimation(alphaAnimation)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            val translateAnimation = TranslateAnimation(0, 0f, 0, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 1f)
            translateAnimation.interpolator = decelerateInterpolator
            translateAnimation.duration = 350

            val alphaAnimation = AlphaAnimation(1f, 0f)
            alphaAnimation.interpolator = decelerateInterpolator
            alphaAnimation.duration = 350

            alphaAnimation.setAnimationListener(dismissAnimationListener)

            val contentView = root?.getChildAt(1)
            val backgroundView = root?.getChildAt(0)

            contentView?.startAnimation(translateAnimation)
            backgroundView?.startAnimation(alphaAnimation)

            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private class DismissAnimationListener(private val dialog: Dialog) : EmptyAnimationListener {

        override fun onAnimationEnd(animation: Animation) {
            dialog.dismiss()
        }
    }
}
