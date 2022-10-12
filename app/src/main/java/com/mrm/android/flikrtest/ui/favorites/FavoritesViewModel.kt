package com.mrm.android.flikrtest.ui.favorites

import android.app.Application
import androidx.lifecycle.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.getDatabase
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    lateinit var favPhotos: List<APIPhoto>
    private val database = getDatabase(Application())

    private val _favorites = MutableLiveData<List<APIPhoto>>()
    val favorites: LiveData<List<APIPhoto>>
    get()=_favorites

    init{
        viewModelScope.launch {
            favPhotos = database.favoritePhotoDao.getFavorites()
            _favorites.value = favPhotos
        }

    }

    fun updateFavorites(){
        viewModelScope.launch{
            favPhotos = database.favoritePhotoDao.getFavorites()
            _favorites.value = favPhotos
        }
    }
}