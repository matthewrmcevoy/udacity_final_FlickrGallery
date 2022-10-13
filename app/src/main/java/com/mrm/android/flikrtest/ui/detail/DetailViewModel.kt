package com.mrm.android.flikrtest.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.FavoritePhotosDatabase
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.ui.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(apiPhoto: APIPhoto, application: Application): AndroidViewModel(application) {
    private val _selectedPhoto = MutableLiveData<APIPhoto>()
    val selectedPhoto: LiveData<APIPhoto>
    get() = _selectedPhoto

    private val database = getDatabase(application)
    var favoritePhotos = mutableListOf<APIPhoto>()

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
    get() = _isFavorite

    private lateinit var dbFavoritePhotos : List<APIPhoto>

    init{
        _selectedPhoto.value = apiPhoto
        viewModelScope.launch {
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites()
            _isFavorite.value = dbFavoritePhotos.contains(apiPhoto)
        }

    }

    fun addFavorite(apiPhoto: APIPhoto){
        favoritePhotos.add(apiPhoto)
        //viewModelScope.launch{
        CoroutineScope(Dispatchers.IO).launch{
            database.favoritePhotoDao.addFavoritePhoto(apiPhoto)
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites()
        }
            _isFavorite.value = true

        Log.i("DVM","List of saved favorites ${dbFavoritePhotos.reversed()}")
    }

    fun deleteFavorite(apiPhoto: APIPhoto){
        viewModelScope.launch {
            database.favoritePhotoDao.deleteFavorite(apiPhoto.media)
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites()
            _isFavorite.value = dbFavoritePhotos.contains(apiPhoto)
        }
    }

    fun isFavorite(apiPhoto: APIPhoto): Boolean{
        viewModelScope.launch{
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites()
            _isFavorite.value = dbFavoritePhotos.contains(apiPhoto)
        }
        return _isFavorite.value == true
    }
}