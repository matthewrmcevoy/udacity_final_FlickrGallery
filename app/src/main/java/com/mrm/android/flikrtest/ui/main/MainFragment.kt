package com.mrm.android.flikrtest.ui.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentMainBinding
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.search_history.view.*

class MainFragment : Fragment() {


    private val viewModel: MainViewModel by lazy{
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.photosGrid.adapter = PhotoGridAdapter(viewModel,requireContext(),PhotoGridAdapter.OnClickListener{
            this.findNavController().navigate(MainFragmentDirections.actionPhotoDetails(it))
        })
        val bottomNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavBar.visibility = View.VISIBLE

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        var searchText:String = ""
        val actv:AutoCompleteTextView = binding.root.findViewById(R.id.tag_search_txt)
        val adapter = ArrayAdapter<String>(requireActivity(),R.layout.search_history, R.id.search_tv , viewModel.recentSearch)
        actv.setAdapter(adapter)

        actv.setOnClickListener{
            actv.showDropDown()
        }

        actv.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == 66){
                searchText = tag_search_txt.text.toString()
                viewModel.addSearch(searchText)
                viewModel.updatePhotoFilter(searchText)
                val adapter = ArrayAdapter<String>(requireActivity(),R.layout.search_history,R.id.search_tv ,viewModel.recentSearch)
                actv.setAdapter(adapter)


                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                tag_search_txt.setText("")
                actv.dismissDropDown()
                true
            }
            false
        }
        actv.setOnItemClickListener { adapterView, view, i, l ->
            var selectedItem = adapterView.getItemAtPosition(i) as String
            Log.i("selected", selectedItem)
            viewModel.updatePhotoFilter(selectedItem)
        }


        binding.imageButton.setOnClickListener {
            searchText = tag_search_txt.text.toString()
            viewModel.addSearch(searchText)
            Log.i("Fragment","recent searches are ${viewModel.recentSearch}")
            viewModel.updatePhotoFilter(searchText)
            val adapter = ArrayAdapter<String>(requireActivity(),R.layout.search_history, viewModel.recentSearch)
            actv.setAdapter(adapter)

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            tag_search_txt.setText("")
            tag_search_txt.clearFocus()
        }

        //Display Camera if user is authorized
        val cameraButton: FloatingActionButton = requireActivity().findViewById(R.id.take_photo_bttn)
        if(CurrentUser.userID != "none"){
            cameraButton.visibility = View.VISIBLE
        }

        //Display "MyPhotos" Bottom Nav Button for navigation if user is authorized
        val myPhotosButton = bottomNavBar.menu.findItem(R.id.userPhotosFragment)
        when(CurrentUser.userID){
            "none" -> {
                myPhotosButton.setVisible(false)
            }
            else -> {
                myPhotosButton.setVisible(true)
            }
        }


        viewModel.imageUrl.observe(viewLifecycleOwner, Observer{
            val profileImage = requireActivity().findViewById<ImageView>(R.id.profile_image_bttn)
            profileImage.visibility = View.VISIBLE
            Log.i("MF","$it")
            if(it != "default"){
                Picasso.get().load(it).into(profileImage)
            }else{
                profileImage.setImageResource(R.drawable.default_profile_48)
            }

        })

        return binding.root
    }

}