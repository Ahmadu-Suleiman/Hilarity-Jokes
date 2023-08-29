package com.meta4projects.hilarityjokes.others;

import static com.meta4projects.hilarityjokes.others.Utils.isNotification;
import static com.meta4projects.hilarityjokes.others.Utils.joke;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.activities.MainActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class JokeSuggestionWork extends Worker {

    public static final int SUGGESTION_NOTIFICATION_ID = 980;
    public static final String SUGGESTION_CHANNEL_ID = "suggestion_channel_id";

    public JokeSuggestionWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Request requestJoke = Utils.getRequestJoke(getApplicationContext());
            Utils.getOkHttpClient().newCall(requestJoke).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        joke = Utils.getRandomJokeFromJson(getApplicationContext(), json);
                        isNotification = true;
                        response.close();

                        Intent wordIntent = new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, wordIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationChannel notificationChannel = new NotificationChannel(SUGGESTION_CHANNEL_ID, "Joke Suggestion", NotificationManager.IMPORTANCE_HIGH);
                        manager.createNotificationChannel(notificationChannel);

                        Notification wordNotification = new NotificationCompat.Builder(getApplicationContext(), SUGGESTION_CHANNEL_ID).setSmallIcon(R.drawable.ic_joke)
                                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_joke))
                                .setContentTitle("Joke Suggestion").setContentText(joke.getText()).setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setAutoCancel(true).setOnlyAlertOnce(true)
                                .setContentIntent(pendingIntent).build();
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                            NotificationManagerCompat.from(getApplicationContext()).notify(SUGGESTION_NOTIFICATION_ID, wordNotification);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success();
    }
}
