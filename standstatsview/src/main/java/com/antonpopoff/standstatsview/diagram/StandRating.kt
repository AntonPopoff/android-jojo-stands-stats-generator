package com.antonpopoff.standstatsview.diagram

data class StandRating(
        val potential: Rating,
        val power: Rating,
        val speed: Rating,
        val precision: Rating,
        val durability: Rating,
        val range: Rating
) {

    val ratings = listOf(potential, power, speed, precision, durability, range)

    companion object {

        val UNKNOWN = StandRating(
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN
        )
    }
}
