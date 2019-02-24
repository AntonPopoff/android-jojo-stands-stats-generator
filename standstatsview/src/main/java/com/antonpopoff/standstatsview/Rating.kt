package com.antonpopoff.standstatsview

enum class Rating(val letter: String, val number: Int) {
    A("A", 5),
    B("B", 4),
    C("C", 3),
    D("D", 2),
    E("E", 1),
    NONE("-", 0)
}