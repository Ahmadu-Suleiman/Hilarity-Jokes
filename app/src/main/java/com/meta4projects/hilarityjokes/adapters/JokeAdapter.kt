package com.meta4projects.hilarityjokes.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.activities.JokeActivity
import com.meta4projects.hilarityjokes.adapters.JokeAdapter.JokeHolder
import com.meta4projects.hilarityjokes.others.Utils.JOKE_EXTRA
import com.meta4projects.hilarityjokes.room.Joke

class JokeAdapter(private var jokes: ArrayList<Joke>, private val context: Context) : RecyclerView.Adapter<JokeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_joke_layout, parent, false)
        return JokeHolder(view)
    }

    override fun onBindViewHolder(holder: JokeHolder, position: Int) {
        val joke = jokes[holder.bindingAdapterPosition]
        holder.textViewJoke.text = joke.text
        holder.textViewJoke.setOnClickListener { context.startActivity(Intent(context, JokeActivity::class.java).putExtra(JOKE_EXTRA, joke.text)) }
    }

    override fun getItemCount(): Int {
        return jokes.size
    }

    fun setJokes(jokes: ArrayList<Joke>) {
        this.jokes = jokes
    }

    class JokeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewJoke: TextView

        init {
            textViewJoke = itemView.findViewById(R.id.textview_joke)
        }
    }
}