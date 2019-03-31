package com.antonpopoff.standstatsview.diagram

data class StandRating(
        val potential: Rating,
        val power: Rating,
        val speed: Rating,
        val precision: Rating,
        val durability: Rating,
        val range: Rating
) {

    private val values = listOf(potential, power, speed, precision, durability, range)

    fun get(index: Int) = values[index]

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
