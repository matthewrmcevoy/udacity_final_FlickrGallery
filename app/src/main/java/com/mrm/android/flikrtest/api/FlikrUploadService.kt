package com.mrm.android.flikrtest.api

import com.squareup.moshi.Moshi
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.io.File

private const val BASE_URL = "https://up.flickr.com/services/"
private const val FORMAT = "json"

private val moshi = Moshi.Builder()
    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
    .build()

private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FlikrUploadService {
    @Multipart
    @POST("upload")
    suspend fun uploadPhoto(@Part("oauth_consumer_key") oack : RequestBody,
                            @Part("oauth_nonce") oan : RequestBody,
                            @Part("oauth_signature_method") oasm : RequestBody,
                            @Part("oauth_timestamp") oats : RequestBody,
                            @Part("oauth_token") oat : RequestBody,
                            @Part("oauth_version") oav : RequestBody,
                            @Part("oauth_signature") oas : RequestBody,
                            @Part photo : MultipartBody.Part) : Response<String>
}

object FlikrUploadApi{
    val retrofitService: FlikrUploadService by lazy {retrofit.create(FlikrUploadService::class.java)}
}