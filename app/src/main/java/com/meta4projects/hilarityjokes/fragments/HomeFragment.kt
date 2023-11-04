package com.meta4projects.hilarityjokes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.others.Utils.getRandomJokeFromJson
import com.meta4projects.hilarityjokes.others.Utils.getRequestJoke
import com.meta4projects.hilarityjokes.others.Utils.isNotification
import com.meta4projects.hilarityjokes.others.Utils.joke
import com.meta4projects.hilarityjokes.others.Utils.loadNativeAd
import com.meta4projects.hilarityjokes.others.Utils.okHttpClient
import com.meta4projects.hilarityjokes.others.Utils.shareText
import com.meta4projects.hilarityjokes.others.Utils.showToast
import com.meta4projects.hilarityjokes.room.JokeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class HomeFragment : Fragment() {

    private var interstitialAd: InterstitialAd? = null
    private lateinit var textviewRandomJoke: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val imageViewSave = view.findViewById<ImageView>(R.id.imageview_save_joke)
        val imageViewShare = view.findViewById<ImageView>(R.id.imageview_share_joke)
        textviewRandomJoke = view.findViewById(R.id.textview_random_joke)
        val buttonShowRandomJoke = view.findViewById<AppCompatButton>(R.id.button_show_random_joke)
        if (isNotification) {
            isNotification = false
            textviewRandomJoke.text = joke.text
        } else setRandomJoke(false)
        imageViewSave.setOnClickListener {
            joke.isSaved = true
            CoroutineScope(Dispatchers.IO).launch { JokeDatabase.getINSTANCE(requireContext()).jokeDao().insertJoke(joke) }
            showToast("joke saved", requireActivity())
        }
        imageViewShare.setOnClickListener { shareText(requireContext(), joke.text) }
        buttonShowRandomJoke.setOnClickListener { if (interstitialAd != null) interstitialAd!!.show(requireActivity()) else setRandomJoke(true) }

        val templateView: TemplateView = view.findViewById(R.id.native_ad_main)
        loadInterstitial()
        loadNativeAd(this, templateView, getString(R.string.main_native))
        return view
    }

    private fun setRandomJoke(showToast: Boolean) {
        try {
            val requestJoke = getRequestJoke(requireContext())
            okHttpClient!!.newCall(requestJoke).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (showToast) requireActivity().runOnUiThread { showToast("could not fetch a random joke, try again", requireActivity()) }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.body != null) {
                        val json = response.body!!.string()
                        joke = getRandomJokeFromJson(requireContext(), json)
                        requireActivity().runOnUiThread { textviewRandomJoke.text = joke.text }
                        response.close()
                    } else {
                        if (showToast) requireActivity().runOnUiThread { showToast("could not fetch a random joke, try again", requireActivity()) }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), getString(R.string.main_interstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                interstitialAd = null
                loadInterstitial()
            }

            override fun onAdLoaded(interstitial: InterstitialAd) {
                interstitialAd = interstitial
                interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        setRandomJoke(true)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        setRandomJoke(true)
                    }

                    override fun onAdShowedFullScreenContent() {
                        interstitialAd = null
                    }
                }
            }
        })
    }
}