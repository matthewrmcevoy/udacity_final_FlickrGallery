package com.mrm.android.flikrtest.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.mrm.android.flikrtest.oauth.CurrentUser
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class APIPhoto (val title: String,
                     @PrimaryKey
                    val media: String,
                    val dateTaken: String, val author: String,
                    val tags: List<String>,
                    val user: String):Parcelable