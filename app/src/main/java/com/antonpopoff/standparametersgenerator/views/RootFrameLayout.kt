package com.antonpopoff.standparametersgenerator.views

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout

class RootFrameLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        for (i in 0 until childCount) getChildAt(i).dispatchApplyWindowInsets(insets)
        return insets
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        return super.dispatchApplyWindowInsets(insets)
    }
}
