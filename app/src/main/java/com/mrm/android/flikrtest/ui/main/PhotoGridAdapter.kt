package com.mrm.android.flikrtest.ui.main

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.FavoritePhotosDatabase
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.databinding.GridViewItemBinding
import kotlinx.android.synthetic.main.grid_view_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoGridAdapter(private val viewModel: MainViewModel, private val context: Context, private val onClickListener: OnClickListener) : ListAdapter<APIPhoto, PhotoGridAdapter.APIPhotoViewHolder>(DiffCallback) {
    private val database: FavoritePhotosDatabase = getDatabase(context)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoGridAdapter.APIPhotoViewHolder {
        return APIPhotoViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PhotoGridAdapter.APIPhotoViewHolder, position: Int) {
        val apiPhoto = getItem(position)
        holder.bind(apiPhoto)
        if(viewModel.isFavorite(apiPhoto)){
            holder.itemView.fav_add_main_bttn.visibility = View.GONE
            holder.itemView.fav_add_remove_bttn.visibility = View.VISIBLE
        }else{
            holder.itemView.fav_add_main_bttn.visibility = View.VISIBLE
            holder.itemView.fav_add_remove_bttn.visibility = View.GONE
        }
        holder.itemView.setOnClickListener{
            onClickListener.onClick(apiPhoto)
        }
        holder.itemView.fav_add_main_bttn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                database.favoritePhotoDao.addFavoritePhoto(apiPhoto)
            }
            holder.itemView.fav_add_main_bttn.visibility = View.GONE
            holder.itemView.fav_add_remove_bttn.visibility = View.VISIBLE
        }
        holder.itemView.fav_add_remove_bttn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                database.favoritePhotoDao.deleteFavorite(apiPhoto.media)
            }
            holder.itemView.fav_add_main_bttn.visibility = View.VISIBLE
            holder.itemView.fav_add_remove_bttn.visibility = View.GONE
        }
        holder.itemView.setOnLongClickListener{
            Log.i("adapter","$apiPhoto")
            showPopup(it, apiPhoto)
            true
        }
    }
    private fun showPopup(view: View, apiPhoto: APIPhoto){
        val popup = PopupMenu(context, view.txtAnchor)
        popup.inflate(R.menu.popup_menu)
        popup.gravity=Gravity.TOP
        popup.show()

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it.itemId){
                R.id.logout_action -> {
                    CoroutineScope(Dispatchers.IO).launch{
                        database.favoritePhotoDao.addFavoritePhoto(apiPhoto)
                    }
                }
            }
            true
        })

    }

    class APIPhotoViewHolder(private var binding: GridViewItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(apiPhoto: APIPhoto?){
            binding.photo = apiPhoto
            binding.executePendingBindings()
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<APIPhoto>() {
        override fun areItemsTheSame(oldItem: APIPhoto, newItem: APIPhoto): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: APIPhoto, newItem: APIPhoto): Boolean {
            return oldItem.media == newItem.media
        }
    }
    class OnClickListener(val clickListener: (apiPhoto: APIPhoto) -> Unit){
        fun onClick(apiPhoto: APIPhoto) = clickListener(apiPhoto)
    }
}


