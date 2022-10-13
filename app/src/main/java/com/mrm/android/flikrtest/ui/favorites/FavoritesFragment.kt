package com.mrm.android.flikrtest.ui.favorites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.FavoritePhotosDatabase
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.databinding.FragmentFavoritesBinding
import com.mrm.android.flikrtest.databinding.GridViewItemBinding
import com.mrm.android.flikrtest.ui.main.PhotoGridAdapter
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.grid_view_item.view.*
import kotlinx.coroutines.*

class FavoritesFragment: Fragment() {
    val viewModel: FavoritesViewModel by lazy {
        ViewModelProvider(this).get(FavoritesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentFavoritesBinding.inflate(inflater)
        binding.favPhotoGrid.adapter =
            FavPhotoGridAdapter(viewModel, requireContext(), FavPhotoGridAdapter.OnClickListener {
                this.findNavController()
                    .navigate(FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(it))
            })

        binding.addFavorites.setOnClickListener {
            this.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToMainFragment())
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }
}