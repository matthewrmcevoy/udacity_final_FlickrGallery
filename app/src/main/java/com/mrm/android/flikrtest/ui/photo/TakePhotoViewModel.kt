package com.mrm.android.flikrtest.ui.photo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrm.android.flikrtest.R
import com.mrm.android.flikrtest.api.FlikrUploadApi
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

enum class UPLOAD_STATUS{UPLOADING, DONE,SUCCESS, ERROR}
class TakePhotoViewModel : ViewModel() {
    val status = MutableLiveData<UPLOAD_STATUS>()
    val clearEvent = MutableLiveData<Boolean>()

    init{
        status.value = UPLOAD_STATUS.DONE
        clearEvent.value = false
    }

    fun uploadFile(file: File, phototitle: String) : Boolean{
        status.value = UPLOAD_STATUS.UPLOADING
        //establish all the components required for the multipart upload request
        val oack = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.API_KEY)
        val nonce = Random.nextInt(0, 99999999).toString()
        val oan = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nonce)
        val oasm = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_SIG_METHOD)
        val ts = (System.currentTimeMillis()/1000).toString()
        val oats = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ts)
        val oat = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_TOKEN)
        val oav = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), OAuthConstants.OAUTH_VERSION)
        val titleTxt = URLEncoder.encode(phototitle).replace("+", "%20")
        val title = RequestBody.create("text/plain".toMediaTypeOrNull(), phototitle)
        val sortParams = "oauth_consumer_key="+ OAuthConstants.API_KEY+"&oauth_nonce="+nonce+"&oauth_signature_method="+ OAuthConstants.OAUTH_SIG_METHOD+"&oauth_timestamp="+ts+"&oauth_token="+ OAuthConstants.OAUTH_TOKEN+"&oauth_version="+ OAuthConstants.OAUTH_VERSION+"&title="+titleTxt
        val oabasetext = "POST&"+ URLEncoder.encode(OAuthConstants.UPLOAD_BASE)+"&"+ URLEncoder.encode(sortParams)
        val oas = calcHmac(oabasetext, OAuthConstants.API_SECRET+ OAuthConstants.OAUTH_TOKEN_SECRET)
        val oasig = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), oas)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val image = MultipartBody.Part.createFormData("photo","photo.jpg",requestFile)
        var success:Boolean = true
        //Utilize Coroutine for async upload:
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val response = FlikrUploadApi.retrofitService.uploadPhoto(oack,oan,oasm,oats,oat,oav,oasig,title,image)
                Log.i("Upload", "$response + ${response.body()}")
                Log.i("Upload","${sortParams}")
                Log.i("Upload","${oabasetext}")
                Log.i("Upload2", URLEncoder.encode(titleTxt)+ titleTxt)
                status.postValue(UPLOAD_STATUS.SUCCESS)
                clearEvent.postValue(true)
                success= true
            }catch(e: Exception){
                Log.i("Upload","$e")
                status.postValue(UPLOAD_STATUS.ERROR)
                success = false
            }
        }
        Log.i("TakePhotoViewModel","$success")
        clearEvent.value = false
        return success
    }
}