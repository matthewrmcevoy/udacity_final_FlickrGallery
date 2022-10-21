package com.mrm.android.flikrtest.ui.main

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.*
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.api.FlikrApi
import com.mrm.android.flikrtest.api.parseAPIPhotosJsonResult
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.dB.getSearchHistoryDB
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.searchhist.SearchTerm
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.net.URLDecoder

enum class APIStatus{LOADING, DONE, ERROR, EMPTY}

class MainViewModel(application: Application) : AndroidViewModel(application) {
//    private val _photoList = MutableLiveData<List<PhotoImage>>()
//    val photoList: LiveData<List<PhotoImage>>
//        get()=_photoList

    val status = MutableLiveData<APIStatus>()

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
    private val searchHistDatabase = getSearchHistoryDB(application)

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
    get()=_imageUrl

    private lateinit var dbFavoritePhotos : List<APIPhoto>

    var ic_server = ""
    var ic_farm = ""


    init {

        status.value = APIStatus.LOADING
        viewModelScope.launch {
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites(CurrentUser.userName)
            val searchList = searchHistDatabase.searchHistoryDao.getSearchHistory(CurrentUser.userName)
            for(i in searchList){
                if(!recentSearch.contains(i.term)){
                    recentSearch.add(i.term)
                }
            }
        }
        getPhotos(filter)
        getProfileDetails()
        Log.i("mvm","init was run")
        _mTest.value = "https://live.staticflickr.com/65535/52363257257_9e1239c5f6_m.jpg"

    }

    fun addSearch(searchString: String){
        if(recentSearch.contains(searchString) || searchString =="" ){
            null
        }else{
            val newSearchTerm= SearchTerm(searchString, CurrentUser.userName)
            CoroutineScope(Dispatchers.IO).launch{
                searchHistDatabase.searchHistoryDao.addSearchTerm(newSearchTerm)
                val searchList = searchHistDatabase.searchHistoryDao.getSearchHistory(CurrentUser.userName)
                for(i in searchList){
                    if(recentSearch.contains(i.term)){
                        null
                    }else{
                        recentSearch.add(0, searchString)
                    }
                }
            }
        }
    }
    fun wipeSearch(){
        viewModelScope.launch{
            searchHistDatabase.searchHistoryDao.clearSearchHistory(CurrentUser.userName)
        }
    }

    fun addFavorite(apiPhoto: APIPhoto){
        favoritePhotos.add(apiPhoto)
        Log.i("mainViewM","$favoritePhotos")
    }


    private fun getPhotos(filter : String) {

        viewModelScope.launch {
            try{
                status.value = APIStatus.LOADING
                val response: Response<String> = FlikrApi.retrofitService.getPhotos(filter)
                //Log.i("ViewModel", "${response.body()}")
                val pList = parseAPIPhotosJsonResult(JSONObject(response.body()!!))
                Log.i("ViewModel","Trying to parse. Result: ${pList.size}")
                Log.i("ViewModel","${pList[1].media}")
                _photoList.value = pList
                _mTest.value = photoList.value!![1].media
                Log.i("ViewModel","photoList ${photoList.value!![1].media}")
                if(pList.size == 0){
                    status.value = APIStatus.EMPTY
                }else{
                    status.value = APIStatus.DONE
                }

            }catch(e: Exception){
                status.value = APIStatus.ERROR
                Log.i("ViewModel","${e.message}")
            }

        }
    }
    fun updatePhotoFilter(filter: String){
        getPhotos(filter)
    }
    fun isFavorite(apiPhoto: APIPhoto): Boolean{
        viewModelScope.launch{
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites(CurrentUser.userName)
        }
        _isFavorite.value = dbFavoritePhotos.contains(apiPhoto)
        Log.i("mvm", "${_isFavorite.value}")
        return _isFavorite.value == true
    }

    fun getProfileDetails(){
        viewModelScope.launch {
            try{
                val nsidD = URLDecoder.decode(CurrentUser.userID)
                val response = FlikrApi.retrofitService.getProfileImageDetails("64187751b962051b4eb86e0c23bd7874",nsidD)
                val res2 = JSONObject(response.body()!!)
                val ic = res2.getJSONObject("person")
                ic_server = ic.getString("iconserver")
                ic_farm = ic.getString("iconfarm")

                loadProfileImage()
            }catch(e: Exception){
                Log.i("MVM", "Exception returned :" + e)
            }
        }
    }

    fun loadProfileImage(){
        val userNsid = URLDecoder.decode(CurrentUser.userID)
        _imageUrl.value ="https://farm${ic_farm}.staticflickr.com/${ic_server}/buddyicons/${userNsid}.jpg"
        Log.i("MVM","${imageUrl.value}")
    }
}
