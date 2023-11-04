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
import com.meta4projects.hilarityjokes.adapters.SavedJokeAdapter
import com.meta4projects.hilarityjokes.others.Utils.loadNativeAd
import com.meta4projects.hilarityjokes.room.JokeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedJokesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_saved_jokes, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.saved_jokes_recyclerview)
        val textViewNoJokes = view.findViewById<TextView>(R.id.text_no_saved_jokes)
        val templateView: TemplateView = view.findViewById(R.id.native_ad_saved_jokes)
        loadNativeAd(this,templateView, getString(R.string.saved_jokes_native))

        CoroutineScope(Dispatchers.IO).launch {
            val jokesSaved = ArrayList(JokeDatabase.getINSTANCE(requireContext()).jokeDao().savedJokes)
            withContext(Dispatchers.Main) {
                val savedJokeAdapter = SavedJokeAdapter(jokesSaved, requireContext())
                recyclerView.adapter = savedJokeAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                if (jokesSaved.isEmpty()) textViewNoJokes.visibility = View.VISIBLE
            }
        }
        return view
    }
}