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
import com.meta4projects.hilarityjokes.adapters.SavedJokeAdapter.SavedJokeHolder
import com.meta4projects.hilarityjokes.others.Utils.JOKE_EXTRA
import com.meta4projects.hilarityjokes.others.Utils.getDate
import com.meta4projects.hilarityjokes.others.Utils.shareText
import com.meta4projects.hilarityjokes.others.Utils.showToast
import com.meta4projects.hilarityjokes.room.Joke
import com.meta4projects.hilarityjokes.room.JokeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedJokeAdapter(private val jokes: ArrayList<Joke>, private val context: Context) : RecyclerView.Adapter<SavedJokeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedJokeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_joke_layout, parent, false)
        return SavedJokeHolder(view)
    }

    override fun onBindViewHolder(holder: SavedJokeHolder, position: Int) {
        val joke = jokes[holder.bindingAdapterPosition]
        holder.textViewDate.text = getDate(joke.date)
        holder.textViewJoke.text = joke.text
        holder.textViewJoke.setOnClickListener { context.startActivity(Intent(context, JokeActivity::class.java).putExtra(JOKE_EXTRA, joke.text)) }
        holder.imageViewDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                JokeDatabase.getINSTANCE(context).jokeDao().deleteJoke(joke)
            }
            jokes.removeAt(holder.bindingAdapterPosition)
            notifyItemRemoved(holder.bindingAdapterPosition)
            showToast("joke deleted", (context as Activity))
        }
        holder.imageViewShare.setOnClickListener { shareText(context, joke.text) }
    }

    override fun getItemCount(): Int {
        return jokes.size
    }

    class SavedJokeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView
        val textViewJoke: TextView
        val imageViewDelete: ImageView
        val imageViewShare: ImageView

        init {
            textViewDate = itemView.findViewById(R.id.textview_date)
            textViewJoke = itemView.findViewById(R.id.textview_joke)
            imageViewDelete = itemView.findViewById(R.id.imageview_delete_joke)
            imageViewShare = itemView.findViewById(R.id.imageview_share_joke)
        }
    }
}