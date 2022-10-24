package com.mrm.android.flikrtest.ui.userphotos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.databinding.GridViewItemBinding
import com.mrm.android.flikrtest.databinding.UserPhotosGridBinding


class UserPhotoGridAdapter(private val onClickListener: OnClickListener) : ListAdapter<UserPhoto, UserPhotoGridAdapter.UserPhotoViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPhotoViewHolder {
        return UserPhotoViewHolder(UserPhotosGridBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: UserPhotoViewHolder, position: Int) {
        val userPhoto = getItem(position)
        holder.bind(userPhoto)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(userPhoto)
        }
    }
    class UserPhotoViewHolder(private var binding: UserPhotosGridBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(userPhoto: UserPhoto?){
            binding.userImage = userPhoto
            binding.executePendingBindings()
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<UserPhoto>() {
        override fun areItemsTheSame(oldItem: UserPhoto, newItem: UserPhoto): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UserPhoto, newItem: UserPhoto): Boolean {
            return oldItem.photoUrl == newItem.photoUrl
        }
    }
    class OnClickListener(val clickListener: (userPhoto: UserPhoto) -> Unit){
        fun onClick(userPhoto: UserPhoto) = clickListener(userPhoto)
    }
}