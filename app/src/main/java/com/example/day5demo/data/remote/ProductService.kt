package com.example.day5demo.data.remote

import com.example.day5demo.model.Product
import com.example.day5demo.model.ProductResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://dummyjson.com/"

interface ProductService {
    @GET("products")
    suspend fun getProducts(): Response<ProductResponse>
}

object RetrofitHelper {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ProductService::class.java)
}