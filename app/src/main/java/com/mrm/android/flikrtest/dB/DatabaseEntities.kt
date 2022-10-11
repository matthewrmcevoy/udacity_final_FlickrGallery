package com.mrm.android.flikrtest.dB

import androidx.lifecycle.Transformations.map
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrm.android.flikrtest.api.APIPhoto

@Entity
data class DataBaseAPIPhoto constructor (
    val title: String,
    @PrimaryKey
    val media: String,
    val dateTaken: String,
    val author: String,
    val tags: List<String>
)

class TagsTypeConverter {
    @TypeConverter
    fun fromString(value: String?): List<String>{
        val listType = object : TypeToken<List<String>>(){}.type

        return Gson().fromJson(value, listType )
    }
    @TypeConverter
    fun fromList(list: List<String>): String {
       return Gson().toJson(list)
    }
}
