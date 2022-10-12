package com.mrm.android.flikrtest.ui.favorites

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.FavoritePhotosDatabase
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.databinding.GridViewItemBinding
import com.mrm.android.flikrtest.ui.main.PhotoGridAdapter
import kotlinx.android.synthetic.main.grid_view_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavPhotoGridAdapter(private val viewModel: FavoritesViewModel, private val context: Context, private val onClickListener: OnClickListener): ListAdapter<APIPhoto, FavPhotoGridAdapter.APIPhotoViewHolder>(PhotoGridAdapter.DiffCallback) {
    private val database: FavoritePhotosDatabase = getDatabase(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavPhotoGridAdapter.APIPhotoViewHolder {
        return FavPhotoGridAdapter.APIPhotoViewHolder(
            GridViewItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }
    class APIPhotoViewHolder(private var binding: GridViewItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(apiPhoto: APIPhoto?){
            binding.photo = apiPhoto
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: APIPhotoViewHolder, position: Int) {
        val apiPhoto = getItem(position)
        holder.bind(apiPhoto)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(apiPhoto)
        }
        holder.itemView.setOnLongClickListener{
            Log.i("adapter","$apiPhoto")
            showPopup(it, apiPhoto, position)
            true
        }
    }
    private fun showPopup(view: View, apiPhoto: APIPhoto, position: Int){
        val popup = PopupMenu(context, view.txtAnchor)
        popup.inflate(R.menu.favpopup_menu)
        popup.gravity= Gravity.TOP
        popup.show()

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it.itemId){
                R.id.favpop_unfav -> {
                    CoroutineScope(Dispatchers.IO).launch{
                        database.favoritePhotoDao.deleteFavorite(apiPhoto.media)
                        viewModel.updateFavorites()
                    }
                    //updateAdapter(position)

                }
            }
            true
        })

    }
    class OnClickListener(val clickListener: (apiPhoto: APIPhoto) -> Unit){
        fun onClick(apiPhoto: APIPhoto) = clickListener(apiPhoto)
    }
    private fun updateAdapter(position: Int){
        notifyItemRemoved(position)
        notifyItemRangeChanged(position+1, itemCount-1)
    }
}