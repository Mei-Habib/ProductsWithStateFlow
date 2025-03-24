package com.example.day5demo.favProducts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.day5demo.model.Product
import com.example.day5demo.model.Response
import com.example.day5demo.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavViewModel(private val repo: Repository) : ViewModel() {

    init {
        getStoredProducts()
    }

    private val _favorites = MutableStateFlow<Response>(Response.Loading)
    val favorites = _favorites.asStateFlow()

    private val _message: MutableLiveData<String> = MutableLiveData("")
    val message: LiveData<String> = _message


    private fun getStoredProducts() {
        viewModelScope.launch {
            try {
                repo.getFavorites()
                    .catch { _favorites.value = Response.Failure(Throwable("Error")) }
                    .collect { list ->
                        _favorites.value = Response.Success(list)
                    }
            } catch (e: Exception) {
                _message.postValue(e.message)
            }
        }
    }

    fun deleteFromFav(product: Product) {
        viewModelScope.launch {
            try {
                repo.deleteFromFav(product)
                _message.postValue("Deleted Successfully")
//                repo.getFavorites().collect { list ->
//                    _favorites.value = Response.Success(list)
//                }
            } catch (e: Exception) {
                _message.postValue(e.message)
            }
        }
    }

    class ProductsFactory(private val repo: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavViewModel(repo) as T
        }
    }
}