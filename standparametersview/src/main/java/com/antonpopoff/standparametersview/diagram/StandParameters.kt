package com.antonpopoff.standparametersview.diagram

import android.os.Parcel
import android.os.Parcelable

data class StandParameters(
        val potential: ParameterRating,
        val power: ParameterRating,
        val speed: ParameterRating,
        val precision: ParameterRating,
        val durability: ParameterRating,
        val range: ParameterRating
) : Parcelable {

    val ratings = listOf(potential, power, speed, range, durability, precision)

    val parameters = listOf(
            StandParameter(ParameterName.POTENTIAL, potential),
            StandParameter(ParameterName.POWER, power),
            StandParameter(ParameterName.SPEED, speed),
            StandParameter(ParameterName.RANGE, range),
            StandParameter(ParameterName.DURABILITY, durability),
            StandParameter(ParameterName.PRECISION, precision)
    )

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
            writeSerializable(potential)
            writeSerializable(power)
            writeSerializable(speed)
            writeSerializable(precision)
            writeSerializable(durability)
            writeSerializable(range)
        }
    }

    override fun describeContents() = 0

    class Builder {

        private val ratings = mutableMapOf(
                ParameterName.DURABILITY to ParameterRating.UNKNOWN,
                ParameterName.POTENTIAL to ParameterRating.UNKNOWN,
                ParameterName.POWER to ParameterRating.UNKNOWN,
                ParameterName.PRECISION to ParameterRating.UNKNOWN,
                ParameterName.RANGE to ParameterRating.UNKNOWN,
                ParameterName.SPEED to ParameterRating.UNKNOWN
        )

        fun setRatings(list: List<StandParameter>): Builder {
            list.forEach { ratings[it.name] = it.rating }
            return this
        }

        fun create() = StandParameters(
                ratings.getOrElse(ParameterName.POTENTIAL, { ParameterRating.UNKNOWN }),
                ratings.getOrElse(ParameterName.POWER, { ParameterRating.UNKNOWN }),
                ratings.getOrElse(ParameterName.SPEED, { ParameterRating.UNKNOWN }),
                ratings.getOrElse(ParameterName.PRECISION, { ParameterRating.UNKNOWN }),
                ratings.getOrElse(ParameterName.DURABILITY, { ParameterRating.UNKNOWN }),
                ratings.getOrElse(ParameterName.RANGE, { ParameterRating.UNKNOWN })
        )
    }
}
