package com.antonpopoff.standparametersview.extensions

import android.graphics.Path

fun Path.lineToIfNotEmpty(x: Float, y: Float) {
    if (this.isEmpty) {
        this.moveTo(x, y)
    } else {
        this.lineTo(x, y)
    }
}