package com.antonpopoff.standcharacteristicsgenerator.extensions

import android.graphics.Rect
import android.text.TextPaint

fun TextPaint.getTextHeight(s: String, r: Rect) = getTextBounds(s, r).run { r.height() }

fun TextPaint.getTextBounds(s: String, r: Rect) = getTextBounds(s, 0, s.length, r)
