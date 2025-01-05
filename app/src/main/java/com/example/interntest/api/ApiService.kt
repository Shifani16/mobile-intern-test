package com.example.interntest.api

import com.example.interntest.data.PeopleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    suspend fun getPeople(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<PeopleResponse>
}
