package com.mrm.android.flikrtest.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.api.FlikrApi
import com.mrm.android.flikrtest.api.parseAPIPhotosJsonResult
import com.mrm.android.flikrtest.dB.getDatabase
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {
//    private val _photoList = MutableLiveData<List<PhotoImage>>()
//    val photoList: LiveData<List<PhotoImage>>
//        get()=_photoList



    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

   private val _photoList = MutableLiveData<List<APIPhoto>>()
    val photoList: LiveData<List<APIPhoto>>
    get()= _photoList

    private val _mTest = MutableLiveData<String>()
    val mTest: LiveData<String>
    get()= _mTest

    private var filter = ""

    val recentSearch:MutableList<String> = mutableListOf()
    val favoritePhotos: MutableList<APIPhoto> = mutableListOf()
    private val database = getDatabase(application)


    init {
        getPhotos(filter)
        _mTest.value = "https://live.staticflickr.com/65535/52363257257_9e1239c5f6_m.jpg"

    }

    fun addSearch(searchString: String){
        if(recentSearch.contains(searchString) || searchString =="" ){
            null
        }else{
            if(recentSearch.size <= 4 ){
                recentSearch.add(0,searchString)
            }else{
                recentSearch.add(0, searchString)
                recentSearch.removeAt(5)
            }
    }
    }

    fun addFavorite(apiPhoto: APIPhoto){
        favoritePhotos.add(apiPhoto)
        Log.i("mainViewM","$favoritePhotos")
    }


    private fun getPhotos(filter : String) {

//        FlikrApi.retrofitService.getPhotos().enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                _response.value = "Failure: " + t.message
//            }
//
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//
//                _response.value = "Success: ${response.body()} API Photos returned"
//                //Log.i("ViewModel","Retrofit response is ${_response.value}")
//            }
//        })
//    }}
        viewModelScope.launch {
            try{
                val response: Response<String> = FlikrApi.retrofitService.getPhotos(filter)
                //Log.i("ViewModel", "${response.body()}")
                val pList = parseAPIPhotosJsonResult(JSONObject(response.body()!!))
                Log.i("ViewModel","Trying to parse. Result: ${pList.size}")
                Log.i("ViewModel","${pList[1].media}")
                _photoList.value = pList
                _mTest.value = photoList.value!![1].media
                Log.i("ViewModel","photoList ${photoList.value!![1].media}")
            }catch(e: Exception){
                Log.i("ViewModel","${e.message}")
            }

        }
    }
    fun updatePhotoFilter(filter: String){
        getPhotos(filter)
    }
    }
