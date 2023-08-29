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

public class SavedJokeAdapter extends RecyclerView.Adapter<SavedJokeAdapter.SavedJokeHolder> {

    private final List<Joke> jokes;
    private final Context context;

    public SavedJokeAdapter(List<Joke> jokes, Context context) {
        this.jokes = jokes;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedJokeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_joke_layout, parent, false);
        return new SavedJokeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedJokeHolder holder, int position) {
        Joke joke = jokes.get(holder.getBindingAdapterPosition());
        holder.textViewDate.setText(getDate(joke.getDate()));
        holder.textViewJoke.setText(joke.getText());
        holder.textViewJoke.setOnClickListener(v -> context.startActivity(new Intent(context, JokeActivity.class).putExtra(JOKE_EXTRA, joke.getText())));
        holder.imageViewDelete.setOnClickListener(v -> {
            AsyncTask.execute(() -> JokeDatabase.getINSTANCE(context).jokeDao().deleteJoke(joke));
            jokes.remove(holder.getBindingAdapterPosition());
            notifyItemRemoved(holder.getBindingAdapterPosition());
            Utils.showToast("joke deleted", (Activity) context);
        });
        holder.imageViewShare.setOnClickListener(v -> Utils.shareText(context, joke.getText()));
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }

    static class SavedJokeHolder extends RecyclerView.ViewHolder {

        final TextView textViewDate;
        final TextView textViewJoke;
        final ImageView imageViewDelete;
        final ImageView imageViewShare;

        public SavedJokeHolder(@NonNull View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textview_date);
            textViewJoke = itemView.findViewById(R.id.textview_joke);
            imageViewDelete = itemView.findViewById(R.id.imageview_delete_joke);
            imageViewShare = itemView.findViewById(R.id.imageview_share_joke);
        }
    }
}
