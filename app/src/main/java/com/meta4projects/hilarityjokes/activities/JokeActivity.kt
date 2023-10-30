package com.meta4projects.hilarityjokes.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.others.Utils

class JokeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joke)
        val text = intent.getStringExtra(Utils.JOKE_EXTRA)
        val textViewJoke = findViewById<TextView>(R.id.textview_joke)
        textViewJoke.text = text
    }
}