package com.mrm.android.flikrtest.ui.userphotos

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.api.FlikrApi
import com.mrm.android.flikrtest.api.parseUserPhotos
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.oauth.OAuthConstants
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.net.URLDecoder

class UserPhotosViewModel(application: Application) : AndroidViewModel(application){
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _userphotoList = MutableLiveData<List<UserPhoto>>()
    val userphotoList: LiveData<List<UserPhoto>>
        get()= _userphotoList

    init{
        getPhotos()
    }


    private fun getPhotos(){
        viewModelScope.launch{
            try{
                val response: Response<String> = FlikrApi.retrofitService.getUserImages(OAuthConstants.API_KEY,
                    URLDecoder.decode(CurrentUser.userID))
                val plister = parseUserPhotos(JSONObject(response.body()!!))
                _userphotoList.value = plister
                Log.i("UserPhotos","${userphotoList.value!!.size}")
            }catch(e: Exception){
                Log.i("UserPhotos","Exception: $e")
            }
        }

    }
}