package com.antonpopoff.standstatsview.diagram

enum class Statistics {
    POTENTIAL,
    POWER,
    SPEED,
    RANGE,
    DURABILITY,
    PRECISION;

    companion object {

        private val values = Statistics.values().toList()

        val count = Statistics.values().size

        fun get(index: Int) = values[index]
    }
}