package com.mrm.android.flikrtest.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.api.FlikrApi
import com.mrm.android.flikrtest.api.parseAPIPhotosJsonResult
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.dB.getSearchHistoryDB
import com.mrm.android.flikrtest.searchhist.SearchTerm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
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

    private lateinit var dbFavoritePhotos : List<APIPhoto>


    init {

        status.value = APIStatus.LOADING
        viewModelScope.launch {
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites()
            val searchList = searchHistDatabase.searchHistoryDao.getSearchHistory()
            for(i in searchList){
                if(!recentSearch.contains(i.term)){
                    recentSearch.add(i.term)
                }
            }
        }
        getPhotos(filter)

        Log.i("mvm","init was run")
        _mTest.value = "https://live.staticflickr.com/65535/52363257257_9e1239c5f6_m.jpg"

    }

    fun addSearch(searchString: String){
        if(recentSearch.contains(searchString) || searchString =="" ){
            null
        }else{
            val newSearchTerm= SearchTerm(searchString)
            CoroutineScope(Dispatchers.IO).launch{
                searchHistDatabase.searchHistoryDao.addSearchTerm(newSearchTerm)
                val searchList = searchHistDatabase.searchHistoryDao.getSearchHistory()
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
            searchHistDatabase.searchHistoryDao.clearSearchHistory()
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
            dbFavoritePhotos = database.favoritePhotoDao.getFavorites()
        }
        _isFavorite.value = dbFavoritePhotos.contains(apiPhoto)
        Log.i("mvm", "${_isFavorite.value}")
        return _isFavorite.value == true
    }
}
