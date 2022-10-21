package com.mrm.android.flikrtest.dB

import android.content.Context
import androidx.room.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.oauth.AuthUser
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.searchhist.SearchTerm

@Dao
interface FavoritePhotoDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoritePhoto(photo: APIPhoto)

    @Query("select * from APIPhoto where user is :username")
    suspend fun getFavorites(username: String): List<APIPhoto>

    @Query("delete from APIPhoto where media is :media")
    suspend fun deleteFavorite(media: String)

    @Query("delete from APIPhoto where user is :username")
    suspend fun clearFavorites(username: String)
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

    @Query("select * from SearchTerm where user is :username")
    suspend fun getSearchHistory(username: String): List<SearchTerm>

    @Query("delete from SearchTerm where term is :term")
    suspend fun deleteSearchTerm(term: String)

    @Query("delete from SearchTerm where user is :username")
    suspend fun clearSearchHistory(username: String)

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

@Dao
interface UserDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: AuthUser)

    @Query("select * from AuthUser where userID is :user_nsid")
    suspend fun getUserInfo(user_nsid : String) : AuthUser

    @Query("select * from AuthUser order by last_used_ts desc ")
    suspend fun getLastUser() : AuthUser

    @Query("SELECT (SELECT COUNT(*) FROM AuthUser) == 0")
    fun isEmpty(): Boolean
}

@Database(entities = [AuthUser::class], version = 1)
abstract class UserDatabase: RoomDatabase(){
    abstract val userDao: UserDao
}
private lateinit var USER_INSTANCE: UserDatabase

fun getUserDB(context: Context): UserDatabase{
    if(!::USER_INSTANCE.isInitialized){
        USER_INSTANCE = Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "Users"
        ).build()
    }
    return USER_INSTANCE
}