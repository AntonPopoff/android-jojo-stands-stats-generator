package com.antonpopoff.standstatsgenerator.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.antonpopoff.standstatsgenerator.R
import com.antonpopoff.standstatsgenerator.common.BaseViewFragment
import kotlinx.android.synthetic.main.fragment_edit_diagram.*

class EditDiagramFragment : BaseViewFragment() {

    override val layoutId = R.layout.fragment_edit_diagram

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { fragmentManager?.popBackStack() }
            inflateMenu(R.menu.fragment_edit_diagram_toolbar_menu)
            setOnMenuItemClickListener { onMenuItemClick(it) }
        }
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.done) {
            fragmentManager?.popBackStack()
            return true
        }

        return false
    }
}
