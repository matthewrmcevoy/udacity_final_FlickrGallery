package com.mrm.android.flikrtest.ui.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mrm.android.flikrtest.dB.FavoritePhotosDatabase
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.dB.getSearchHistoryDB
import com.mrm.android.flikrtest.oauth.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class FavSettingsStatus{LOADING, DONE}
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val searchHistoryDatabase = getSearchHistoryDB(application)

    private val _favCount = MutableLiveData<Int>()
    val favCount: LiveData<Int>
    get() = _favCount

    private val _searchCount = MutableLiveData<Int>()
    val searchCount: LiveData<Int>
    get() = _searchCount

    val favSettingStat= MutableLiveData<FavSettingsStatus>()

    init{
        favSettingStat.value = FavSettingsStatus.DONE
        viewModelScope.launch{
            _favCount.value = database.favoritePhotoDao.getFavorites(CurrentUser.userName).size
            Log.i("svm","${_favCount.value}")
            _searchCount.value = searchHistoryDatabase.searchHistoryDao.getSearchHistory(CurrentUser.userName).size
        }
        Log.i("svm","init")

    }

    fun clearFavorites(){
        favSettingStat.value = FavSettingsStatus.LOADING
        CoroutineScope(Dispatchers.IO).launch{
            database.favoritePhotoDao.clearFavorites(CurrentUser.userName)
            _favCount.postValue(database.favoritePhotoDao.getFavorites(CurrentUser.userName).size)
        }


        favSettingStat.value = FavSettingsStatus.DONE
    }

    fun clearSearchHistory(){
        CoroutineScope(Dispatchers.IO).launch{
            searchHistoryDatabase.searchHistoryDao.clearSearchHistory(CurrentUser.userName)
            _searchCount.postValue(searchHistoryDatabase.searchHistoryDao.getSearchHistory(CurrentUser.userName).size)
        }

    }
}