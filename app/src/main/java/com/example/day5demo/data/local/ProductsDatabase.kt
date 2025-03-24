package com.example.day5demo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.day5demo.model.Product

@Database(entities = [Product::class], version = 1)
abstract class ProductsDatabase : RoomDatabase() {

    abstract fun getDao(): ProductDao

    companion object {
        private var INSTANCE: ProductsDatabase? = null
        fun getInstance(context: Context): ProductsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, ProductsDatabase::class.java, "products_database")
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}