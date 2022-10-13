package com.mrm.android.flikrtest.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrm.android.flikrtest.dB.FavoritePhotosDatabase
import com.mrm.android.flikrtest.dB.getDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class FavSettingsStatus{LOADING, DONE}
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    val favSettingStat= MutableLiveData<FavSettingsStatus>()

    init{
        favSettingStat.value = FavSettingsStatus.DONE
    }

    fun clearFavorites(){
        favSettingStat.value = FavSettingsStatus.LOADING
        CoroutineScope(Dispatchers.IO).launch{
            database.favoritePhotoDao.clearFavorites()

        }
        favSettingStat.value = FavSettingsStatus.DONE

    }
}