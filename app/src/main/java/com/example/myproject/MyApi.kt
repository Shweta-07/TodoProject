package com.example.myproject

import retrofit2.Response
import retrofit2.http.GET

interface MyApi {

    @GET("/todos")
    suspend fun getTodos(): Response<List<Todo>>

}