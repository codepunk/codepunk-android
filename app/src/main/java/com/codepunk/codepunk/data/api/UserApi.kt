package com.codepunk.codepunk.data.api

import com.codepunk.codepunk.data.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface UserApi {

    @Headers("Accept: application/json")
    @GET("api/user")
    fun getUser(): Call<User>

}