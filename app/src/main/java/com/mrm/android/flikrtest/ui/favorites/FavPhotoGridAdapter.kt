package com.mrm.android.flikrtest.ui.favorites

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.databinding.GridViewItemBinding
import com.mrm.android.flikrtest.ui.main.PhotoGridAdapter
import kotlinx.android.synthetic.main.grid_view_item.view.*

class FavPhotoGridAdapter(private val onClickListener: OnClickListener): ListAdapter<APIPhoto, FavPhotoGridAdapter.APIPhotoViewHolder>(PhotoGridAdapter.DiffCallback) {
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
//        holder.itemView.save_image_details_bttn.setOnClickListener {
//            holder.itemView.save_image_details_bttn.setBackgroundColor(Color.RED)
//        }
    }
    class OnClickListener(val clickListener: (apiPhoto: APIPhoto) -> Unit){
        fun onClick(apiPhoto: APIPhoto) = clickListener(apiPhoto)
    }
}