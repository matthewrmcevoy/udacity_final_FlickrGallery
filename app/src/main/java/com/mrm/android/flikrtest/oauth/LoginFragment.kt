package com.mrm.android.flikrtest.oauth

import android.app.Application
import android.content.Intent
import android.content.Intent.getIntent
import android.content.Intent.makeMainActivity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.mrm.android.flikrtest.MainActivity
import com.mrm.android.flikrtest.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.lang.Math.abs
import java.net.URI
import java.net.URLEncoder
import java.security.SignatureException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class LoginFragment : Fragment() {

    private val apiKey = "64187751b962051b4eb86e0c23bd7874"
    private val secret = "d3cc409f4d513aea&"
    private val redirectUri = "flickrtest%3A%2F%2Fcallback"
    private val callbackN = "flickrtest://callback"
    private val nonce = Random.nextInt(0, 99999999)
    private val testNonce = "95613465"
    private val ts = System.currentTimeMillis()/1000
    private val testTs = "1305586162"
    private val baseUrl ="https://www.flickr.com/services/oauth/request_token"
    private val baseText = "GET&"+baseUrl + "&oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
    private val sortParam = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
    private val testSortParams = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
    private val testSigGen = "GET&"+URLEncoder.encode(baseUrl)+"&"+URLEncoder.encode(testSortParams)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentLoginBinding.inflate(inflater)
        val testString = "GET&https%3A%2F%2Fwww.flickr.com%2Fservices%2Foauth%2Frequest_token&oauth_callback%3Dhttp%253A%252F%252Fwww.example.com%26oauth_consumer_key%3D64187751b962051b4eb86e0c23bd7874%26oauth_nonce%3D95613465%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1305586162%26oauth_version%3D1.0"
        val testoauth = calcHmac(testString, secret)
        val testoauthEncoded = URLEncoder.encode(testoauth)
        val testSigGenOauth = calcHmac(testSigGen,secret)
        val testSignGenOauthEnc = URLEncoder.encode(testSigGenOauth)

        Log.i("test",testSigGen)
        Log.i("test",testString)
        Log.i("test56", testoauth)
        Log.i("test57", testoauthEncoded)
        Log.i("test58", testSignGenOauthEnc)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?oauth_nonce=" + nonce + "&oauth_timestamp=" + ts +"&oauth_consumer_key=" + apiKey +"&oauth_signature_method=HMAC-SHA1" + "&oauth_version=1.0" + "&oauth_signature=" + testSignGenOauthEnc + "&oauth_callback=" + callbackN))
        binding.button.setOnClickListener {


            val queue = Volley.newRequestQueue(requireContext())
            val url=baseUrl + "?oauth_nonce=" + nonce + "&oauth_timestamp=" + ts +"&oauth_consumer_key=" + apiKey +"&oauth_signature_method=HMAC-SHA1" + "&oauth_version=1.0" + "&oauth_signature=" + testSignGenOauthEnc + "&oauth_callback=" + callbackN

            val stringrq = StringRequest(com.android.volley.Request.Method.GET,url, { response ->
                Log.i("innerTest", response)
                val respBody = response.split("&")
                val rtrnOauthToken = respBody[1]
                val rtrnOauthTokenSecret = respBody[2]
                val token_secrett = rtrnOauthTokenSecret.split("=")
                val token_secret = token_secrett[1]
                CurrentUser.oa_token_secret = token_secret
                Log.i("innerTest", rtrnOauthToken)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flickr.com/services/oauth/authorize?"+rtrnOauthToken+"&perms=delete"))
                startActivity(intent)
            }, {
                Log.i("innterTest","WHOOPS!")
            })
            queue.add(stringrq)
            //startActivity(intent)

        }


        return binding.root
    }

//    fun calcHmac(data: String, key: String):String{
//        val signingKey = SecretKeySpec(key.toByteArray(), "HmacSHA1")
//        val mac = Mac.getInstance("HmacSHA1")
//        mac.init(signingKey)
//        //return transToHexString(mac.doFinal(data.toByteArray()))
//        return Base64.getEncoder().encodeToString(mac.doFinal(data.toByteArray()))
//    }

}