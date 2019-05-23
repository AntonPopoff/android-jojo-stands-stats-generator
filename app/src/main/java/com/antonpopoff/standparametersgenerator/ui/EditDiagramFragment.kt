package com.antonpopoff.standparametersgenerator.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.common.BaseViewFragment
import com.antonpopoff.standparametersgenerator.views.StandParameterRatingBar
import com.antonpopoff.standparametersview.diagram.ParameterRating
import com.antonpopoff.standparametersview.diagram.StandParameters
import kotlinx.android.synthetic.main.fragment_edit_diagram.*
import java.util.*

class EditDiagramFragment : BaseViewFragment() {

    private val random by lazy { Random() }

    override val layoutId = R.layout.fragment_edit_diagram

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preFillStandParameters()
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

    private fun preFillStandParameters() {
        arguments?.getParcelable<StandParameters>(KEY_STAND_RATING)?.apply {
            potentialRatingBar.setRating(potential, false)
            powerRatingBar.setRating(power, false)
            speedRatingBar.setRating(speed, false)
            rangeRatingBar.setRating(range, false)
            durabilityRatingBar.setRating(durability, false)
            precisionRatingBar.setRating(precision, false)
        }
    }

    private fun onMenuItemClick(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.apply -> {
            applyRatings()
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

    private fun randomizeRatingBar(ratingBar: StandParameterRatingBar) {
        ratingBar.setRating(ParameterRating.ratings[random.nextInt(ParameterRating.ratingsCount)])
    }

    private fun applyRatings() {
        val rating = getStandRatings()
        val intent = Intent().apply { putExtra(STAND_RATINGS, rating) }

        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        fragmentManager?.popBackStack()
    }

    private fun getStandRatings() = StandParameters(
            potentialRatingBar.rating,
            powerRatingBar.rating,
            speedRatingBar.rating,
            precisionRatingBar.rating,
            durabilityRatingBar.rating,
            rangeRatingBar.rating
    )

    companion object {

        const val STAND_PARAMETERS_CODE = 0

        const val STAND_RATINGS = "stand_ratings"

        private const val KEY_STAND_RATING = "stand_rating"

        fun create(rating: StandParameters) = EditDiagramFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_STAND_RATING, rating)
            }
        }
    }
}
