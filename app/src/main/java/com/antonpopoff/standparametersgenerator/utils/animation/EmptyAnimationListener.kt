package com.antonpopoff.standparametersgenerator.utils.animation

import android.view.animation.Animation

interface EmptyAnimationListener : Animation.AnimationListener {

    override fun onAnimationRepeat(animation: Animation) { }

    override fun onAnimationEnd(animation: Animation) { }

    override fun onAnimationStart(animation: Animation) { }
}
