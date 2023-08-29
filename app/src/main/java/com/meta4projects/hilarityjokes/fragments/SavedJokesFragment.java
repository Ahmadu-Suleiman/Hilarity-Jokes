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
import com.meta4projects.hilarityjokes.adapters.SavedJokeAdapter;
import com.meta4projects.hilarityjokes.room.Joke;
import com.meta4projects.hilarityjokes.room.JokeDatabase;

import java.util.ArrayList;

public class SavedJokesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_jokes, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.saved_jokes_recyclerview);
        TextView textViewNoJokes = view.findViewById(R.id.text_no_saved_jokes);

        AsyncTask.execute(() -> {
            ArrayList<Joke> jokesSaved = new ArrayList<>(JokeDatabase.getINSTANCE(requireContext()).jokeDao().getSavedJokes());

            requireActivity().runOnUiThread(() -> {
                SavedJokeAdapter savedJokeAdapter = new SavedJokeAdapter(jokesSaved, requireContext());
                recyclerView.setAdapter(savedJokeAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
                if (jokesSaved.isEmpty()) textViewNoJokes.setVisibility(View.VISIBLE);
            });
        });
        return view;
    }
}