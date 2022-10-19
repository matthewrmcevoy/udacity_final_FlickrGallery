package com.mrm.android.flikrtest

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrm.android.flikrtest.api.APIPhoto
import com.mrm.android.flikrtest.ui.favorites.FavPhotoGridAdapter
import com.mrm.android.flikrtest.ui.favorites.FavoritesLoadStatus
import com.mrm.android.flikrtest.ui.main.APIStatus
import com.mrm.android.flikrtest.ui.main.PhotoGridAdapter
import com.mrm.android.flikrtest.ui.settings.FavSettingsStatus
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.grid_view_item.view.*

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?){
    Log.i("BindingAdapter","imageUrl is called on: $imgView and $imgUrl")
    Picasso
        .get()
        .load(imgUrl)
        .into(imgView)
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
@BindingAdapter("mainLoadStatus")
fun bindMainLoadStatus(progressBar: ProgressBar, loadAPIStatus: APIStatus){
    when(loadAPIStatus){
        APIStatus.LOADING -> progressBar.visibility = View.VISIBLE
        APIStatus.DONE -> progressBar.visibility = View.GONE
        else -> progressBar.visibility = View.GONE
    }
}
@BindingAdapter("emptyStatus")
fun bindEmptyStatus(textView: TextView, loadAPIStatus: APIStatus){
    when(loadAPIStatus){
        APIStatus.LOADING -> textView.visibility = View.GONE
        APIStatus.DONE -> textView.visibility = View.GONE
        APIStatus.EMPTY -> textView.visibility = View.VISIBLE
        APIStatus.ERROR -> {
            textView.visibility = View.VISIBLE
            textView.gravity = Gravity.CENTER
            textView.text=R.string.conn_err.toString()
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_apps_outage_24)
        }
    }
}
@BindingAdapter("favoritesLoadStatus")
fun bindFavsLoadStat(textView: TextView, favoritesLoadStatus: FavoritesLoadStatus){
    when(favoritesLoadStatus){
        FavoritesLoadStatus.EMPTY -> textView.visibility = View.VISIBLE
        else -> textView.visibility = View.GONE
    }
}
@BindingAdapter("favStat")
fun bindFavStat(progressBar: ProgressBar, favSettingsStatus: FavSettingsStatus?){
    when(favSettingsStatus){
        null -> progressBar.visibility = View.GONE
        FavSettingsStatus.DONE -> progressBar.visibility = View.GONE
        FavSettingsStatus.LOADING -> progressBar.visibility = View.VISIBLE
    }
}