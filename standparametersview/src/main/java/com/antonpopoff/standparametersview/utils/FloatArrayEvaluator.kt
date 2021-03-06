package com.antonpopoff.standparametersview.utils

import android.animation.TypeEvaluator

class FloatArrayEvaluator(private val reuseArray: FloatArray) : TypeEvaluator<FloatArray> {

    override fun evaluate(fraction: Float, startValue: FloatArray, endValue: FloatArray): FloatArray {
        for (i in startValue.indices) {
            reuseArray[i] = startValue[i] + fraction * (endValue[i] - startValue[i])
        }

        return reuseArray
    }
}
