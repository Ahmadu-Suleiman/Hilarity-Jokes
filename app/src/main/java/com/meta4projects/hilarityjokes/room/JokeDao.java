package com.meta4projects.hilarityjokes.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JokeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJoke(Joke joke);

    @Update
    void updateJoke(Joke joke);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertJokes(List<Joke> jokes);

    @Delete
    void deleteJoke(Joke joke);

    @Query("SELECT * FROM joke WHERE joke_is_saved == 1")
    List<Joke> getSavedJokes();

    @Query("SELECT * FROM joke")
    List<Joke> getViewedJokes();
}
