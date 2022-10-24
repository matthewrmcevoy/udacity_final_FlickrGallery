package com.mrm.android.flikrtest.ui.userphotos

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentUserPhotosBinding

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
            null
        })
        return binding.root
    }

}