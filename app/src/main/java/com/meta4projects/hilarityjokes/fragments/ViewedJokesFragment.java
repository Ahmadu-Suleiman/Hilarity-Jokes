package com.meta4projects.hilarityjokes.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.adapters.ViewedJokeAdapter;
import com.meta4projects.hilarityjokes.room.Joke;
import com.meta4projects.hilarityjokes.room.JokeDatabase;

import java.util.ArrayList;

public class ViewedJokesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewed_jokes, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.viewed_jokes_recyclerview);
        TextView textViewNoJokes = view.findViewById(R.id.text_no_viewed_jokes);

        AsyncTask.execute(() -> {
            ArrayList<Joke> jokesViewed = new ArrayList<>(JokeDatabase.getINSTANCE(requireContext()).jokeDao().getViewedJokes());

            requireActivity().runOnUiThread(() -> {
                ViewedJokeAdapter viewedJokeAdapter = new ViewedJokeAdapter(jokesViewed, requireContext());
                recyclerView.setAdapter(viewedJokeAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
                if (jokesViewed.isEmpty()) textViewNoJokes.setVisibility(View.VISIBLE);
            });
        });
        return view;
    }
}