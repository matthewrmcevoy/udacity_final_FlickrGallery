package com.mrm.android.flikrtest.dB

import android.content.Context
import androidx.room.*
import com.mrm.android.flikrtest.api.APIPhoto

@Dao
interface FavoritePhotoDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoritePhoto(photo: APIPhoto)

    @Query("select * from APIPhoto")
    suspend fun getFavorites(): List<APIPhoto>

    @Query("delete from APIPhoto where media is :media")
    suspend fun deleteFavorite(media: String)

    @Query("delete from APIPhoto")
    suspend fun clearFavorites()
}

@Database(entities = [APIPhoto::class], version = 1)
@TypeConverters(TagsTypeConverter::class)
abstract class FavoritePhotosDatabase: RoomDatabase() {
    abstract val favoritePhotoDao: FavoritePhotoDao
}

private lateinit var INSTANCE: FavoritePhotosDatabase

fun getDatabase(context: Context): FavoritePhotosDatabase{
    if (!::INSTANCE.isInitialized){
        INSTANCE = Room.databaseBuilder(
            context.applicationContext,
            FavoritePhotosDatabase::class.java,
            "favorites"
        ).build()
    }
    return INSTANCE
}