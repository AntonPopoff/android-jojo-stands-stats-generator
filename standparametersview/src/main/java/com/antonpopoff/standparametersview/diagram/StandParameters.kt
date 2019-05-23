package com.antonpopoff.standparametersview.diagram

import android.os.Parcel
import android.os.Parcelable

class StandParameters(
        potentialRating: ParameterRating,
        powerRating: ParameterRating,
        speedRating: ParameterRating,
        precisionRating: ParameterRating,
        durabilityRating: ParameterRating,
        rangeRating: ParameterRating
) : Parcelable {

    val potential = StandParameter(ParameterName.POTENTIAL, potentialRating)

    val power = StandParameter(ParameterName.POWER, powerRating)

    val speed = StandParameter(ParameterName.SPEED, speedRating)

    val precision= StandParameter(ParameterName.PRECISION, precisionRating)

    val durability = StandParameter(ParameterName.DURABILITY, durabilityRating)

    val range = StandParameter(ParameterName.RANGE, rangeRating)

    val parameters = listOf(potential, power, speed, range, durability, precision)

    constructor(parcel: Parcel) : this(
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating
    )

    companion object {

        val UNKNOWN = StandParameters(
                ParameterRating.UNKNOWN,
                ParameterRating.UNKNOWN,
                ParameterRating.UNKNOWN,
                ParameterRating.UNKNOWN,
                ParameterRating.UNKNOWN,
                ParameterRating.UNKNOWN
        )

        @JvmField
        val CREATOR = object : Parcelable.Creator<StandParameters> {

            override fun createFromParcel(parcel: Parcel) = StandParameters(parcel)

            override fun newArray(size: Int) = arrayOfNulls<StandParameters>(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeSerializable(potential.rating)
            writeSerializable(power.rating)
            writeSerializable(speed.rating)
            writeSerializable(precision.rating)
            writeSerializable(durability.rating)
            writeSerializable(range.rating)
        }
    }

    override fun describeContents() = 0
}
