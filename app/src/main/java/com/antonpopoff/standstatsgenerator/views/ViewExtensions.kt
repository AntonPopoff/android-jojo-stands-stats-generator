package com.antonpopoff.standstatsgenerator.views

import android.view.View

fun View.dpToPx(dp: Float) = this.resources.displayMetrics.density * dp

fun View.getDimension(id: Int) = this.resources.getDimension(id)
