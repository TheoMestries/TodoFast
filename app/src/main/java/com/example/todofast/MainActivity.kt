package com.example.todofast

import TaskAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    var taskAdapter: TaskAdapter = TaskAdapter(arrayListOf(), this)
    private var listView: RecyclerView? = null
    var values = arrayListOf<Task>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         values = arrayListOf(
            Task("tafast"),
            Task("tonperefast"),
            Task("todofast"),
            Task("susfast"),
            Task("afast"),
            Task("unimolixfast"),
            Task("the fast"),
            Task("not so fast"),
        )
        taskAdapter = TaskAdapter(values, this)
        listView = findViewById(R.id.list)
        listView!!.adapter = taskAdapter
        listView!!.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // set movement left and right forbidden
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Called when an item is dragged and dropped to a new position
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                // Update your data set with the new item order
                Collections.swap(values, fromPosition, toPosition)
                taskAdapter.notifyItemMoved(fromPosition, toPosition)

                println("MOVED")
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Called when an item is swiped to the left or right
                val position = viewHolder.adapterPosition
                // Remove the item from your data set and notify the adapter
                values.removeAt(position)
                taskAdapter.notifyItemRemoved(position)

                println("REMOVED")
            }
        })
        itemTouchHelper.attachToRecyclerView(listView)

    }

    fun setItemToFirst(index: Int) {
        Collections.swap(values, index, 0)
        taskAdapter.notifyItemMoved(index, 0)
    }

}