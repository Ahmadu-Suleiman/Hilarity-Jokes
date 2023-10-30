package com.meta4projects.hilarityjokes.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface JokeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJoke(joke: Joke)

    @Update
    fun updateJoke(joke: Joke)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJokes(jokes: List<Joke>)

    @Delete
    fun deleteJoke(joke: Joke)

    @get:Query("SELECT * FROM joke WHERE joke_is_saved == 1")
    val savedJokes: List<Joke>

    @get:Query("SELECT * FROM joke")
    val viewedJokes: List<Joke>
}