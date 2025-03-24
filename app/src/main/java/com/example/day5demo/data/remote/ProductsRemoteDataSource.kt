package com.example.day5demo.data.remote

import com.example.day5demo.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRemoteDataSource(private val service: ProductService) {
    fun getProducts(): Flow<List<Product>> = flow {
        val response = service.getProducts()
        if (response.isSuccessful) {
            response.body()?.products?.let { list ->
                emit(list)
            }
        }
    }
}