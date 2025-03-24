package com.example.day5demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.day5demo.favProducts.view.FavoritesActivity
import com.example.day5demo.products.view.ProductsActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val activity = (LocalContext.current) as Activity
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val intent = Intent(activity, ProductsActivity::class.java)
            activity.startActivity(intent)
        }) {
            Text(text = "Get all products")
        }

        Button(onClick = {
            val intent = Intent(activity, FavoritesActivity::class.java)
            activity.startActivity(intent)
        }) {
            Text(text = "get favorite products")
        }

        Button(onClick = {
            activity.finish()
        }) {
            Text(text = stringResource(R.string.exit))
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}