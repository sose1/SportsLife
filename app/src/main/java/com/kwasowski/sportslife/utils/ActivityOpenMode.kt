package com.kwasowski.sportslife.utils

import android.os.Parcel
import android.os.Parcelable

enum class ActivityOpenMode : Parcelable {
    DEFAULT,
    ADD_EXERCISE_TO_TRAINING_PLAN,
    ADD_TRAINING_PLAN_TO_CALENDAR_DAY;

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityOpenMode> {
        override fun createFromParcel(parcel: Parcel): ActivityOpenMode {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<ActivityOpenMode?> {
            return arrayOfNulls(size)
        }
    }
}
