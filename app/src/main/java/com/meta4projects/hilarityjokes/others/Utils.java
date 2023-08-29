package com.meta4projects.hilarityjokes.others;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.room.Joke;
import com.meta4projects.hilarityjokes.room.JokeDatabase;
import com.meta4projects.hilarityjokes.room.Jokes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Utils {

    public static final String JOKE_EXTRA = "com.meta4projects.hilarityjokes.JokeExtra";

    public static final String endPointJoke = "https://icanhazdadjoke.com/";
    public static final String endPointJokeSearch = "https://icanhazdadjoke.com/search";

    private static OkHttpClient okHttpClient;

    private static Gson gson;

    public static Joke joke = new Joke();
    public static boolean isNotification;

    public static String getSearchEndpoint(String term, int page) {//todo query parameters
        return String.format("https://icanhazdadjoke.com/search?term=%s&limit=30&page=%s", term, page);
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
            okHttpClient = new OkHttpClient();
        return okHttpClient;
    }

    public static Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    public static Request getRequestJoke(Context context) {
        return new Request.Builder().url(endPointJoke)
                .addHeader("User-Agent", String.format("My App: http://play.google.com/store/apps/details?id=%s, My Email: ahmadumeta4.1@gmailcom", context.getPackageName()))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json").build();
    }

    public static Request getRequestJokes(Context context, String term, int page) {
        HttpUrl.Builder builderUrl = Objects.requireNonNull(HttpUrl.parse(endPointJokeSearch)).newBuilder()
                .addQueryParameter("term", term)
                .addQueryParameter("limit", "30")
                .addQueryParameter("page", String.valueOf(page));
        String url = builderUrl.toString();

        return new Request.Builder().url(url)
                .addHeader("User-Agent", String.format("My App: http://play.google.com/store/apps/details?id=%s, My Email: ahmadumeta4.1@gmailcom", context.getPackageName()))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json").build();
    }

    public static Joke getRandomJokeFromJson(Context context, String json) {
        JsonObject jsonObject = getGson().fromJson(json, JsonObject.class);
        String id = jsonObject.get("id").getAsString();
        String text = jsonObject.get("joke").getAsString();

        Joke joke = new Joke(id, text);
        AsyncTask.execute(() -> JokeDatabase.getINSTANCE(context).jokeDao().insertJoke(joke));
        return joke;
    }

    public static Jokes getJokesFromJson(Context context, String json) {
        JsonObject jsonObject = getGson().fromJson(json, JsonObject.class);
        int currentPage = jsonObject.get("current_page").getAsInt();
        int previousPage = jsonObject.get("previous_page").getAsInt();
        int nextPage = jsonObject.get("next_page").getAsInt();
        int totalPages = jsonObject.get("total_pages").getAsInt();

        JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();
        ArrayList<Joke> jokeList = new ArrayList<>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            JsonObject object = jsonElement.getAsJsonObject();
            String id = object.get("id").getAsString();
            String text = object.get("joke").getAsString();
            jokeList.add(new Joke(id, text));
        }

        Jokes jokes = new Jokes(currentPage, previousPage, nextPage, totalPages, jokeList);
        AsyncTask.execute(() -> JokeDatabase.getINSTANCE(context).jokeDao().insertJokes(jokeList));
        return jokes;
    }

    public static void shareText(Context context, String text) {
        new ShareCompat.IntentBuilder(context).setType("text/plain").setSubject("Hilarity Jokes").setChooserTitle("share using...").setText(text).startChooser();
    }

    public static String getDate(long milliseconds) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(milliseconds));
    }

    public static void showToast(String text, Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_custom_toast, activity.findViewById(R.id.toast_layout));

        TextView message = view.findViewById(R.id.toast_layout);
        message.setText(text);

        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void prepareJokeWorker(Context context) {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(JokeSuggestionWork.class, 6, TimeUnit.HOURS).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("SuggestionWork", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    public static AlertDialog getDialogView(Context context, View view) {
        return new AlertDialog.Builder(context).setView(view).create();
    }
}
