package com.antonpopoff.standparametersview.diagram

import android.os.Parcel
import android.os.Parcelable

class StandParameters(
        val potential: ParameterRating,
        val power: ParameterRating,
        val speed: ParameterRating,
        val range: ParameterRating,
        val durability: ParameterRating,
        val precision: ParameterRating
) : Parcelable {

    val ratings = listOf(potential, power, speed, range, durability, precision)

    constructor(parcel: Parcel) : this(
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating,
            parcel.readSerializable() as ParameterRating
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeSerializable(potential)
            writeSerializable(power)
            writeSerializable(speed)
            writeSerializable(precision)
            writeSerializable(durability)
            writeSerializable(range)
        }
    }

    override fun describeContents() = 0

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

        fun from(list: List<ParameterRating>) = StandParameters(
                list.getOrNull(0) ?: ParameterRating.UNKNOWN,
                list.getOrNull(1) ?: ParameterRating.UNKNOWN,
                list.getOrNull(2) ?: ParameterRating.UNKNOWN,
                list.getOrNull(3) ?: ParameterRating.UNKNOWN,
                list.getOrNull(4) ?: ParameterRating.UNKNOWN,
                list.getOrNull(5) ?: ParameterRating.UNKNOWN
        )
    }
}
