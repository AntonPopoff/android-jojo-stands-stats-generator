package com.antonpopoff.standparametersgenerator.utils

import android.view.ViewGroup
import android.widget.FrameLayout

const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

fun frameLayoutParams(width: Int, height: Int) = FrameLayout.LayoutParams(width, height)
