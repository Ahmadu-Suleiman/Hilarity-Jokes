package com.meta4projects.hilarityjokes.fragments;

import static com.meta4projects.hilarityjokes.others.Utils.isNotification;
import static com.meta4projects.hilarityjokes.others.Utils.joke;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.others.Utils;
import com.meta4projects.hilarityjokes.room.JokeDatabase;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private TextView textviewRandomJoke;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView imageViewSave = view.findViewById(R.id.imageview_save_joke);
        ImageView imageViewShare = view.findViewById(R.id.imageview_share_joke);
        textviewRandomJoke = view.findViewById(R.id.textview_random_joke);
        AppCompatButton buttonShowRandomJoke = view.findViewById(R.id.button_show_random_joke);

        if (isNotification) {
            isNotification = false;
            textviewRandomJoke.setText(joke.getText());
        } else
            setRandomJoke(false);

        imageViewSave.setOnClickListener(v -> {
            joke.setSaved(true);
            AsyncTask.execute(() -> JokeDatabase.getINSTANCE(requireContext()).jokeDao().insertJoke(joke));
            Utils.showToast("joke saved", requireActivity());
        });

        imageViewShare.setOnClickListener(v -> Utils.shareText(requireContext(), joke.getText()));
        buttonShowRandomJoke.setOnClickListener(v -> setRandomJoke(true));
        return view;
    }

    private void setRandomJoke(boolean showToast) {
        try {
            Request requestJoke = Utils.getRequestJoke(requireContext());
            Utils.getOkHttpClient().newCall(requestJoke).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (showToast)
                        requireActivity().runOnUiThread(() -> Utils.showToast("could not fetch a random joke, try again", requireActivity()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        joke = Utils.getRandomJokeFromJson(requireContext(), json);

                        requireActivity().runOnUiThread(() -> textviewRandomJoke.setText(joke.getText()));
                        response.close();
                    } else {
                        if (showToast)
                            requireActivity().runOnUiThread(() -> Utils.showToast("could not fetch a random joke, try again", requireActivity()));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}