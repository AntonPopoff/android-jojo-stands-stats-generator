package com.antonpopoff.standparametersview.diagram

enum class ParameterName {
    POTENTIAL,
    POWER,
    SPEED,
    RANGE,
    DURABILITY,
    PRECISION;

    companion object {

        val values by lazy { values().toList() }

        val count by lazy { values.size }

        fun get(index: Int) = values[index]
    }
}