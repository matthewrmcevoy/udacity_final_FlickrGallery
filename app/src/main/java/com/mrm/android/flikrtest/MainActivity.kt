package com.mrm.android.flikrtest

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.ui.main.MainFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.grid_view_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navView.setupWithNavController(navController)

        val profileImage: ImageView = findViewById(R.id.profile_image_bttn)
        profileImage.setOnClickListener{
            showPopup(it)
        }

        val cameraButton: FloatingActionButton = findViewById(R.id.floatingActionButton)
        cameraButton.visibility = View.GONE
        cameraButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.takePhotoFragment)
        }

    }

    override fun onResume() {
        super.onResume()
    }

    fun showPopup(view: View){
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.logout_menu)
        popup.gravity= Gravity.TOP
        popup.show()

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it.itemId){
                R.id.logout_action -> {
                    CurrentUser.userID = "none"
                    profile_image_bttn.setImageResource(android.R.color.transparent)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.welcomeFragment)
                }
            }
            true
        })
    }
}