package com.antonpopoff.standparametersview.diagram

import android.os.Parcel
import android.os.Parcelable

data class StandParameter(val name: ParameterName, val rating: ParameterRating) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readSerializable() as ParameterName,
            parcel.readSerializable() as ParameterRating
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeSerializable(name)
            writeSerializable(rating)
        }
    }

    override fun describeContents() = 0

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<StandParameter> {

            override fun createFromParcel(parcel: Parcel) = StandParameter(parcel)

            override fun newArray(size: Int) = arrayOfNulls<StandParameter>(size)
        }
    }
}
