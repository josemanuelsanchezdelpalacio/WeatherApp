package com.example.weatherapp.services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {
    @GET("posts")
    suspend fun getPosts(): List <Post>
}

object RetrofitServiceFactory {
    fun makeRetrofitService(): RetrofitService {
        return Retrofit
            .Builder()
            .baseUrl("https://private-f73aae-jmsdam2.apiary-mock.com/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build().create(RetrofitService::class.java)
    }
}
