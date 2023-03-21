package com.tm.todofast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(private val taskManager: TaskListManager) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeTitle = 0
    private val viewTypeNotDoneTask = 1
    private val viewTypeDoneTask = 2

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.titleTask)
    }

    inner class TaskViewHolder(itemView: View, private var isDone: Boolean) :
        RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView
            get() {
                return itemView.findViewById(R.id.titleTask)
            }

        val dateTextView: TextView
            get() {
                return itemView.findViewById(R.id.dateTask)
            }

        private val actionButton: ImageButton
            get() {
                return itemView.findViewById(R.id.actionTask)
            }

        init {
            itemView.findViewById<ImageButton>(R.id.actionTask)
                .setOnClickListener { onActionButtonClicked(it) }
            itemView.findViewById<ImageButton>(R.id.deleteButton)
                .setOnClickListener { onDeleteButtonClicked() }
            itemView.tag = isDone
        }

        private fun onDeleteButtonClicked() {
            // Get the position of the item that was clicked
            val position: Int = adapterPosition

            val mainActivity = itemView.context as MainActivity
            mainActivity.removeTask(position)
        }

        fun updateImageButton() {
            if (itemView.tag!! as Boolean)
                actionButton.setImageResource(R.drawable.undo)
            else
                actionButton.setImageResource(R.drawable.done)
        }

        /**
         * Will be called when the image button of the item is clicked
         */
        private fun onActionButtonClicked(view: View?) {
            // Get the position of the item that was clicked
            val position: Int = adapterPosition

            val mainActivity = view!!.context as MainActivity

            //keep the state of the item
            itemView.tag = isDone
            if (isDone) {
                mainActivity.setItemUnDone(position)
            } else {
                mainActivity.setItemDone(position)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        // what type of view
        return if (taskManager.isTitle(position))
            viewTypeTitle
        else
            if (taskManager.isDone(position)) viewTypeDoneTask else viewTypeNotDoneTask
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == viewTypeTitle) {
            val view: View = inflater.inflate(R.layout.title_task, parent, false)
            TitleViewHolder(view)
        } else {
            val view: View = inflater.inflate(R.layout.task, parent, false)
            TaskViewHolder(view, viewType == viewTypeDoneTask)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            holder.titleTextView.text = taskManager.getTitle(position)
        } else if (holder is TaskViewHolder) {
            val item: Task = taskManager.getTask(position)

            holder.titleTextView.text = item.title

            //set the date only if there is one
            if (item.selectedDate != null)
                holder.dateTextView.text =
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(item.selectedDate!!)
            else
                holder.dateTextView.text = ""


            holder.updateImageButton()
        }
    }


    override fun getItemCount(): Int {
        return taskManager.getRecyclerListSize()
    }

}