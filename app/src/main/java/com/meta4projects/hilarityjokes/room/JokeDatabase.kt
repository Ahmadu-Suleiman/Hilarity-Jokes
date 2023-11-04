package com.meta4projects.hilarityjokes.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Joke::class], version = 2, exportSchema = true)
abstract class JokeDatabase : RoomDatabase() {
    abstract fun jokeDao(): JokeDao

    companion object {
        @Volatile
        private var INSTANCE: JokeDatabase? = null
        fun getINSTANCE(context: Context): JokeDatabase {
            return INSTANCE ?: synchronized(JokeDatabase::class.java) {
                val instance = getDataBase(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

private fun getDataBase(context: Context): JokeDatabase {
    return databaseBuilder(context.applicationContext, JokeDatabase::class.java, "joke_database").build()
}
