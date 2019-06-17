package com.antonpopoff.standparametersgenerator.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.common.BaseViewFragment
import com.antonpopoff.standparametersgenerator.dialogs.EditDiagramColorDialog
import com.antonpopoff.standparametersgenerator.storage.AppDataCache
import com.antonpopoff.standparametersgenerator.storage.AppDataPreferencesCache
import com.antonpopoff.standparametersview.diagram.StandParameters
import kotlinx.android.synthetic.main.fragment_diagram.*

class DiagramFragment : BaseViewFragment(), EditDiagramColorDialog.Listener {

    private val handler = Handler()

    private lateinit var appDataCache: AppDataCache

    override val layoutId = R.layout.fragment_diagram

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appDataCache = AppDataPreferencesCache(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStandDiagram()
        setupToolbarMenu()
    }

    private fun setupStandDiagram() {
        val defColor = ResourcesCompat.getColor(resources, R.color.magenta, context?.theme)

        standParametersDiagram.also {
            it.setParameters(appDataCache.readStandRating(StandParameters.UNKNOWN))
            it.setPolylineColor(appDataCache.readDiagramColor(defColor))
        }
    }

    private fun setupToolbarMenu() {
        toolbar.apply {
            inflateMenu(R.menu.fragment_diagram_toolbar_menu)
            setOnMenuItemClickListener { onToolbarMenuClick(it) }
        }
    }

    private fun onToolbarMenuClick(menuItem: MenuItem) = when(menuItem.itemId) {
        R.id.edit_diagram -> {
            pushEditFragment()
            true
        }
        R.id.edit_diagram_color -> {
            showEditColorDiagramFragment()
            true
        }
        else -> false
    }

    private fun showEditColorDiagramFragment() {
        context?.let { EditDiagramColorDialog(it, standParametersDiagram.polylineColor, this).show() }
    }

    private fun pushEditFragment() {
        val f = EditDiagramFragment.create(standParametersDiagram.standParameters).also {
            it.setTargetFragment(this, EditDiagramFragment.STAND_PARAMETERS_CODE)
        }

        fragmentManager?.beginTransaction()?.apply {
            setCustomAnimations(R.anim.fragment_enter, 0, 0, R.anim.fragment_pop_exit)
            add(R.id.container, f)
            addToBackStack(null)
            commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkResult(requestCode, resultCode, data)
    }

    private fun checkResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EditDiagramFragment.STAND_PARAMETERS_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<StandParameters>(EditDiagramFragment.STAND_RATINGS)?.let {
                updateStandParameters(it)
                appDataCache.saveStandRating(it)
            }
        }
    }

    private fun updateStandParameters(parameters: StandParameters) {
        handler.postDelayed({
            standParametersDiagram.setParameters(parameters, true)
        }, resources.getInteger(R.integer.fragments_transaction_duration).toLong())
    }

    override fun onColorApplied(argb: Int) {
        appDataCache.saveDiagramColor(argb)
        standParametersDiagram?.setPolylineColor(argb, true)
    }
}
