package com.tm.todofast

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TaskAdapter(private val done: ArrayList<Task>, private val notDone: ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeTitle = 0
    private val viewTypeTask = 1

    override fun getItemViewType(position: Int): Int {
        // what type of view
        return if (position == 0 || position == notDone.size + 1)
            viewTypeTitle
        else
            viewTypeTask
    }

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.titleTask)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var titleTextView: TextView = itemView.findViewById(R.id.titleTask)

        init {
            itemView.findViewById<ImageButton>(R.id.actionTask).setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Get the position of the item that was clicked
            val position: Int = adapterPosition
            // Do something with the clicked item
            Log.d("com.tm.todofast.TaskAdapter", "Item clicked at position $position")

            val mainActivity = view?.context as MainActivity
            mainActivity.setItemDone(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == viewTypeTitle) {
            val view: View = inflater.inflate(R.layout.title_task, parent, false)
            TitleViewHolder(view)
        } else {
            val view: View = inflater.inflate(R.layout.task, parent, false)
            TaskViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            holder.titleTextView.text = if (position == 0) "Not Done" else "Done"
        } else if (holder is TaskViewHolder) {
            val item =
                if (position < notDone.size + 1) notDone[position - 1] else done[position - notDone.size - 2]
            holder.titleTextView.text = item.title
        }
    }


    override fun getItemCount(): Int {
        return done.size + notDone.size + 2
    }

}