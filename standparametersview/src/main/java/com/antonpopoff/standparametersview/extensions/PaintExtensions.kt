package com.antonpopoff.standparametersview.extensions

import android.graphics.Rect
import android.text.TextPaint

fun TextPaint.getTextHeight(text: String, start: Int, end: Int, rect: Rect) =
        getTextBounds(text, start, end, rect).let { rect.height() }
