@file:JvmName("ProductsActivityKt")

package com.example.day5demo.products.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.day5demo.R
import com.example.day5demo.data.local.ProductsDatabase
import com.example.day5demo.data.local.ProductsLocalDataSource
import com.example.day5demo.data.remote.ProductsRemoteDataSource
import com.example.day5demo.data.remote.RetrofitHelper
import com.example.day5demo.model.Product
import com.example.day5demo.model.Response
import com.example.day5demo.products.viewmodel.ProductsViewModel
import com.example.day5demo.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductsActivity : ComponentActivity() {
    private var productList = mutableStateOf<List<Product>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {

            val factory = ProductsViewModel.ProductsFactory(
                Repository.getInstance(
                    ProductsRemoteDataSource(RetrofitHelper.apiService),
                    ProductsLocalDataSource(ProductsDatabase.getInstance(this).getDao())
                )
            )
            val viewModel = ViewModelProvider(this, factory).get(ProductsViewModel::class.java)
            ProductsScreen(viewModel)
            checkConnectivity(viewModel)
        }


    }

    private fun checkConnectivity(viewModel: ProductsViewModel) {
        if (isNetworkAvailable()) {
//            fetchFromApi()
            viewModel.getProducts(true)
        } else {

//            fetchFromRoom()
            viewModel.getProducts(false)
        }
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

//    private fun fetchFromApi() {
//        val service = RetrofitHelper.apiService
//        lifecycleScope.launch(Dispatchers.IO) {
//            val productResponse = service.getProducts()
//            if (productResponse.isSuccessful) {
//                val dao = ProductsDatabase.getInstance(this@ProductsActivity).getDao()
//                dao.insertAll(productList.value)
//                withContext(Dispatchers.Main) {
//                    productList.value = dao.getProducts()
//
//                }
//            } else {
//                Log.e("TAG", "Error: " + productResponse.errorBody())
//            }
//
//        }
//    }
//
//    private fun fetchFromRoom() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val dao = ProductsDatabase.getInstance(this@ProductsActivity).getDao()
//            withContext(Dispatchers.Main) {
//                productList.value = dao.getProducts()
//            }
//        }
//    }
}


@Composable
fun ProductsScreen(viewModel: ProductsViewModel) {
    val productState = viewModel.products.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val messageState = viewModel.message.observeAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (productState.value) {
            is Response.Loading -> CircularProgressIndicator()
            is Response.Success -> {
                LazyColumn {
                    items((productState.value as Response.Success).list) { product ->
                        ProductItem(product) {
                            viewModel.addToFav(product)
                            Toast.makeText(
                                context,
                                "Added Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            is Response.Failure -> {
                Text(
                    text = "Error: ${(productState as Response.Failure).err.message}",
                    color = Color.Red
                )
            }
        }

    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductItem(product: Product, action: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize(), verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = product.thumbnail,
            contentDescription = "Product Image",
            modifier = Modifier.size(150.dp, 150.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = product.title, fontSize = 14.sp)
            Spacer(Modifier.padding(8.dp))
            Text(text = product.description, fontSize = 12.sp, maxLines = 2)
            Spacer(Modifier.padding(8.dp))
            Button(onClick = action) {
                Text(text = "Add To Favorites")
            }
        }
    }
}