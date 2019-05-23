package com.antonpopoff.standparametersgenerator.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.antonpopoff.standcharacteristicsgenerator.storage.AppDataCache
import com.antonpopoff.standcharacteristicsgenerator.storage.AppDataPreferencesCache
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.common.BaseViewFragment
import com.antonpopoff.standparametersgenerator.dialogs.EditDiagramColorDialog
import com.antonpopoff.standparametersview.diagram.StandParameters
import kotlinx.android.synthetic.main.fragment_diagram.*

class DiagramFragment : BaseViewFragment(), EditDiagramColorDialog.Listener {

    private lateinit var appDataCache: AppDataCache
    private var rating = StandParameters.UNKNOWN
    private var polylineColorAnimator: ValueAnimator? = null

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
            it.standParameters = rating
            it.polylineColor = appDataCache.readDiagramColor(defColor)
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
        EditDiagramColorDialog
                .create(standParametersDiagram.polylineColor)
                .show(childFragmentManager, null)
    }

    private fun pushEditFragment() {
        val f = EditDiagramFragment.create(rating).also {
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
                standParametersDiagram.standParameters = it
                rating = it
            }
        }
    }

    override fun onColorApplied(argb: Int) {
        Handler().postDelayed({
            appDataCache.saveDiagramColor(argb)
            animateParametersPolylineColor(argb)
        }, resources.getInteger(R.integer.statistics_dialog_anim_duration).toLong())
    }

    private fun animateParametersPolylineColor(argb: Int) {
        polylineColorAnimator?.end()
        polylineColorAnimator = createColorAnimator(argb).apply { start() }
    }

    private fun createColorAnimator(argb: Int) = ObjectAnimator.ofInt(standParametersDiagram.polylineColor, argb).apply {
        addUpdateListener { standParametersDiagram?.polylineColor = it.animatedValue as Int }
        setEvaluator(ArgbEvaluator())
        interpolator = LinearInterpolator()
        duration = 750
    }
}
