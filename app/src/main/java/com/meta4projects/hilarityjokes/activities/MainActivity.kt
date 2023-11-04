package com.meta4projects.hilarityjokes.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.fragments.HomeFragment
import com.meta4projects.hilarityjokes.fragments.SavedJokesFragment
import com.meta4projects.hilarityjokes.fragments.ViewedJokesFragment
import com.meta4projects.hilarityjokes.others.Utils
import com.meta4projects.hilarityjokes.others.Utils.getDialogView
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private var reviewInfo: ReviewInfo? = null
    private lateinit var consentInformation: ConsentInformation
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var interstitialAdSaved: InterstitialAd? = null
    private var interstitialAdViewed: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragment(HomeFragment())
        val imageViewSearch = findViewById<ImageView>(R.id.search_jokes)
        val imageViewToggle = findViewById<ImageView>(R.id.nav_toggle)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.getItem(3).subMenu!!.getItem(0).setActionView(R.layout.menu_image_ad)
        imageViewSearch.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
        imageViewToggle.setOnClickListener { if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START, true) else drawer.openDrawer(GravityCompat.START, true) }
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> setFragment(HomeFragment())
                R.id.nav_saved -> if (interstitialAdSaved != null) interstitialAdSaved!!.show(this@MainActivity) else setFragment(SavedJokesFragment())
                R.id.nav_viewed -> if (interstitialAdViewed != null) interstitialAdViewed!!.show(this@MainActivity) else setFragment(ViewedJokesFragment())
                R.id.apps -> showApps()
                R.id.about -> showAboutDialog()
                R.id.rate -> rate()
                R.id.share_app -> shareApp()
            }
            drawer.closeDrawer(GravityCompat.START, true)
            true
        }
        updateAndReview()
        consentForAds()
        loadInterstitialSaved()
        loadInterstitialViewed()
    }

    private fun consentForAds() {
        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params, {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { loadAndShowError ->
                // Consent gathering failed.
                if (loadAndShowError != null) {
                    Log.w(Utils.TAG, String.format("%s: %s", loadAndShowError.errorCode, loadAndShowError.message))
                }

                // Consent has been gathered.
                if (consentInformation.canRequestAds()) {
                    initializeMobileAdsSdk()
                }
            }
        }, { requestConsentError ->
            // Consent gathering failed.
            Log.w("Hilarity Tag", String.format("%s: %s", requestConsentError.errorCode, requestConsentError.message))
        })

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)
    }

    private fun updateAndReview() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val reviewManager = ReviewManagerFactory.create(this)

        //update
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) appUpdateManager.startUpdateFlow(appUpdateInfo, this, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).setAllowAssetPackDeletion(true).build()) else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) appUpdateManager.startUpdateFlow(appUpdateInfo, this, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).setAllowAssetPackDeletion(true).build())
        }

        //review
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task: Task<ReviewInfo?> -> if (task.isSuccessful) reviewInfo = task.result }
        if (reviewInfo != null) reviewManager.launchReviewFlow(this, reviewInfo!!)
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.layout_container, fragment).commit()
    }

    private fun showApps() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5382562347439530585")))
    }

    private fun rate() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun shareApp() {
        val message = """
            
            I'm recommending this impressive joke app, give a try!
            
            https//play.google.com/store/apps/details?id=$packageName
            
            """.trimIndent()
        IntentBuilder(this).setType("text/plain").setSubject(getString(R.string.app_name)).setChooserTitle("share using...").setText(message).startChooser()
    }

    private fun showAboutDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_about, findViewById(R.id.about_dialog), false)
        val dialogAbout = getDialogView(this, view)
        dialogAbout.show()
    }

    private fun loadInterstitialSaved() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.saved_joke_interstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                interstitialAdSaved = null
                loadInterstitialSaved()
            }

            override fun onAdLoaded(interstitial: InterstitialAd) {
                interstitialAdSaved = interstitial
                interstitialAdSaved!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        setFragment(SavedJokesFragment())
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        setFragment(SavedJokesFragment())
                    }

                    override fun onAdShowedFullScreenContent() {
                        interstitialAdSaved = null
                    }
                }
            }
        })
    }

    private fun loadInterstitialViewed() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.viewed_joke_interstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                interstitialAdViewed = null
                loadInterstitialViewed()
            }

            override fun onAdLoaded(interstitial: InterstitialAd) {
                interstitialAdViewed = interstitial
                interstitialAdViewed!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        setFragment(ViewedJokesFragment())
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        setFragment(ViewedJokesFragment())
                    }

                    override fun onAdShowedFullScreenContent() {
                        interstitialAdViewed = null
                    }
                }
            }
        })
    }
}