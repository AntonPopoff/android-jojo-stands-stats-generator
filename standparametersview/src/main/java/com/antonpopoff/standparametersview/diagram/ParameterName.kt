package com.antonpopoff.standparametersview.diagram

enum class ParameterName {
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