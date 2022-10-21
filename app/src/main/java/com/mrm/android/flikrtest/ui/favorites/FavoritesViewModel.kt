package com.mrm.android.flikrtest.ui.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.oauth.CurrentUser
import kotlinx.coroutines.launch
enum class FavoritesLoadStatus{LOADING, DONE, EMPTY}
class FavoritesViewModel : ViewModel() {
    val favoritesStatus= MutableLiveData<FavoritesLoadStatus>()

    lateinit var favPhotos: List<APIPhoto>
    private val database = getDatabase(Application())

    private val _favorites = MutableLiveData<List<APIPhoto>>()
    val favorites: LiveData<List<APIPhoto>>
    get()=_favorites

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite


    init{
        favoritesStatus.value=FavoritesLoadStatus.LOADING
        viewModelScope.launch {
            favPhotos = database.favoritePhotoDao.getFavorites(CurrentUser.userName)
            _favorites.value = favPhotos
            if(favPhotos.isEmpty()){
                favoritesStatus.value = FavoritesLoadStatus.EMPTY
            }else{
                favoritesStatus.value=FavoritesLoadStatus.DONE
            }
        }

    }

    fun updateFavorites(){
        viewModelScope.launch{
            favPhotos = database.favoritePhotoDao.getFavorites(CurrentUser.userName)
            _favorites.value = favPhotos
            if(favPhotos.isEmpty()){
                favoritesStatus.value = FavoritesLoadStatus.EMPTY
            }else{
                favoritesStatus.value=FavoritesLoadStatus.DONE
            }
        }
    }

    fun isFavorite(apiPhoto: APIPhoto): Boolean{
        viewModelScope.launch{
            favPhotos = database.favoritePhotoDao.getFavorites(CurrentUser.userName)
        }
        _isFavorite.value = favPhotos.contains(apiPhoto)
        Log.i("mvm", "${_isFavorite.value}")
        return _isFavorite.value == true
    }
}