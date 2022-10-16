package com.mrm.android.flikrtest.searchhist

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize



@Entity
data class SearchTerm(
    @PrimaryKey
    val term: String)