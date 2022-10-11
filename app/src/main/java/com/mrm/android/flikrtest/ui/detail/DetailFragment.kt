package com.mrm.android.flikrtest.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import com.mrm.android.flikrtest.databinding.PhotoDetailFragmentBinding
import com.mrm.android.flikrtest.ui.main.MainViewModel

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        val binding = PhotoDetailFragmentBinding.inflate(inflater)
        val apiPhoto = DetailFragmentArgs.fromBundle(requireArguments()).selectedPhoto
        val viewModelFactory = DetailViewModelFactory(apiPhoto, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
        binding.viewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        binding.imageButton3.setOnClickListener {
            viewModel.addFavorite(apiPhoto)
        }
        return binding.root
    }
}