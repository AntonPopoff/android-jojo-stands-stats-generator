package com.antonpopoff.standstatsgenerator.utils.listeners

import android.support.design.widget.BottomSheetBehavior
import android.view.View

open class SimpleBottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {

    override fun onSlide(bottomSheet: View, slideOffset: Float) { }

    override fun onStateChanged(bottomSheet: View, newState: Int) { }
}
