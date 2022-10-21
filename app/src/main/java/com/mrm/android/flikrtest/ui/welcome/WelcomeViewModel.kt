package com.mrm.android.flikrtest.ui.welcome

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mrm.android.flikrtest.dB.getUserDB
import com.mrm.android.flikrtest.oauth.AuthUser
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.oauth.calcHmac
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URLEncoder
import kotlin.random.Random

class WelcomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userDb = getUserDB(application)
    private var lastUser:AuthUser = AuthUser(CurrentUser.userID,CurrentUser.fullName,CurrentUser.oa_token,CurrentUser.oa_token_secret,CurrentUser.userName, System.currentTimeMillis().toInt()/1000)

    private val _recentUser=MutableLiveData<AuthUser>()
    val recentUser: LiveData<AuthUser>
    get()=_recentUser

    private val _skipToMain = MutableLiveData<Int>()
    val skipToMain: LiveData<Int>
    get()=_skipToMain

    init{
        _recentUser.value = lastUser
        if(CurrentUser.userID != "none"){
            _skipToMain.value = 1
        }
        CoroutineScope(Dispatchers.IO).launch {
            if(!userDb.userDao.isEmpty()){
                    lastUser = userDb.userDao.getLastUser()
                    _recentUser.postValue(lastUser)
                    Log.i("WelcomeVM","lastUser = ${lastUser.userName}")
            }else{
                _recentUser.postValue(lastUser)
            }
        }
        Log.i("WVM","skipToMain = ${skipToMain.value}")
    }
    fun resetSkipToMain(){
        _skipToMain.value = 0
    }

    fun setCurrentUser(user: String){
        if(user == "guest"){
            CurrentUser.apply{
                userName = "guest"
                userID = "none"
                fullName = "null"
                oa_token = "null"
                oa_token_secret = "null"
            }
        }else{
            CurrentUser.apply{
                userName = recentUser.value!!.userName
                userID = recentUser.value!!.userID
                fullName = recentUser.value!!.fullName
                oa_token = recentUser.value!!.oa_token
                oa_token_secret = recentUser.value!!.oa_token_secret
            }
        }
    }
}
