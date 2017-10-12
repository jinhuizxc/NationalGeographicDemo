package com.example.jh.nationalgeographic.data

import android.arch.lifecycle.LiveData
import com.example.jh.nationalgeographic.data.model.Detail
import com.example.jh.nationalgeographic.data.model.Item
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by wheat7 on 2017/9/12.
 */


interface Webservice {

    @GET("mains/p{index}.html")
    fun getItem(@Path("index") index : Int): Call<Item>


    @GET("albums/a{id}.html")
    fun getDetail(@Path("id") id : String): Call<Detail>
}