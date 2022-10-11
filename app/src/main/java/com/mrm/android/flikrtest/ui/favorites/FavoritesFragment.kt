package com.mrm.android.flikrtest.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mrm.android.flikrtest.databinding.FragmentFavoritesBinding
import kotlinx.android.synthetic.main.fragment_main.view.*

class FavoritesFragment: Fragment() {
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentFavoritesBinding.inflate(inflater)
        binding.favPhotoGrid.adapter = FavPhotoGridAdapter(FavPhotoGridAdapter.OnClickListener{
            this.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(it))
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }
}