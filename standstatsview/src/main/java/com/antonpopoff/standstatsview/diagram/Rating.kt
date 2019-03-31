package com.antonpopoff.standstatsview.diagram

enum class Rating(val letter: String) {
    INFINITE("∞"),
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    UNKNOWN("?"),
    NONE("");

    companion object {

        val letterRatings = listOf(A, B, C, D, E)
    }
}
