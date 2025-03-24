package com.example.day5demo.repo

import com.example.day5demo.data.local.ProductsLocalDataSource
import com.example.day5demo.data.remote.ProductsRemoteDataSource
import com.example.day5demo.model.Product
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private val remoteDataSource: ProductsRemoteDataSource,
    private val localDataSource: ProductsLocalDataSource
) {
    suspend fun insertAll(products: List<Product>) {
        return localDataSource.insertAll(products)
    }

    fun getProducts(isOnline: Boolean = true): Flow<List<Product>> {
//        if (isOnline) {
//            if (remoteDataSource.getProducts().isSuccessful) {
        return remoteDataSource.getProducts()
//            }
//        }
        return localDataSource.getProducts()
    }

    fun getFavorites(): Flow<List<Product>> {
        return localDataSource.getFavorites()
    }

    suspend fun addToFav(product: Product) {
        return localDataSource.addToFav(product)
    }

    suspend fun deleteFromFav(product: Product) {
        return localDataSource.deleteFromFav(product)
    }

    companion object {
        private var repository: Repository? = null
        fun getInstance(
            remoteDataSource: ProductsRemoteDataSource,
            localDataSource: ProductsLocalDataSource
        ): Repository {

            return repository ?: synchronized(this) {
                val repo = Repository(remoteDataSource, localDataSource)
                repository = repo
                repo
            }
        }
    }
}