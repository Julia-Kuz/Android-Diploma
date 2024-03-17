package ru.netology.diploma.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.diploma.dto.Event


interface EventApiService {
    @GET("events")
    suspend fun getAll (@Header("Api-Key") apiKey: String) : Response<List<Event>>

    @POST("events/{id}/likes")
    suspend fun likeById(@Path("id") id: Int, @Header("Api-Key") apiKey: String): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Int, @Header("Api-Key") apiKey: String): Response<Event>

    @POST("events")
    suspend fun save(@Body event: Event, @Header("Api-Key") apiKey: String): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeById(@Path("id") id: Int, @Header("Api-Key") apiKey: String): Response<Unit>

}