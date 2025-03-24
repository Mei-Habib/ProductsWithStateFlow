package com.example.day5demo.model

sealed class Response {
    data object Loading : Response()
    data class Success(val list: List<Product>) : Response()
    data class Failure(val err: Throwable) : Response()
}