package com.antonpopoff.standstatsgenerator.views

import android.view.View

fun View.pxToDp(px: Float) = this.resources.displayMetrics.density * px
