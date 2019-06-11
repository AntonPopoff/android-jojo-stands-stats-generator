package com.antonpopoff.standparametersgenerator.dialogs

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import java.lang.ref.WeakReference

class TestBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private var state = -1
    private var viewRef: WeakReference<View>? = null

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        parent.onLayoutChild(child, layoutDirection)

        child.translationY = parent.height.toFloat()
//        ViewCompat.offsetTopAndBottom(child, parent.height)

        if (viewRef === null) {
            viewRef = WeakReference(child)
        }

        if (state != -1) {
            expand()
        }

        return true
    }

    fun expand() {
        val v = viewRef?.get()

        if (v !== null) {
            val parent = v.parent

            if (parent !== null && parent.isLayoutRequested && ViewCompat.isAttachedToWindow(v)) {
                v.post {
                    expandInternal(v)
                }
            } else {
                expandInternal(v)
            }
        } else {
            state = 0
        }
    }

    private fun expandInternal(v: View) {
//        val v = viewRef?.get();

//        if (v !== null) {
            v.animate()
                    .translationYBy(-v.height.toFloat())
                    .setDuration(400)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
//        }
    }
}