package com.meta4projects.hilarityjokes.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Joke.class, version = 1, exportSchema = false)
public abstract class JokeDatabase extends RoomDatabase {

    private static volatile JokeDatabase INSTANCE;

    public static JokeDatabase getINSTANCE(final Context context) {
        if (INSTANCE == null) {
            synchronized (JokeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = getDataBase(context);
                }
            }
        }

        return INSTANCE;
    }

    private static JokeDatabase getDataBase(final Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                JokeDatabase.class, "joke_database").build();
    }

    public abstract JokeDao jokeDao();
}
