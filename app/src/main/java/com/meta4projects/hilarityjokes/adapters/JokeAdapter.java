package com.meta4projects.hilarityjokes.adapters;

import static com.meta4projects.hilarityjokes.others.Utils.JOKE_EXTRA;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.activities.JokeActivity;
import com.meta4projects.hilarityjokes.room.Joke;

import java.util.ArrayList;

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeHolder> {

    private final Context context;
    private ArrayList<Joke> jokes;

    public JokeAdapter(ArrayList<Joke> jokes, Context context) {
        this.jokes = jokes;
        this.context = context;
    }

    @NonNull
    @Override
    public JokeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joke_layout, parent, false);
        return new JokeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JokeHolder holder, int position) {
        Joke joke = jokes.get(holder.getBindingAdapterPosition());
        holder.textViewJoke.setText(joke.getText());
        holder.textViewJoke.setOnClickListener(v -> context.startActivity(new Intent(context, JokeActivity.class).putExtra(JOKE_EXTRA, joke.getText())));
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }

    public void setJokes(ArrayList<Joke> jokes) {
        this.jokes = jokes;
    }

    static class JokeHolder extends RecyclerView.ViewHolder {

        final TextView textViewJoke;

        public JokeHolder(@NonNull View itemView) {
            super(itemView);
            textViewJoke = itemView.findViewById(R.id.textview_joke);
        }
    }
}
