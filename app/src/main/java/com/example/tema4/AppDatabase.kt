package com.example.tema4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//baza de date
@Database(entities = [Tag::class, Image::class], version = 13)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tagDao(): TagDao
    abstract fun imageDao(): ImageDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
//legatura multi-multi inttre cele doua tabele
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }

        }
    }
}
