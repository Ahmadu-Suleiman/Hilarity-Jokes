package com.meta4projects.hilarityjokes.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.others.Utils;

public class JokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        String text = getIntent().getStringExtra(Utils.JOKE_EXTRA);
        TextView textViewJoke = findViewById(R.id.textview_joke);
        textViewJoke.setText(text);
    }
}