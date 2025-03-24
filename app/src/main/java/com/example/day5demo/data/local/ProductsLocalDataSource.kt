package com.example.day5demo.data.local

import com.example.day5demo.model.Product
import kotlinx.coroutines.flow.Flow

class ProductsLocalDataSource(private val dao: ProductDao) {

    suspend fun insertAll(products: List<Product>) {
        return dao.insertAll(products)
    }

     fun getProducts(): Flow<List<Product>> {
        return dao.getProducts()
    }

     fun getFavorites(): Flow<List<Product>> {
        return dao.getProducts()
    }

    suspend fun deleteFromFav(product: Product) {
        return dao.deleteProduct(product)
    }

    suspend fun addToFav(product: Product) {
        return dao.insertProduct(product)
    }
}