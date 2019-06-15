package com.antonpopoff.standparametersgenerator.ui

import android.os.Bundle
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.common.BaseViewActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseViewActivity() {

    override val layoutId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container.setOnClickListener {
            TestDialog(this).show()
        }
    }

    private fun addDiagramFragmentIfAbsent() {
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, DiagramFragment())
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (container.childCount > 0) {
            container.removeViewAt(0)
        } else {
            super.onBackPressed()
        }
    }
}
