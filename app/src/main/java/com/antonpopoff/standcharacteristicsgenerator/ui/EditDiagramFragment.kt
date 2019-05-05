package com.antonpopoff.standcharacteristicsgenerator.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.antonpopoff.standcharacteristicsgenerator.R
import com.antonpopoff.standcharacteristicsgenerator.common.BaseViewFragment
import com.antonpopoff.standcharacteristicsgenerator.views.CharacteristicRatingBar
import com.antonpopoff.standcharacteristicsview.diagram.Rating
import com.antonpopoff.standcharacteristicsview.diagram.StandRating
import kotlinx.android.synthetic.main.fragment_edit_diagram.*
import java.util.*

class EditDiagramFragment : BaseViewFragment() {

    private val random by lazy { Random() }

    override val layoutId = R.layout.fragment_edit_diagram

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preFillCharacteristics()
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

    private fun preFillCharacteristics() {
        arguments?.getParcelable<StandRating>(KEY_STAND_RATING)?.apply {
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

    private fun randomizeRatingBar(ratingBar: CharacteristicRatingBar) {
        ratingBar.setRating(Rating.ratings[random.nextInt(Rating.ratingsCount)])
    }

    private fun applyRatings() {
        val rating = getStandRatings()
        val intent = Intent().apply { putExtra(STAND_RATINGS, rating) }

        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        fragmentManager?.popBackStack()
    }

    private fun getStandRatings() = StandRating(
            potentialRatingBar.rating,
            powerRatingBar.rating,
            speedRatingBar.rating,
            precisionRatingBar.rating,
            durabilityRatingBar.rating,
            rangeRatingBar.rating
    )

    companion object {

        const val STAND_CHARACTERISTICS_CODE = 0

        const val STAND_RATINGS = "stand_ratings"

        private const val KEY_STAND_RATING = "stand_rating"

        fun create(rating: StandRating) = EditDiagramFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_STAND_RATING, rating)
            }
        }
    }
}
