package ru.vinyarsky.testapplication.data

import retrofit2.http.GET
import ru.vinyarsky.testapplication.data.models.NetworkResponse

interface NetworkService {

    @GET("/test/test.json")
    suspend fun getProducts(): NetworkResponse
}