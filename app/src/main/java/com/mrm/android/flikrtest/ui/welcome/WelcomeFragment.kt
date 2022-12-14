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
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentWelcomeBinding
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.oauth.calcHmac
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.coroutines.*
import java.net.URLEncoder
import kotlin.random.Random

class WelcomeFragment : Fragment() {
    val viewModel: WelcomeViewModel by lazy{
        ViewModelProvider(this).get(WelcomeViewModel::class.java)
    }
    //AUTHENTICATION
//    private val apiKey = "64187751b962051b4eb86e0c23bd7874"
//    private val secret = "d3cc409f4d513aea&"
//    private val redirectUri = "flickrtest%3A%2F%2Fcallback"
//    private val callbackN = "flickrtest://callback"
//    private val nonce = Random.nextInt(0, 99999999)
//    private val ts = System.currentTimeMillis()/1000
//    private val baseUrl ="https://www.flickr.com/services/oauth/request_token"
//    private val baseText = "GET&"+baseUrl + "&oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
//    private val sortParam = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
//    private val testSortParams = "oauth_callback="+redirectUri+"&oauth_consumer_key="+apiKey+"&oauth_nonce="+nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_version=1.0"
//    private val testSigGen = "GET&"+ URLEncoder.encode(baseUrl)+"&"+ URLEncoder.encode(testSortParams)
//    val testSigGenOauth = calcHmac(testSigGen,secret)
//    val testSignGenOauthEnc = URLEncoder.encode(testSigGenOauth)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWelcomeBinding.inflate(inflater)

        binding.loginBttn.setOnClickListener {
            var authToken = ""
            viewModel.retrieveOAuthToken()
            viewModel.oauthToken.observe(viewLifecycleOwner, Observer {
                authToken = it
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flickr.com/services/oauth/authorize?"+authToken+"&perms=delete"))
                startActivity(intent)
                })



        }
        binding.guestBttn.setOnClickListener {
            viewModel.setCurrentUser("guest")
            Toast.makeText(requireContext(),R.string.guest_restrictions, Toast.LENGTH_LONG).show()
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment())
        }

        binding.continueAsLastBttn.setOnClickListener {
            viewModel.setCurrentUser("user")
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment())

        }

        val listener = object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(motionLayout: MotionLayout?,startId: Int,endId: Int) {
                viewModel.recentUser.observe(viewLifecycleOwner, Observer{
                    if(it.userID != "none"){
                        continue_as_last_bttn.text = "Continue as ${viewModel.recentUser.value!!.userName}"
                    }
                })
                login_bttn.visibility = View.INVISIBLE
                guest_bttn.visibility = View.INVISIBLE
            }
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int,endId: Int,progress: Float
            ) {
                Log.i("test", progress.toString())
                progressBar2.setProgress((progress*100).toInt(), true)
            }
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                viewModel.recentUser.observe(viewLifecycleOwner, Observer{
                    if(it.userID != "none"){
                        continue_as_last_bttn.visibility = View.VISIBLE
                        progressBar2.visibility = View.GONE
                    }
                })
                login_bttn.visibility = View.VISIBLE
                guest_bttn.visibility = View.VISIBLE
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                //do nothing
            }
        }
        binding.motionLayout.setTransitionListener(listener)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*Hide the bottom navigation bar during the user selection process to prevent navigation
        * prior to authorization - avoids overriding bottomNavBar item click listeners to wrap
        * with conditional logic*/
        val bottomNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavBar.visibility = View.GONE

        val cameraButton = requireActivity().findViewById<FloatingActionButton>(R.id.take_photo_bttn)
        cameraButton.visibility = View.GONE

        val profileImage = requireActivity().findViewById<ImageView>(R.id.profile_image_bttn)
        profileImage.visibility = View.GONE

        viewModel.skipToMain.observe(viewLifecycleOwner, Observer{
            Log.i("WelFrag","Setting = ${viewModel.skipToMain.value}")
            if(it == 1){
                viewModel.resetSkipToMain()
                findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment())
            }
        })
    }

}