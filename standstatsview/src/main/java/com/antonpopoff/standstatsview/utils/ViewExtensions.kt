package com.antonpopoff.standstatsview.utils

import android.view.View

fun View.dpToPx(dp: Float) = this.resources.displayMetrics.density * dp

fun View.getDimension(id: Int) = this.resources.getDimension(id)
