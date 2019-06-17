package com.antonpopoff.standparametersgenerator.utils.animation

import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation

val CUBIC_BEZIER_EASY_OUTINT = PathInterpolator(.23f, 1f, .32f, 1f)

val CUBIC_BEZIER_EASY_OUT = PathInterpolator(0f, 0f, .58f, 1f)

fun selfRelativeTranslateAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float) = TranslateAnimation(
        TranslateAnimation.RELATIVE_TO_SELF, fromX,
        TranslateAnimation.RELATIVE_TO_SELF, toX,
        TranslateAnimation.RELATIVE_TO_SELF, fromY,
        TranslateAnimation.RELATIVE_TO_SELF, toY
)
