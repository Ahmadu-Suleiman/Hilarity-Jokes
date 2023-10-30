package com.meta4projects.hilarityjokes.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.adapters.JokeAdapter
import com.meta4projects.hilarityjokes.others.Utils.getJokesFromJson
import com.meta4projects.hilarityjokes.others.Utils.getRequestJokes
import com.meta4projects.hilarityjokes.others.Utils.okHttpClient
import com.meta4projects.hilarityjokes.others.Utils.showToast
import com.meta4projects.hilarityjokes.room.Jokes
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

@SuppressLint("NotifyDataSetChanged")
class SearchActivity : AppCompatActivity() {
    private var searchTerm = ""
    private var jokes = Jokes()
    private lateinit var textViewNoJokes: TextView
    private var textViewPage: TextView? = null
    private var jokeAdapter: JokeAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val editTextSearch = findViewById<EditText>(R.id.search_jokes)
        val recyclerView = findViewById<RecyclerView>(R.id.searched_jokes_recyclerview)
        textViewNoJokes = findViewById(R.id.text_no_searched_jokes)
        textViewPage = findViewById(R.id.textview_page)
        val imageViewPrevious = findViewById<ImageView>(R.id.imageview_search_previous)
        val imageViewNext = findViewById<ImageView>(R.id.imageview_search_next)
        jokeAdapter = JokeAdapter(jokes.jokes, this)
        recyclerView.adapter = jokeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        setJokes(1)
        editTextSearch.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTerm = editTextSearch.text.toString()
                setJokes(1)
                return@setOnEditorActionListener true
            }
            false
        }
        imageViewPrevious.setOnClickListener { if (jokes.currentPage != jokes.previousPage) setJokes(jokes.previousPage) }
        imageViewNext.setOnClickListener { if (jokes.currentPage != jokes.nextPage) setJokes(jokes.nextPage) }
        if (jokes.jokes.isEmpty()) textViewNoJokes.visibility = View.VISIBLE
    }

    private fun setJokes(page: Int) {
        val requestJoke = getRequestJokes(this@SearchActivity, searchTerm, page)
        okHttpClient!!.newCall(requestJoke).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { showToast("error, try again", this@SearchActivity) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body != null) {
                    val json = response.body!!.string()
                    jokes = getJokesFromJson(this@SearchActivity, json)
                    runOnUiThread {
                        textViewPage!!.text = getPage(jokes)
                        jokeAdapter!!.setJokes(jokes.jokes)
                        jokeAdapter!!.notifyDataSetChanged()
                        if (jokes.jokes.isEmpty()) textViewNoJokes.visibility = View.VISIBLE else textViewNoJokes.visibility = View.GONE
                    }
                    response.close()
                } else {
                    runOnUiThread { showToast("error, try again", this@SearchActivity) }
                }
            }
        })
    }

    private fun getPage(jokes: Jokes): String {
        return String.format("%s/%s", jokes.currentPage, jokes.totalPages)
    }
}