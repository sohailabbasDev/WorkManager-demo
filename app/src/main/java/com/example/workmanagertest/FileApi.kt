package com.example.workmanagertest

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

//Api instance that has method that gets the api
interface FileApi {

    // api method to get image
    @GET("/photo-1651938409309-ecf9971aca9f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTY1MjM5ODMzNg&ixlib=rb-1.2.1&q=80&w=1080")
    suspend fun getImage():Response<ResponseBody>


    companion object{
        //retrofit lazy instance
      val  instance by lazy{
          Retrofit.Builder(
          ).baseUrl("https://images.unsplash.com/").build().create(FileApi::class.java)
        }
    }
}