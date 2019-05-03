package com.antonpopoff.standcharacteristicsgenerator.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.antonpopoff.standcharacteristicsgenerator.R
import com.antonpopoff.standcharacteristicsgenerator.common.BaseViewFragment
import com.antonpopoff.standcharacteristicsgenerator.views.CharacteristicRatingBar
import com.antonpopoff.standcharacteristicsview.diagram.Rating
import kotlinx.android.synthetic.main.fragment_edit_diagram.*
import java.util.*

class EditDiagramFragment : BaseViewFragment() {

    private val random by lazy { Random() }

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

    private fun onMenuItemClick(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.done -> {
            fragmentManager?.popBackStack()
            true
        }
        R.id.randomize -> {
            randomizeStatistics()
            true
        }
        else -> false
    }

    private fun randomizeStatistics() {
        randomizeRatingBar(potentialRatingBar)
        randomizeRatingBar(powerRatingBar)
        randomizeRatingBar(speedRatingBar)
        randomizeRatingBar(rangeRatingBar)
        randomizeRatingBar(durabilityRatingBar)
        randomizeRatingBar(precisionRatingBar)
    }

    private fun randomizeRatingBar(ratingBar: CharacteristicRatingBar) {
        ratingBar.setRating(Rating.ratings[random.nextInt(Rating.ratingsCount)])
    }
}
