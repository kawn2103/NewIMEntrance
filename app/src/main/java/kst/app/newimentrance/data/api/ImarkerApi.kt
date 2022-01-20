package kst.app.newimentrance.data.api

import kst.app.newimentrance.data.entity.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ImarkerApi {

    // 로그인
   @POST("/v3/2d584a07-7325-4b82-bbf7-d9d41a5f1fb4")
    suspend fun login(@Body data: Map<String,String>): Response<LoginResponse>

}