package com.meta4projects.hilarityjokes.activities;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.meta4projects.hilarityjokes.others.Utils.getDialogView;
import static com.meta4projects.hilarityjokes.others.Utils.prepareJokeWorker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.meta4projects.hilarityjokes.R;
import com.meta4projects.hilarityjokes.fragments.HomeFragment;
import com.meta4projects.hilarityjokes.fragments.SavedJokesFragment;
import com.meta4projects.hilarityjokes.fragments.ViewedJokesFragment;

public class MainActivity extends AppCompatActivity {

    private ReviewInfo reviewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment(new HomeFragment());

        ImageView imageViewSearch = findViewById(R.id.search_jokes);
        ImageView imageViewToggle = findViewById(R.id.nav_toggle);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).getSubMenu().getItem(0).setActionView(R.layout.menu_image_ad);

        imageViewSearch.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        imageViewToggle.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START, true);
            else drawer.openDrawer(GravityCompat.START, true);
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) setFragment(new HomeFragment());
            else if (id == R.id.nav_saved) setFragment(new SavedJokesFragment());
            else if (id == R.id.nav_viewed) setFragment(new ViewedJokesFragment());
            else if (id == R.id.apps) showApps();
            else if (id == R.id.about) showAboutDialog();
            else if (id == R.id.rate) rate();
            else if (id == R.id.share_app) shareApp();
            drawer.closeDrawer(GravityCompat.START, true);
            return true;
        });
        updateAndReview();
        prepareJokeWorker(this);
    }

    private void updateAndReview() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        ReviewManager reviewManager = ReviewManagerFactory.create(this);

        //update
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE))
                appUpdateManager.startUpdateFlow(appUpdateInfo, this, AppUpdateOptions.newBuilder(IMMEDIATE).setAllowAssetPackDeletion(true).build());
            else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                appUpdateManager.startUpdateFlow(appUpdateInfo, this, AppUpdateOptions.newBuilder(IMMEDIATE).setAllowAssetPackDeletion(true).build());
        });

        //review
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) reviewInfo = task.getResult();
        });
        if (reviewInfo != null) reviewManager.launchReviewFlow(this, reviewInfo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setFragment(new HomeFragment());
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.layout_container, fragment).commit();
    }

    private void showApps() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5382562347439530585")));
    }

    private void rate() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp() {
        String message = "\nI'm recommending this impressive joke app, give a try!".concat("\n").concat("\n").concat("https//play.google.com/store/apps/details?id=").concat(getPackageName()).concat("\n");

        new ShareCompat.IntentBuilder(this).setType("text/plain").setSubject(getString(R.string.app_name)).setChooserTitle("share using...").setText(message).startChooser();
    }

    private void showAboutDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_about, findViewById(R.id.about_dialog), false);
        final AlertDialog dialogAbout = getDialogView(this, view);
        dialogAbout.show();
    }
}