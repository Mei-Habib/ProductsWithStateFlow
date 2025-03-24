package com.example.day5demo.products.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.day5demo.model.Product
import com.example.day5demo.model.Response
import com.example.day5demo.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductsViewModel(private val repo: Repository) : ViewModel() {

    init {
        getProducts(true)
    }

    // Backing property
    private var _products = MutableStateFlow<Response>(Response.Loading)
    var products = _products.asStateFlow()

    private val _message: MutableLiveData<String> = MutableLiveData("")
    val message: LiveData<String> = _message

    fun getProducts(isOnline: Boolean) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.getProducts()
                    .catch { _products.value = Response.Failure(Throwable("Error")) }
                    .collect { list ->
                        _products.value = Response.Success(list)
                    }
            }
        } catch (e: Exception) {
            _message.postValue(e.message)
        }
    }

    fun addToFav(product: Product) {
        viewModelScope.launch {
            try {
                repo.addToFav(product)
                _message.postValue("Added Successfully")
            } catch (e: Exception) {
                _message.postValue(e.message)
            }
        }
    }

    class ProductsFactory(private val repo: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductsViewModel(repo) as T
        }
    }
}