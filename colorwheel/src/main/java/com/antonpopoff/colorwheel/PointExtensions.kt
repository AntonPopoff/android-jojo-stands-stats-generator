package com.antonpopoff.colorwheel

import android.graphics.PointF
import android.view.MotionEvent

fun PointF.set(motionEvent: MotionEvent) = this.set(motionEvent.x, motionEvent.y)