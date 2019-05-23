package com.antonpopoff.standparametersgenerator.ui

import android.os.Bundle
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.common.BaseViewActivity

class MainActivity : BaseViewActivity() {

    override val layoutId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDiagramFragmentIfAbsent()
    }

    private fun addDiagramFragmentIfAbsent() {
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, DiagramFragment())
                    .commit()
        }
    }
}
