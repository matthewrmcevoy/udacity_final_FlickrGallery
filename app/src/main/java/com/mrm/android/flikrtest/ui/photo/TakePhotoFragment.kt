package com.mrm.android.flikrtest.ui.photo

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.FlikrUploadApi
import com.mrm.android.flikrtest.databinding.FragmentTakePhotoBinding
import com.mrm.android.flikrtest.oauth.CurrentUser
import com.mrm.android.flikrtest.oauth.OAuthConstants
import com.mrm.android.flikrtest.oauth.calcHmac
import kotlinx.android.synthetic.main.fragment_take_photo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
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
        viewModel = ViewModelProvider(this).get(TakePhotoViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        //Hide the take/post photo floatingActionButton since no need to navigate to this fragment again
        //also initialize the uploadPhotoBttn as GONE and only display after an image has been captured
        requireActivity().findViewById<FloatingActionButton>(R.id.take_photo_bttn).visibility = View.GONE
        binding.uploadPhotoBttn.visibility = View.GONE

        //Set button to launch camera application and capture an image
        binding.captureImgBttn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(requireContext(), "com.mrm.android.flikrtest.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            startActivityForResult(intent, 1)

        }

        /*After an image is captured this button becomes visible and will upload the captured
        * file using multipart POST method via retrofit*/
        binding.uploadPhotoBttn.setOnClickListener {
            uploadPhoto()
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            when(it){
                UPLOAD_STATUS.UPLOADING -> {
                    uploadProgressBar.visibility = View.VISIBLE
                }
                UPLOAD_STATUS.DONE -> {
                    uploadProgressBar.visibility = View.GONE
                }
                UPLOAD_STATUS.ERROR ->{
                    //HIDE LOADING BAR + DISPLAY SNACKBAR
                    uploadProgressBar.visibility = View.GONE
                    Snackbar.make(requireView(),R.string.upload_failed,Snackbar.LENGTH_LONG).setAction(android.R.string.ok) {
                        null
                    }.show()
                }
                UPLOAD_STATUS.SUCCESS ->{
                    //HIDE LOADING BAR + DISPLAY SNACKBAR
                    uploadProgressBar.visibility = View.GONE
                    Snackbar.make(requireView(),R.string.upload_success,Snackbar.LENGTH_LONG).setAction(R.string.view) {
                        findNavController().navigate(R.id.userPhotosFragment)
                        }.show()
                }
            }
        })

        viewModel.clearEvent.observe(viewLifecycleOwner, Observer {
            when(it){
                true -> {
                    capture_image.setImageBitmap(null)
                    photo_upload_title_txt.text = null
                    photo_upload_title_txt.visibility = View.GONE
                    upload_photo_bttn.visibility = View.GONE
                }
                false -> {
                    null
                }
            }
        })

        return binding.root
    }

    private fun uploadPhoto(){
        viewModel.uploadFile(photoFile,photo_upload_title_txt.text.toString())
    }

//    private fun uploadFile(){
//        //establish all the components required for the multipart upload request
//        val oack = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),OAuthConstants.API_KEY)
//        val nonce = Random.nextInt(0, 99999999).toString()
//        val oan = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nonce)
//        val oasm = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_SIG_METHOD)
//        val ts = (System.currentTimeMillis()/1000).toString()
//        val oats = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ts)
//        val oat = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_TOKEN)
//        val oav = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_VERSION)
//        val titleTxt = URLEncoder.encode(photo_upload_title_txt.text.toString()).replace("+", "%20")
//        val title =RequestBody.create("text/plain".toMediaTypeOrNull(), photo_upload_title_txt.text.toString())
//        val sortParams = "oauth_consumer_key="+OAuthConstants.API_KEY+"&oauth_nonce="+nonce+"&oauth_signature_method="+OAuthConstants.OAUTH_SIG_METHOD+"&oauth_timestamp="+ts+"&oauth_token="+OAuthConstants.OAUTH_TOKEN+"&oauth_version="+OAuthConstants.OAUTH_VERSION+"&title="+titleTxt
//        val oabasetext = "POST&"+URLEncoder.encode(OAuthConstants.UPLOAD_BASE)+"&"+URLEncoder.encode(sortParams)
//        val oas = calcHmac(oabasetext,OAuthConstants.API_SECRET+OAuthConstants.OAUTH_TOKEN_SECRET)
//        val oasig = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), oas)
//        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), photoFile)
//        val image = MultipartBody.Part.createFormData("photo","photo.jpg",requestFile)
//
//        //Utilize Coroutine for async upload:
//        CoroutineScope(Dispatchers.IO).launch{
//            try{
//                val response = FlikrUploadApi.retrofitService.uploadPhoto(oack,oan,oasm,oats,oat,oav,oasig,title,image)
//                Log.i("Upload", "$response + ${response.body()}")
//                Log.i("Upload","${sortParams}")
//                Log.i("Upload","${oabasetext}")
//                Log.i("Upload2",URLEncoder.encode(titleTxt)+ titleTxt)
//                Snackbar.make(requireView(),"Upload Succesful!",Snackbar.LENGTH_INDEFINITE).setAction("View") {
//                    findNavController().navigate(R.id.userPhotosFragment)
//                }.show()
//            }catch(e: Exception){
//                Log.i("Upload","$e")
//            }
//
//        }
////        val token = CurrentUser.oa_token
////        Log.i("Uploader", "$token")
//    }

    private fun getPhotoFile(fileName : String): File{
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    //Intercept the captured image after the camera application finishes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(photoFile.absolutePath)
            capture_image.setImageBitmap(takenPhoto)
            upload_photo_bttn.visibility=View.VISIBLE
            photo_upload_title_txt.visibility=View.VISIBLE
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}