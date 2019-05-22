package com.antonpopoff.standcharacteristicsgenerator.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.antonpopoff.standcharacteristicsgenerator.R
import com.antonpopoff.standcharacteristicsgenerator.common.BaseViewFragment
import com.antonpopoff.standcharacteristicsgenerator.dialogs.EditDiagramColorDialog
import com.antonpopoff.standcharacteristicsview.diagram.StandRating
import kotlinx.android.synthetic.main.fragment_diagram.*

class DiagramFragment : BaseViewFragment(), EditDiagramColorDialog.Listener {

    private var rating = StandRating.UNKNOWN
    private var polylineColorAnimator: ValueAnimator? = null
    private var polylineColor = 0

    override val layoutId = R.layout.fragment_diagram

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        polylineColor = ResourcesCompat.getColor(resources, R.color.magenta, context?.theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStandDiagram()
        setupToolbarMenu()
    }

    private fun setupStandDiagram() {
        standCharacteristicsDiagram.also {
            it.rating = rating
            it.polylineColor = polylineColor
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
                .create(standCharacteristicsDiagram.polylineColor)
                .show(childFragmentManager, null)
    }

    private fun pushEditFragment() {
        val f = EditDiagramFragment.create(rating).also {
            it.setTargetFragment(this, EditDiagramFragment.STAND_CHARACTERISTICS_CODE)
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
        if (requestCode == EditDiagramFragment.STAND_CHARACTERISTICS_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<StandRating>(EditDiagramFragment.STAND_RATINGS)?.let {
                standCharacteristicsDiagram.rating = it
                rating = it
            }
        }
    }

    override fun onColorApplied(argb: Int) {
        Handler().postDelayed({
            polylineColor = argb
            animateCharacteristicPolylineColor(argb)
        }, resources.getInteger(R.integer.statistics_dialog_anim_duration).toLong())
    }

    private fun animateCharacteristicPolylineColor(argb: Int) {
        polylineColorAnimator?.end()
        polylineColorAnimator = createColorAnimator(argb).apply { start() }
    }

    private fun createColorAnimator(argb: Int) = ObjectAnimator.ofInt(standCharacteristicsDiagram.polylineColor, argb).apply {
        addUpdateListener { standCharacteristicsDiagram?.polylineColor = it.animatedValue as Int }
        setEvaluator(ArgbEvaluator())
        interpolator = LinearInterpolator()
        duration = 750
    }
}
