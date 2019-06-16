package com.antonpopoff.standparametersgenerator.utils

import android.view.animation.TranslateAnimation

fun selfRelativeTranslateAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float) = TranslateAnimation(
        TranslateAnimation.RELATIVE_TO_SELF, fromX,
        TranslateAnimation.RELATIVE_TO_SELF, toX,
        TranslateAnimation.RELATIVE_TO_SELF, fromY,
        TranslateAnimation.RELATIVE_TO_SELF, toY
)
