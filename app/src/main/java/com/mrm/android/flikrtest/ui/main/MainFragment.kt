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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

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
        binding.photosGrid.adapter = PhotoGridAdapter(requireContext(),PhotoGridAdapter.OnClickListener{
            this.findNavController().navigate(MainFragmentDirections.actionPhotoDetails(it))
        })




        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        var searchText:String = ""
        val actv:AutoCompleteTextView = binding.root.findViewById(R.id.tag_search_txt)
        val adapter = ArrayAdapter<String>(requireActivity(),R.layout.search_history, viewModel.recentSearch)
        actv.setAdapter(adapter)

        actv.setOnClickListener{
            actv.showDropDown()
        }

        actv.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == 66){
                searchText = tag_search_txt.text.toString()
                viewModel.addSearch(searchText)
                viewModel.updatePhotoFilter(searchText)
                val adapter = ArrayAdapter<String>(requireActivity(),R.layout.search_history, viewModel.recentSearch)
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

        return binding.root
    }

}