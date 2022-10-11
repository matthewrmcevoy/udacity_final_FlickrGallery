package com.mrm.android.flikrtest.api

import com.squareup.moshi.Moshi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.flickr.com/services/feeds/"
private const val FORMAT = "json"

private val moshi = Moshi.Builder()
    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FlikrAPIService {
    @GET("photos_public.gne?format=json&nojsoncallback=1")
    suspend fun getPhotos(@Query("tags") type: String): Response<String>
}

object FlikrApi{
    val retrofitService: FlikrAPIService by lazy {retrofit.create(FlikrAPIService::class.java)}
}