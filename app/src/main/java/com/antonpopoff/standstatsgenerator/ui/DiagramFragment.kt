package com.antonpopoff.standstatsgenerator.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.antonpopoff.standstatsgenerator.R
import com.antonpopoff.standstatsgenerator.common.BaseViewFragment
import kotlinx.android.synthetic.main.fragment_diagram.*

class DiagramFragment : BaseViewFragment() {

    override val layoutId = R.layout.fragment_diagram

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarMenu()
    }

    private fun setupToolbarMenu() {
        toolbar.apply {
            inflateMenu(R.menu.fragment_diagram_toolbar_menu)
            setOnMenuItemClickListener { onToolbarMenuClick(it) }
        }
    }

    private fun onToolbarMenuClick(menuItem: MenuItem): Boolean {
        return false
    }
}
