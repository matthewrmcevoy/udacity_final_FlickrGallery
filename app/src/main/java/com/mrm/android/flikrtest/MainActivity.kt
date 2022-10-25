package com.mrm.android.flikrtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mrm.android.flikrtest.oauth.CurrentUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set the layout/navhost in the mainActivity
        setContentView(R.layout.activity_main)

        //Find the NavController and setup the persisted UI elements at the activity level
        //Bottom application bar / Profile Image BUttons / and upload photo floatingActionButton
        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navView.setupWithNavController(navController)

        //hide/unhide menu items based on authstate (guest user cannot have userPhotos from FLICKR)
        val myPhotosButton = navView.menu.findItem(R.id.userPhotosFragment)
        when(CurrentUser.userID){
            "none" -> {
                myPhotosButton.setVisible(false)
            }
            else -> {
                myPhotosButton.setVisible(true)
            }
        }

        //Profile Image Button housing a popup-menu for logout action
        val profileImage: ImageView = findViewById(R.id.profile_image_bttn)
        profileImage.setOnClickListener{
            showPopup(it)
        }

        //Upload Photo button - Only available to authorized users. Handled in mainFragment to capture auth events
        val cameraButton: FloatingActionButton = findViewById(R.id.take_photo_bttn)
        when(CurrentUser.userID){
            "none" -> cameraButton.visibility = View.GONE
            else -> cameraButton.visibility = View.VISIBLE
        }

        cameraButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.takePhotoFragment)
        }

    }

    //Define the popup function and attach it to the profileImage button as its anchor
    fun showPopup(view: View){
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.logout_menu)
        popup.gravity= Gravity.TOP
        popup.show()

        //define the action of the only item in the menu (to change users)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it.itemId){
                R.id.logout_action -> {
                    CurrentUser.userID = "none"
                    profile_image_bttn.setImageResource(android.R.color.transparent)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.welcomeFragment)
                }
                R.id.settingsFragment -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                }
            }
            true
        })
    }
}