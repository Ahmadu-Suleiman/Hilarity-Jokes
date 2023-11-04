package com.meta4projects.hilarityjokes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.TemplateView
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.adapters.ViewedJokeAdapter
import com.meta4projects.hilarityjokes.others.Utils.loadNativeAd
import com.meta4projects.hilarityjokes.room.JokeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewedJokesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_viewed_jokes, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.viewed_jokes_recyclerview)
        val textViewNoJokes = view.findViewById<TextView>(R.id.text_no_viewed_jokes)
        val templateView: TemplateView = view.findViewById(R.id.native_ad_viewed_jokes)
        loadNativeAd(this, templateView, getString(R.string.viewed_jokes_native))

        CoroutineScope(Dispatchers.IO).launch {
            val jokesViewed = ArrayList(JokeDatabase.getINSTANCE(requireContext()).jokeDao().viewedJokes)
            withContext(Dispatchers.Main) {
                val viewedJokeAdapter = ViewedJokeAdapter(jokesViewed, requireContext())
                recyclerView.adapter = viewedJokeAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                if (jokesViewed.isEmpty()) textViewNoJokes.visibility = View.VISIBLE
            }
        }
        return view
    }
}