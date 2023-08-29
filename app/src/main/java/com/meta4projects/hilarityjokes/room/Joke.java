package com.meta4projects.hilarityjokes.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "joke")
public class Joke {

    @NonNull
    @ColumnInfo(name = "joke_primary_id")
    @PrimaryKey()
    private String id;

    @ColumnInfo(name = "joke_date")
    private long date;

    @ColumnInfo(name = "joke_text")
    private String text;

    @ColumnInfo(name = "joke_is_saved")
    private boolean isSaved;

    public Joke() {
        this.id = UUID.randomUUID().toString();
        this.date = System.currentTimeMillis();
        this.text = "insert very funny joke here…";
    }

    @Ignore
    public Joke(@NonNull String id, String text) {
        this.id = id;
        this.date = System.currentTimeMillis();
        this.text = text;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
