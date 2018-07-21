package com.antonpopoff.standstatsgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.antonpopoff.standstatsgenerator.utils.extensions.toggle
import com.antonpopoff.standstatsgenerator.utils.listeners.SimpleBottomSheetCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomSheetControlButton.setOnClickListener { bottomSheetBehavior.toggle() }
        initBottomSheetBehavior()
    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            setBottomSheetCallback(BottomSheetOffsetCallback())
        }
    }

    private inner class BottomSheetOffsetCallback : SimpleBottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            bottomSheetControlButton.translationY = -bottomSheet.height * slideOffset
        }
    }
}
