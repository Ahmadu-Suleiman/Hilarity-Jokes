package com.meta4projects.hilarityjokes.adapters;

import static com.meta4projects.hilarityjokes.others.Utils.JOKE_EXTRA;
import static com.meta4projects.hilarityjokes.others.Utils.getDate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.others.Utils;
import com.meta4projects.hilarityjokes.activities.JokeActivity;
import com.meta4projects.hilarityjokes.room.Joke;
import com.meta4projects.hilarityjokes.room.JokeDatabase;

import java.util.List;

public class ViewedJokeAdapter extends RecyclerView.Adapter<ViewedJokeAdapter.ViewedJokeHolder> {

    private final List<Joke> jokes;
    private final Context context;

    public ViewedJokeAdapter(List<Joke> jokes, Context context) {
        this.jokes = jokes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewedJokeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewed_joke_layout, parent, false);
        return new ViewedJokeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewedJokeHolder holder, int position) {
        Joke joke = jokes.get(holder.getBindingAdapterPosition());
        holder.textViewDate.setText(getDate(joke.getDate()));
        holder.textViewJoke.setText(joke.getText());
        holder.textViewJoke.setOnClickListener(v -> context.startActivity(new Intent(context, JokeActivity.class).putExtra(JOKE_EXTRA, joke.getText())));
        holder.imageViewSave.setOnClickListener(v -> {
            joke.setSaved(true);
            joke.setDate(System.currentTimeMillis());
            AsyncTask.execute(() -> JokeDatabase.getINSTANCE(context).jokeDao().updateJoke(joke));
            Utils.showToast("joke saved", (Activity) context);
        });
        holder.imageViewShare.setOnClickListener(v -> Utils.shareText(context, joke.getText()));
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }

    static class ViewedJokeHolder extends RecyclerView.ViewHolder {

        final TextView textViewDate;
        final TextView textViewJoke;
        final ImageView imageViewSave;
        final ImageView imageViewShare;

        public ViewedJokeHolder(@NonNull View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textview_date);
            textViewJoke = itemView.findViewById(R.id.textview_joke);
            imageViewSave = itemView.findViewById(R.id.imageview_save_joke);
            imageViewShare = itemView.findViewById(R.id.imageview_share_joke);
        }
    }
}
