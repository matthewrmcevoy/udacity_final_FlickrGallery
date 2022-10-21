package com.mrm.android.flikrtest.oauth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AuthUser(
    @PrimaryKey
    var userID : String,
    var fullName : String,
    var oa_token : String,
    var oa_token_secret : String,
    var userName : String,
    var last_used_ts : Int
)