package com.antonpopoff.standcharacteristicsview.diagram

import android.os.Parcel
import android.os.Parcelable

data class StandRating(
        val potential: Rating,
        val power: Rating,
        val speed: Rating,
        val precision: Rating,
        val durability: Rating,
        val range: Rating
) : Parcelable {

    val ratings = listOf(potential, power, speed, range, durability, precision)

    constructor(parcel: Parcel) : this(
            parcel.readSerializable() as Rating,
            parcel.readSerializable() as Rating,
            parcel.readSerializable() as Rating,
            parcel.readSerializable() as Rating,
            parcel.readSerializable() as Rating,
            parcel.readSerializable() as Rating
    )

    companion object {

        val UNKNOWN = StandRating(
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN,
                Rating.UNKNOWN
        )

        @JvmField
        val CREATOR = object : Parcelable.Creator<StandRating> {

            override fun createFromParcel(parcel: Parcel) = StandRating(parcel)

            override fun newArray(size: Int) = arrayOfNulls<StandRating>(size)
        }
    }

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
}
