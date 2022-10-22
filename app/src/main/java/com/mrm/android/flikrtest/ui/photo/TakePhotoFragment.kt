package com.mrm.android.flikrtest.ui.photo

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.databinding.FragmentTakePhotoBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_take_photo.*
import java.io.File
import java.net.URI

private const val FILE_NAME = "photo.jpg"
class TakePhotoFragment : Fragment() {
        private lateinit var viewModel: TakePhotoViewModel
        private lateinit var photoFile: File


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTakePhotoBinding.inflate(inflater)

        binding.button2.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(requireContext(), "com.mrm.android.flikrtest.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (intent.resolveActivity(requireActivity().packageManager) != null){
                startActivityForResult(intent, 1)
            }else{
                Toast.makeText(requireContext(),"No camera app detected", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun uploadFile(){
        // test flickr upload file API code
    }

    private fun getPhotoFile(fileName : String): File{
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TakePhotoViewModel::class.java)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(photoFile.absolutePath)
            capture_image.setImageBitmap(takenPhoto)
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}