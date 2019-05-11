package com.antonpopoff.colorwheel

const val PI = Math.PI.toFloat()

fun toRadians(degrees: Float) = degrees / 180f * PI

fun toDegrees(radians: Float) = radians * 180f / PI
