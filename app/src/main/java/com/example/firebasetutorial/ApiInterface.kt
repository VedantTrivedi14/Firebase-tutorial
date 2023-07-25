package com.example.firebasetutorial

import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {

    @Headers("Authorization: Client-ID glaeMp9xw9jwDrSzy9oVG1WpLzXZpzE7SdPkCXvxeZs")
    @GET("/photos")
    fun getData() : Call<List<Photos>>
}