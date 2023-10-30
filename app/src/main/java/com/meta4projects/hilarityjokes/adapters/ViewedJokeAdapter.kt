package com.meta4projects.hilarityjokes.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meta4projects.hilarityjokes.R
import com.meta4projects.hilarityjokes.activities.JokeActivity
import com.meta4projects.hilarityjokes.adapters.ViewedJokeAdapter.ViewedJokeHolder
import com.meta4projects.hilarityjokes.others.Utils.JOKE_EXTRA
import com.meta4projects.hilarityjokes.others.Utils.getDate
import com.meta4projects.hilarityjokes.others.Utils.shareText
import com.meta4projects.hilarityjokes.others.Utils.showToast
import com.meta4projects.hilarityjokes.room.Joke
import com.meta4projects.hilarityjokes.room.JokeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewedJokeAdapter(private val jokes: List<Joke>, private val context: Context) : RecyclerView.Adapter<ViewedJokeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewedJokeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewed_joke_layout, parent, false)
        return ViewedJokeHolder(view)
    }

    override fun onBindViewHolder(holder: ViewedJokeHolder, position: Int) {
        val joke = jokes[holder.bindingAdapterPosition]
        holder.textViewDate.text = getDate(joke.date)
        holder.textViewJoke.text = joke.text
        holder.textViewJoke.setOnClickListener { context.startActivity(Intent(context, JokeActivity::class.java).putExtra(JOKE_EXTRA, joke.text)) }
        holder.imageViewSave.setOnClickListener {
            joke.isSaved = true
            joke.date = System.currentTimeMillis()
            CoroutineScope(Dispatchers.IO).launch { JokeDatabase.getINSTANCE(context).jokeDao().updateJoke(joke) }
            showToast("joke saved", (context as Activity))
        }
        holder.imageViewShare.setOnClickListener { shareText(context, joke.text) }
    }

    override fun getItemCount(): Int {
        return jokes.size
    }

    class ViewedJokeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView
        val textViewJoke: TextView
        val imageViewSave: ImageView
        val imageViewShare: ImageView

        init {
            textViewDate = itemView.findViewById(R.id.textview_date)
            textViewJoke = itemView.findViewById(R.id.textview_joke)
            imageViewSave = itemView.findViewById(R.id.imageview_save_joke)
            imageViewShare = itemView.findViewById(R.id.imageview_share_joke)
        }
    }
}