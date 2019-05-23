package com.antonpopoff.standparametersgenerator.common

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseViewFragment : Fragment() {

    protected var preferredOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = preferredOrientation
        view?.isClickable = true
    }

    override fun onPause() {
        super.onPause()
        view?.isClickable = false
    }
}
