package com.mrm.android.flikrtest.ui.userphotos

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return binding.root
    }

    fun showFullPhoto(userPhoto: UserPhoto){
        val url = userPhoto.photoUrl
        Picasso.get().load(url).into(user_detail_view)
        user_detail_view.visibility=View.VISIBLE
        user_detail_view.setOnClickListener {
            user_detail_view.visibility = View.GONE
        }
    }

}