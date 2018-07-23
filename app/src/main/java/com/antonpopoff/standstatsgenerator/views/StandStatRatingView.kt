package com.antonpopoff.standstatsgenerator.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.antonpopoff.standstatsgenerator.R

class StandStatRatingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    init {
        orientation = LinearLayout.HORIZONTAL
        View.inflate(context, R.layout.layout_stand_stat_rating_view, this)
    }
}