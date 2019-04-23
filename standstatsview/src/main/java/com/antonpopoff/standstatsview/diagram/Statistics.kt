package com.antonpopoff.standstatsview.diagram

enum class Statistics {
    POTENTIAL,
    POWER,
    SPEED,
    RANGE,
    DURABILITY,
    PRECISION;

    companion object {

        private val values = values().toList()

        val count = values.size

        fun get(index: Int) = values[index]
    }
}