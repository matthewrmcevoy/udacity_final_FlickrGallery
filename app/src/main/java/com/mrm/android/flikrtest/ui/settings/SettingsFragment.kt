package com.mrm.android.flikrtest.ui.settings

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlin.math.absoluteValue

class SettingsFragment : Fragment(){
    val viewModel: SettingsViewModel by lazy{
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(inflater)

        viewModel.favCount.observe(viewLifecycleOwner, Observer {
            if(it > 0){
                wipe_fav_bttn.setTextColor(resources.getColor(R.color.app_purple))
                val drawable = wipe_fav_bttn.compoundDrawables
                for(item in drawable){
                    item?.setColorFilter(PorterDuffColorFilter(resources.getColor(R.color.app_purple), PorterDuff.Mode.SRC_IN))
                }
                wipe_fav_bttn.isEnabled = true
            }else{
                wipe_fav_bttn.setTextColor(Color.GRAY)
                val drawable = wipe_fav_bttn.compoundDrawables
                for(item in drawable){
                    item?.setColorFilter(PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN))
                }
                wipe_fav_bttn.isEnabled = false
            }
        })

        viewModel.searchCount.observe(viewLifecycleOwner, Observer{
            if(it > 0){
                clr_srch_hist_bttn.setTextColor(resources.getColor(R.color.app_purple))
                val drawable = clr_srch_hist_bttn.compoundDrawables
                for(item in drawable){
                    item?.setColorFilter(PorterDuffColorFilter(resources.getColor(R.color.app_purple), PorterDuff.Mode.SRC_IN))
                }
                clr_srch_hist_bttn.isEnabled = true
            }else{
                clr_srch_hist_bttn.setTextColor(Color.GRAY)
                val drawable = clr_srch_hist_bttn.compoundDrawables
                for(item in drawable){
                    item?.setColorFilter(PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN))
                }
                clr_srch_hist_bttn.isEnabled = false
            }
        })

        binding.wipeFavBttn.setOnClickListener {
            if(viewModel.favCount.value!! > 0){
                viewModel.clearFavorites()
                Snackbar.make(this.requireView(),"Favorites Cleared!", Snackbar.LENGTH_INDEFINITE).setAction("Ok"){null}.show()
            }else{
                Toast.makeText(this.requireContext(),"Favorites is empty!", Toast.LENGTH_SHORT).show()
            }

        }

        binding.clrSrchHistBttn.setOnClickListener {
            if(viewModel.searchCount.value!! > 0){
                viewModel.clearSearchHistory()
                Snackbar.make(this.requireView(),"Search History Cleared!", Snackbar.LENGTH_INDEFINITE).setAction("Ok"){null}.show()
            }else{
                Toast.makeText(this.requireContext(),"Search History is empty!", Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }
}