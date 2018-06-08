package com.antonpopoff.standstatsgenerator.views

import android.view.View

fun View.pxToDp(px: Int) = this.resources.displayMetrics.density * px
