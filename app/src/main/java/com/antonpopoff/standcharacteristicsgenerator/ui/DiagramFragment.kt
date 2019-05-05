package com.antonpopoff.standcharacteristicsgenerator.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.antonpopoff.standcharacteristicsgenerator.R
import com.antonpopoff.standcharacteristicsgenerator.common.BaseViewFragment
import com.antonpopoff.standcharacteristicsview.diagram.StandRating
import kotlinx.android.synthetic.main.fragment_diagram.*

class DiagramFragment : BaseViewFragment() {

    private var rating = StandRating.UNKNOWN

    override val layoutId = R.layout.fragment_diagram

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        standStatsDiagram.rating = rating
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
        val f = EditDiagramFragment.create(rating).also {
            it.setTargetFragment(this, EditDiagramFragment.STAND_CHARACTERISTICS_CODE)
        }

        fragmentManager?.beginTransaction()?.apply {
            setCustomAnimations(
                    R.anim.fragment_enter,
                    R.anim.fragment_exit,
                    R.anim.fragment_pop_enter,
                    R.anim.fragment_pop_exit
            )
            replace(R.id.container, f)
            addToBackStack(null)
            commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkResult(requestCode, resultCode, data)
    }

    private fun checkResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EditDiagramFragment.STAND_CHARACTERISTICS_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<StandRating>(EditDiagramFragment.STAND_RATINGS)?.let {
                rating = it
            }
        }
    }
}
