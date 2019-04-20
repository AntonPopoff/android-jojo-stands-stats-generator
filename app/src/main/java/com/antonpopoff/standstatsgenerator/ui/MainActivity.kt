package com.antonpopoff.standstatsgenerator.ui

import android.os.Bundle
import com.antonpopoff.standstatsgenerator.R
import com.antonpopoff.standstatsgenerator.common.BaseViewActivity

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