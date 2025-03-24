package com.example.day5demo.favProducts.view

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.day5demo.R
import com.example.day5demo.data.local.ProductsDatabase
import com.example.day5demo.data.local.ProductsLocalDataSource
import com.example.day5demo.data.remote.ProductsRemoteDataSource
import com.example.day5demo.data.remote.RetrofitHelper
import com.example.day5demo.favProducts.viewmodel.FavViewModel
import com.example.day5demo.model.Product
import com.example.day5demo.model.Response
import com.example.day5demo.repo.Repository

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val factory = FavViewModel.ProductsFactory(
                Repository.getInstance(
                    ProductsRemoteDataSource(RetrofitHelper.apiService),
                    ProductsLocalDataSource(
                        ProductsDatabase.getInstance(this@FavoritesActivity).getDao()
                    )
                )
            )

            FavoritesScreen(
                ViewModelProvider(this@FavoritesActivity, factory).get(FavViewModel::class.java)
            )
        }
    }
}

@Composable
private fun FavoritesScreen(viewModel: FavViewModel) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val productState = viewModel.favorites.collectAsStateWithLifecycle()
        val messageState = viewModel.message.observeAsState()

        when (productState.value) {
            is Response.Loading -> CircularProgressIndicator()
            is Response.Success -> {
                LazyColumn {
                    items((productState.value as Response.Success).list) { product ->
                        FavoriteItem(product) {
                            viewModel.deleteFromFav(product)
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT)
                                .show()
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
fun FavoriteItem(product: Product, action: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize(), verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = product.thumbnail,
            contentDescription = stringResource(R.string.product_image),
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
                Text(text = stringResource(R.string.remove_from_favorites))
            }
        }
    }
}