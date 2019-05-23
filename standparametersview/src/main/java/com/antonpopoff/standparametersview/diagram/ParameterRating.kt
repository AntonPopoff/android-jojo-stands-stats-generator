package com.antonpopoff.standparametersview.diagram

enum class ParameterRating(val char: String, val mark: Int) {
    NONE("_", 0),
    UNKNOWN("?", 0),
    E("E", 1),
    D("D", 2),
    C("C", 3),
    B("B", 4),
    A("A", 5),
    INFINITE("∞", 6);

    companion object {

        val ratings = values().toList()

        val ratingsCount = ratings.size

        val letterRatings = listOf(A, B, C, D, E)
    }
}
