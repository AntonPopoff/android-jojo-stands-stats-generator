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
        if (menuItem.itemId == R.id.edit) {
            pushEditFragment()
            return true
        }

        return false
    }

    private fun pushEditFragment() {
        fragmentManager?.beginTransaction()?.apply {
            setCustomAnimations(
                    R.anim.fragment_enter,
                    R.anim.fragment_exit,
                    R.anim.fragment_pop_enter,
                    R.anim.fragment_pop_exit
            )
            replace(R.id.container, EditDiagramFragment())
            addToBackStack(null)
            commit()
        }
    }
}
