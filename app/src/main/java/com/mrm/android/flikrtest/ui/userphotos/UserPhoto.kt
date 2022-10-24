package com.mrm.android.flikrtest.ui.userphotos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserPhoto (val title: String,
                      val photoUrl: String
): Parcelable