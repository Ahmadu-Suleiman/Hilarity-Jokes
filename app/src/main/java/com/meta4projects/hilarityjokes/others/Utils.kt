package com.meta4projects.hilarityjokes.others

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.fragment.app.Fragment
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.room.Joke
import com.meta4projects.hilarityjokes.room.JokeDatabase
import com.meta4projects.hilarityjokes.room.Jokes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    const val JOKE_EXTRA = "com.meta4projects.hilarityjokes.JokeExtra"
    private const val endPointJoke = "https://icanhazdadjoke.com/"
    private const val endPointJokeSearch = "https://icanhazdadjoke.com/search"
    const val TAG: String = "Hilarity TAG"

    @JvmStatic
    var okHttpClient: OkHttpClient? = null
        get() {
            if (field == null) field = OkHttpClient()
            return field
        }
        private set
    private var gson: Gson? = null
        get() {
            if (field == null) field = Gson()
            return field
        }

    @JvmField
    var joke = Joke()

    @JvmField
    var isNotification = false

    @JvmStatic
    fun getRequestJoke(context: Context): Request {
        return Request.Builder().url(endPointJoke).addHeader("User-Agent", String.format("My App: http://play.google.com/store/apps/details?id=%s, My Email: ahmadumeta4.1@gmailcom", context.packageName)).addHeader("Accept", "application/json").addHeader("Content-Type", "application/json").build()
    }

    @JvmStatic
    fun getRequestJokes(context: Context, term: String?, page: Int): Request {
        val builderUrl: HttpUrl.Builder = endPointJokeSearch.toHttpUrl().newBuilder().addQueryParameter("term", term).addQueryParameter("limit", "30").addQueryParameter("page", page.toString())
        val url: String = builderUrl.toString()
        return Request.Builder().url(url).addHeader("User-Agent", String.format("My App: http://play.google.com/store/apps/details?id=%s, My Email: ahmadumeta4.1@gmailcom", context.packageName)).addHeader("Accept", "application/json").addHeader("Content-Type", "application/json").build()
    }

    @JvmStatic
    fun getRandomJokeFromJson(context: Context, json: String?): Joke {
        val jsonObject = gson!!.fromJson(json, JsonObject::class.java)
        val id = jsonObject["id"].asString
        val text = jsonObject["joke"].asString
        val joke = Joke(id, text)
        CoroutineScope(Dispatchers.IO).launch {
            JokeDatabase.getINSTANCE(context).jokeDao().insertJoke(joke)
        }
        return joke
    }

    @JvmStatic
    fun getJokesFromJson(context: Context, json: String?): Jokes {
        val jsonObject = gson!!.fromJson(json, JsonObject::class.java)
        val currentPage = jsonObject["current_page"].asInt
        val previousPage = jsonObject["previous_page"].asInt
        val nextPage = jsonObject["next_page"].asInt
        val totalPages = jsonObject["total_pages"].asInt
        val jsonArray = jsonObject["results"].asJsonArray
        val jokeList = ArrayList<Joke>(jsonArray.size())
        for (jsonElement in jsonArray) {
            val `object` = jsonElement.asJsonObject
            val id = `object`["id"].asString
            val text = `object`["joke"].asString
            jokeList.add(Joke(id, text))
        }
        val jokes = Jokes(currentPage, previousPage, nextPage, totalPages, jokeList)
        CoroutineScope(Dispatchers.IO).launch {
            JokeDatabase.getINSTANCE(context).jokeDao().insertJokes(jokeList)
        }
        return jokes
    }

    @JvmStatic
    fun shareText(context: Context?, text: String?) {
        IntentBuilder(context!!).setType("text/plain").setSubject("Hilarity Jokes").setChooserTitle("share using...").setText(text).startChooser()
    }

    @JvmStatic
    fun getDate(milliseconds: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(milliseconds))
    }

    @JvmStatic
    fun showToast(text: String?, activity: Activity) {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_custom_toast, activity.findViewById(R.id.toast_layout))
        val message = view.findViewById<TextView>(R.id.toast_layout)
        message.text = text
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    @JvmStatic
    fun getDialogView(context: Context?, view: View?): AlertDialog {
        return AlertDialog.Builder(context!!).setView(view).create()
    }

    @JvmStatic
    fun loadNativeAd(fragment: Fragment, templateView: TemplateView, adUnitId: String?) {
        templateView.visibility = View.GONE
        val adLoader = AdLoader.Builder(fragment.requireContext(), adUnitId!!).forNativeAd { nativeAd: NativeAd ->
            templateView.setNativeAd(nativeAd)
            if (fragment.isAdded && fragment.requireActivity().isDestroyed) nativeAd.destroy()
        }.withAdListener(object : AdListener() {
            override fun onAdLoaded() {
                templateView.visibility = View.VISIBLE
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}