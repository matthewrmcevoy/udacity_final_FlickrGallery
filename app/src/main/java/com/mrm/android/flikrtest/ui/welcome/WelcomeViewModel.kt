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
    //get an instance of the db of users who have authenticated with the app locally
    private val userDb = getUserDB(application)

    //get an instance of the CurrentUser object and transform it into an AuthUser
    private var lastUser:AuthUser = AuthUser(CurrentUser.userID,CurrentUser.fullName,CurrentUser.oa_token,CurrentUser.oa_token_secret,CurrentUser.userName, System.currentTimeMillis().toInt()/1000)

    //return the most recent user used for quick re-access
    private val _recentUser=MutableLiveData<AuthUser>()
    val recentUser: LiveData<AuthUser>
    get()=_recentUser

    /* Consumable property used during authentication process.
    * After returning from the authentication activity this tells the fragment
    * to automatically move to the MainFragment */
    private val _skipToMain = MutableLiveData<Int>()
    val skipToMain: LiveData<Int>
    get()=_skipToMain

    private val _oauthToken = MutableLiveData<String>()
    val oauthToken: LiveData<String>
    get()=_oauthToken

    val queue = Volley.newRequestQueue(application)


    init{
        /*Initialize the most recent user as the lastUser - which is
        * the guest account on the first runs (and any subsequent runs prior to authorizing
        * an actual user */
        _recentUser.value = lastUser
        /*if that user is an authenticated user set our consumable flag
        * to immediately skip this welcome fragment when going through authorization cycle*/
        if(CurrentUser.userID != "none"){
            _skipToMain.value = 1
        }
        /*Access the last used user account that isn't the guest account if any have
        * been authenticated, otherwise maintain the recent user as the guest accnt */
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

    /*remove the skip flag -- used in logout scenario to ensure
    * the login screen isn't immediately skipped prior to selecting
    * a different user */
    fun resetSkipToMain(){
        _skipToMain.value = 0
    }

    /* After selecting a user account to use update the current user object
    * for its usage around the application*/
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

    fun retrieveOAuthToken(){
        var oauthToken: String = ""
        val apiKey = "64187751b962051b4eb86e0c23bd7874"
        val secret = "d3cc409f4d513aea&"
        val redirectUri = "flickrtest%3A%2F%2Fcallback"
        val callbackN = "flickrtest://callback"
        val nonce = Random.nextInt(0, 99999999)
        val ts = System.currentTimeMillis()/1000
        val baseUrl ="https://www.flickr.com/services/oauth/request_token"
        val baseText = "GET&"+baseUrl + "&oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
        val sortParam = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
        val testSortParams = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
        val testSigGen = "GET&"+ URLEncoder.encode(baseUrl)+"&"+ URLEncoder.encode(testSortParams)
        val testSigGenOauth = calcHmac(testSigGen,secret)
        val testSignGenOauthEnc = URLEncoder.encode(testSigGenOauth)

        val url=baseUrl + "?oauth_nonce=" + nonce + "&oauth_timestamp=" + ts +"&oauth_consumer_key=" + apiKey +"&oauth_signature_method=HMAC-SHA1" + "&oauth_version=1.0" + "&oauth_signature=" + testSignGenOauthEnc + "&oauth_callback=" + callbackN

        val stringrq = StringRequest(Request.Method.GET,url, { response ->
            Log.i("innerTest", response)
            val respBody = response.split("&")
            val rtrnOauthToken = respBody[1]
            val rtrnOauthTokenSecret = respBody[2]
            val token_secrett = rtrnOauthTokenSecret.split("=")
            val token_secret = token_secrett[1]
            CurrentUser.oa_token_secret = token_secret
            Log.i("innerTest", rtrnOauthToken)
            oauthToken = rtrnOauthToken
            _oauthToken.value = rtrnOauthToken
        }, {
            Log.i("innterTest","WHOOPS!")
        })
        queue.add(stringrq)

    }

}
