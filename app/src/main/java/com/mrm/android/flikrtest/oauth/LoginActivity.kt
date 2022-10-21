package com.mrm.android.flikrtest.oauth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mrm.android.flikrtest.MainActivity
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.dB.getUserDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private val baseAuthUrl = "https://www.flickr.com/services/oauth/access_token"
    private val oa_nonce = Random.nextInt(0, 99999999)
    private val oa_ts = System.currentTimeMillis()/1000
    private val apiKey = "64187751b962051b4eb86e0c23bd7874"
    private val secret = "d3cc409f4d513aea&"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val mainIntent = intent
        val authToken = mainIntent.data!!.getQueryParameter("oauth_token")
        val oa_verifier = mainIntent.data!!.getQueryParameter("oauth_verifier")
        val authParams = "oauth_consumer_key="+apiKey+"&oauth_nonce="+oa_nonce+"&oauth_signature_method=HMAC-SHA1"+"&oauth_timestamp="+oa_ts+"&oauth_token="+authToken+"&oauth_verifier="+oa_verifier+"&oauth_version=1.0"
        val testSigGen = "GET&"+URLEncoder.encode(baseAuthUrl)+"&"+URLEncoder.encode(authParams)
        val token_secreta = CurrentUser.oa_token_secret
        Log.i("TestLogAct","Secret is now:${token_secreta}")
        val authSig = URLEncoder.encode(calcHmac(testSigGen, secret+token_secreta))

        val queue = Volley.newRequestQueue(this)
        val url=baseAuthUrl + "?oauth_nonce=" + oa_nonce + "&oauth_timestamp=" + oa_ts +"&oauth_verifier="+oa_verifier+"&oauth_consumer_key=" + apiKey +"&oauth_signature_method=HMAC-SHA1" + "&oauth_version=1.0" +"&oauth_token="+authToken+"&oauth_signature=" + authSig

        val stringrq = StringRequest(com.android.volley.Request.Method.GET,url, { response ->
            Log.i("innerTest", response)
            val respBody = response.split("&","=")
            val fullname = URLDecoder.decode(respBody[1])
            val oauth_token = respBody[3]
            val oauth_token_secret = respBody[5]
            val user_nsid = respBody[7]
            val username = respBody[9]

            CurrentUser.userID = user_nsid
            CurrentUser.fullName = fullname
            CurrentUser.userName = username
            CurrentUser.oa_token = oauth_token
            CurrentUser.oa_token_secret = oauth_token_secret

            val userdB = getUserDB(this)
            val authUser= AuthUser(user_nsid, fullname, oauth_token, oauth_token_secret, username, System.currentTimeMillis().toInt()/1000)

            CoroutineScope(Dispatchers.IO).launch{
                userdB.userDao.addUser(authUser)
            }


            Log.i("VolleyReturn",fullname + oauth_token + oauth_token_secret + user_nsid + username)

            Log.i("CurrentUser", CurrentUser.userName + CurrentUser.fullName)

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }, {
            Log.i("innterTest","WHOOPS!")
        })
        queue.add(stringrq)


        Log.i("LogAct","$url")



        Log.i("LogAct","Rec Data is ${mainIntent.data}")
        Log.i("LogAct","authToken is: ${authToken}")
        Log.i("LogAct","auth_verifier is: ${oa_verifier}")
        Log.i("LogAct","genSig is: ${authSig}")
        Log.i("LogAct", "CurrentUser = ${CurrentUser.userID}")
    }
}