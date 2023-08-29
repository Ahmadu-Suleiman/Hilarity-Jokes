package com.meta4projects.hilarityjokes.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.others.Utils;
import com.meta4projects.hilarityjokes.adapters.JokeAdapter;
import com.meta4projects.hilarityjokes.room.Jokes;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("NotifyDataSetChanged")
public class SearchActivity extends AppCompatActivity {

    private String searchTerm = "";
    private Jokes jokes = new Jokes();
    private TextView textViewNoJokes;
    private TextView textViewPage;
    private JokeAdapter jokeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText editTextSearch = findViewById(R.id.search_jokes);
        RecyclerView recyclerView = findViewById(R.id.searched_jokes_recyclerview);
        textViewNoJokes = findViewById(R.id.text_no_searched_jokes);
        textViewPage = findViewById(R.id.textview_page);
        ImageView imageViewPrevious = findViewById(R.id.imageview_search_previous);
        ImageView imageViewNext = findViewById(R.id.imageview_search_next);

        jokeAdapter = new JokeAdapter(jokes.getJokes(), this);
        recyclerView.setAdapter(jokeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        setJokes(1);

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTerm = editTextSearch.getText().toString();
                setJokes(1);
                return true;
            }
            return false;
        });

        imageViewPrevious.setOnClickListener(v -> {
            if (jokes.getCurrentPage() != jokes.getPreviousPage())
                setJokes(jokes.getPreviousPage());
        });

        imageViewNext.setOnClickListener(v -> {
            if (jokes.getCurrentPage() != jokes.getNextPage())
                setJokes(jokes.getNextPage());
        });

        if (jokes.getJokes().isEmpty()) textViewNoJokes.setVisibility(View.VISIBLE);
    }

    private void setJokes(int page) {
        Request requestJoke = Utils.getRequestJokes(SearchActivity.this, searchTerm, page);
        Utils.getOkHttpClient().newCall(requestJoke).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Utils.showToast("error, try again", SearchActivity.this));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    jokes = Utils.getJokesFromJson(SearchActivity.this, json);

                    runOnUiThread(() -> {
                        textViewPage.setText(getPage(jokes));
                        jokeAdapter.setJokes(jokes.getJokes());
                        jokeAdapter.notifyDataSetChanged();

                        if (jokes.getJokes().isEmpty()) textViewNoJokes.setVisibility(View.VISIBLE);
                        else textViewNoJokes.setVisibility(View.GONE);
                    });
                    response.close();
                } else {
                    runOnUiThread(() -> Utils.showToast("error, try again", SearchActivity.this));
                }
            }
        });
    }

    private String getPage(Jokes jokes) {
        return String.format("%s/%s", jokes.getCurrentPage(), jokes.getTotalPages());
    }
}