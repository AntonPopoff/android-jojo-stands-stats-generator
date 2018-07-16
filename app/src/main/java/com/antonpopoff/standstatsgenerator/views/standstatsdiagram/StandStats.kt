package com.antonpopoff.standstatsgenerator.views.standstatsdiagram

data class StandStats(
        val potential: Rating,
        val power: Rating,
        val speed: Rating,
        val precision: Rating,
        val durability: Rating,
        val range: Rating
) {

    val orderedStats = arrayOf(potential, power, speed, precision, durability, range)

    companion object {

        val NONE = StandStats(
                Rating.NONE,
                Rating.NONE,
                Rating.NONE,
                Rating.NONE,
                Rating.NONE,
                Rating.NONE
        )
    }
}
