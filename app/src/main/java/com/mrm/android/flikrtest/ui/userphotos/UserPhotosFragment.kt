package com.mrm.android.flikrtest.ui.userphotos

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentUserPhotosBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_photos.*

class UserPhotosFragment : Fragment() {

    private val viewModel: UserPhotosViewModel by lazy{
        ViewModelProvider(this).get(UserPhotosViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserPhotosBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.userPhotoGrid.adapter = UserPhotoGridAdapter(UserPhotoGridAdapter.OnClickListener{
            showFullPhoto(it)
        })

        //REINSTATE THE CAMERA BUTTON
        requireActivity().findViewById<FloatingActionButton>(R.id.take_photo_bttn).visibility = View.VISIBLE
        return binding.root
    }

    //take the passed UserPhoto and display the full image
    fun showFullPhoto(userPhoto: UserPhoto){
        val url = userPhoto.photoUrl
        val fullPhoto = user_detail_view
        /*Load the passed UserPhoto into our overlay imageView
        * then set the imageView to visible and establish its own
        * onClickListener to dismiss onClick */
        Picasso.get().load(url).into(fullPhoto)

        fullPhoto.visibility=View.VISIBLE
        fullPhoto.setOnClickListener {
            fullPhoto.visibility = View.GONE
        }
    }

}