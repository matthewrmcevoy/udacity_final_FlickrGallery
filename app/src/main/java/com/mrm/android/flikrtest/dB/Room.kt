package com.mrm.android.flikrtest.dB

import android.content.Context
import androidx.room.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.searchhist.SearchTerm

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

@Dao
interface SearchHistoryDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSearchTerm(searchTerm: SearchTerm)

    @Query("select * from SearchTerm")
    suspend fun getSearchHistory(): List<SearchTerm>

    @Query("delete from SearchTerm where term is :term")
    suspend fun deleteSearchTerm(term: String)

    @Query("delete from SearchTerm")
    suspend fun clearSearchHistory()

}

@Database(entities = [SearchTerm::class], version = 1)
abstract class SearchHistoryDatabase: RoomDatabase() {
    abstract val searchHistoryDao: SearchHistoryDao
}

private lateinit var SH_INSTANCE: SearchHistoryDatabase

fun getSearchHistoryDB(context: Context): SearchHistoryDatabase{
    if(!::SH_INSTANCE.isInitialized){
        SH_INSTANCE = Room.databaseBuilder(
            context.applicationContext,
            SearchHistoryDatabase::class.java,
            "searchHistory"
        ).build()
    }
    return SH_INSTANCE
}