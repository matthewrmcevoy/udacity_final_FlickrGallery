package com.mrm.android.flikrtest.ui.main

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.dB.getDatabase
import com.mrm.android.flikrtest.databinding.GridViewItemBinding
import kotlinx.android.synthetic.main.grid_view_item.view.*
import kotlinx.android.synthetic.main.photo_detail_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class PhotoGridAdapter(private val onClickListener: OnClickListener) : ListAdapter<APIPhoto, PhotoGridAdapter.APIPhotoViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoGridAdapter.APIPhotoViewHolder {
        return APIPhotoViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PhotoGridAdapter.APIPhotoViewHolder, position: Int) {
        val apiPhoto = getItem(position)
        holder.bind(apiPhoto)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(apiPhoto)
        }
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


