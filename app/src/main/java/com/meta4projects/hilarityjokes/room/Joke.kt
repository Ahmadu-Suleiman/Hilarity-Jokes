package com.meta4projects.hilarityjokes.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "joke")
class Joke {
    @JvmField
    @ColumnInfo(name = "joke_primary_id")
    @PrimaryKey
    var id: String

    @JvmField
    @ColumnInfo(name = "joke_date")
    var date: Long

    @JvmField
    @ColumnInfo(name = "joke_text")
    var text: String

    @JvmField
    @ColumnInfo(name = "joke_is_saved")
    var isSaved = false

    constructor() {
        id = UUID.randomUUID().toString()
        date = System.currentTimeMillis()
        text = "insert very funny joke here…"
    }

    @Ignore
    constructor(id: String, text: String) {
        this.id = id
        date = System.currentTimeMillis()
        this.text = text
    }
}