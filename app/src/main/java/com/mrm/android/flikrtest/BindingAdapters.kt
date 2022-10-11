package com.mrm.android.flikrtest

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.ui.favorites.FavPhotoGridAdapter
import com.mrm.android.flikrtest.ui.main.PhotoGridAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?){
    Log.i("BindingAdapter","imageUrl is called on: $imgView and $imgUrl")
    Picasso.get().load(imgUrl).into(imgView)
    }

@BindingAdapter("listData")
fun bindRecycleView(recyclerView: RecyclerView, data: List<APIPhoto>?){
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}
@BindingAdapter("favListData")
fun bindFavRecycleView(recyclerView: RecyclerView, data: List<APIPhoto>?){
    val adapter = recyclerView.adapter as FavPhotoGridAdapter
    adapter.submitList(data)
}