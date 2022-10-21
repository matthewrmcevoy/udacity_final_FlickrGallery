package com.mrm.android.flikrtest.ui.welcome

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentWelcomeBinding
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.oauth.calcHmac
import com.squareup.picasso.Picasso
import java.net.URLEncoder
import kotlin.random.Random

class WelcomeFragment : Fragment() {


    val viewModel: WelcomeViewModel by lazy{
        ViewModelProvider(this).get(WelcomeViewModel::class.java)
    }
    //AUTHENTICATION
    private val apiKey = "64187751b962051b4eb86e0c23bd7874"
    private val secret = "d3cc409f4d513aea&"
    private val redirectUri = "flickrtest%3A%2F%2Fcallback"
    private val callbackN = "flickrtest://callback"
    private val nonce = Random.nextInt(0, 99999999)
    private val ts = System.currentTimeMillis()/1000
    private val baseUrl ="https://www.flickr.com/services/oauth/request_token"
    private val baseText = "GET&"+baseUrl + "&oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
    private val sortParam = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
    private val testSortParams = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
    private val testSigGen = "GET&"+ URLEncoder.encode(baseUrl)+"&"+ URLEncoder.encode(testSortParams)
    val testSigGenOauth = calcHmac(testSigGen,secret)
    val testSignGenOauthEnc = URLEncoder.encode(testSigGenOauth)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWelcomeBinding.inflate(inflater)

        binding.loginBttn.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flickr.com/services/oauth/authorize?"+rtrnOauthToken+"&perms=delete"))
                startActivity(intent)
            }, {
                Log.i("innterTest","WHOOPS!")
            })
            queue.add(stringrq)
        }
        binding.guestBttn.setOnClickListener {
            viewModel.setCurrentUser("guest")
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment())
        }
        viewModel.recentUser.observe(viewLifecycleOwner, Observer{
            if(it.userID != "none"){
                binding.continueAsLastBttn.visibility = View.VISIBLE
                binding.continueAsLastBttn.text = "Continue as ${viewModel.recentUser.value!!.userName}"
            }
        })

        binding.continueAsLastBttn.setOnClickListener {
            viewModel.setCurrentUser("user")
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment())

        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bottomNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavBar.visibility = View.GONE

        viewModel.skipToMain.observe(viewLifecycleOwner, Observer{
            Log.i("WelFrag","Setting = ${viewModel.skipToMain.value}")
            if(it == 1){
                viewModel.resetSkipToMain()
                findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment())
            }
        })
    }

}