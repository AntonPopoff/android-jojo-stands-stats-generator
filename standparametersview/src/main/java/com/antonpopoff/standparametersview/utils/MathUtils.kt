package com.antonpopoff.standparametersview.utils

import kotlin.math.cos
import kotlin.math.sin

const val PI = Math.PI.toFloat()

fun toRadians(degrees: Float) = degrees / 180f * PI

fun toDegrees(radians: Float) = radians * 180f / PI

fun xOnCircle(radians: Float, r: Float, centerX: Float = 0f) = r * cos(radians) + centerX

fun yOnCircle(radians: Float, r: Float, centerY: Float = 0f) = r * sin(radians) + centerY
