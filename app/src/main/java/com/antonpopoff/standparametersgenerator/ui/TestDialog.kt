package com.antonpopoff.standparametersgenerator.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.antonpopoff.standparametersgenerator.R

class TestDialog(container: ViewGroup) {

    val layout: View

    init {
        layout = LayoutInflater.from(container.context).inflate(R.layout.dialog_fragment_edit_diagram_color, container, false)
        startAnimation()
    }

    private fun startAnimation() {
        val a = AnimationUtils.loadAnimation(layout.context, R.anim.statistics_dialog_in)
        layout.findViewById<View>(R.id.content).startAnimation(a)
    }
}