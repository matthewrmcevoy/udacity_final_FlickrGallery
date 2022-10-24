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
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.FlikrUploadApi
import com.mrm.android.flikrtest.databinding.FragmentTakePhotoBinding
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.oauth.OAuthConstants
import com.mrm.android.flikrtest.oauth.calcHmac
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_take_photo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.file.Files.createFile
import kotlin.random.Random

private const val FILE_NAME = "photo.jpg"
class TakePhotoFragment : Fragment() {
        private lateinit var viewModel: TakePhotoViewModel
        private lateinit var photoFile: File


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTakePhotoBinding.inflate(inflater)

        requireActivity().findViewById<FloatingActionButton>(R.id.floatingActionButton).visibility = View.GONE
        binding.floatingActionButton2.visibility = View.GONE

        binding.captureImgBttn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(requireContext(), "com.mrm.android.flikrtest.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            //if (intent.resolveActivity(requireActivity().packageManager) != null){
                startActivityForResult(intent, 1)
            //}else{
               // Toast.makeText(requireContext(),"No camera app detected", Toast.LENGTH_SHORT).show()
            //}
        }

        binding.floatingActionButton2.setOnClickListener {
            uploadFile()
        }


        return binding.root
    }



    private fun uploadFile(){
        val oack = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),OAuthConstants.API_KEY)
        val nonce = Random.nextInt(0, 99999999).toString()
        val oan = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nonce)
        val oasm = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_SIG_METHOD)
        val ts = (System.currentTimeMillis()/1000).toString()
        val oats = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ts)
        val oat = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_TOKEN)
        val oav = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_VERSION)
        val titleTxt = URLEncoder.encode(photo_upload_title_txt.text.toString()).replace("+", "%20")
        val title =RequestBody.create("text/plain".toMediaTypeOrNull(), photo_upload_title_txt.text.toString())
        val sortParams = "oauth_consumer_key="+OAuthConstants.API_KEY+"&oauth_nonce="+nonce+"&oauth_signature_method="+OAuthConstants.OAUTH_SIG_METHOD+"&oauth_timestamp="+ts+"&oauth_token="+OAuthConstants.OAUTH_TOKEN+"&oauth_version="+OAuthConstants.OAUTH_VERSION+"&title="+titleTxt
        val oabasetext = "POST&"+URLEncoder.encode(OAuthConstants.UPLOAD_BASE)+"&"+URLEncoder.encode(sortParams)
        val oas = calcHmac(oabasetext,OAuthConstants.API_SECRET+OAuthConstants.OAUTH_TOKEN_SECRET)
        val oasig = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), oas)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), photoFile)
        val image = MultipartBody.Part.createFormData("photo","photo.jpg",requestFile)


        CoroutineScope(Dispatchers.IO).launch{
            try{
                val response = FlikrUploadApi.retrofitService.uploadPhoto(oack,oan,oasm,oats,oat,oav,oasig,title,image)
                Log.i("Upload", "$response + ${response.body()}")
                Log.i("Upload","${sortParams}")
                Log.i("Upload","${oabasetext}")
                Log.i("Upload2",URLEncoder.encode(titleTxt)+ titleTxt)
                Snackbar.make(requireView(),"Upload Succesful!",Snackbar.LENGTH_INDEFINITE).setAction("View",{
                    findNavController().navigate(R.id.userPhotosFragment)
                }).show()
            }catch(e: Exception){
                Log.i("Upload","$e")
            }

        }
        val token = CurrentUser.oa_token
        Log.i("Uploader", "$token")

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
            floatingActionButton2.visibility=View.VISIBLE
            photo_upload_title_txt.visibility=View.VISIBLE
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}