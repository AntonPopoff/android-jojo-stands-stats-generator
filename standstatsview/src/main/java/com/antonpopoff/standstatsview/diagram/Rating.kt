package com.antonpopoff.standstatsview.diagram

enum class Rating(val letter: String, val mark: Int) {
    INFINITE("∞", 6),
    A("A", 5),
    B("B", 4),
    C("C", 3),
    D("D", 2),
    E("E", 1),
    UNKNOWN("?", 0),
    NONE("", 0);

    companion object {

        val letterRatings = listOf(A, B, C, D, E)
    }
}
