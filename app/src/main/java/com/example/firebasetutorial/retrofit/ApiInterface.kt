package com.example.firebasetutorial.retrofit

import com.example.firebasetutorial.model.Photos
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterface {

    @Headers("Authorization: Client-ID glaeMp9xw9jwDrSzy9oVG1WpLzXZpzE7SdPkCXvxeZs")
    @GET("/photos")
    fun getData() : Call<List<Photos>>
}