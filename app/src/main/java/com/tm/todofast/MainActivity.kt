package com.tm.todofast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    var doneTaskAdapter: TaskAdapter = TaskAdapter(arrayListOf(), arrayListOf())

    private var doneList: RecyclerView? = null

    var done = arrayListOf<Task>()
    var notDone = arrayListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        done = arrayListOf()

        notDone = arrayListOf(
            Task("1"),
            Task("2"),
            Task("3"),
            Task("4"),
            Task("5"),
            Task("6"),
            Task("7"),
            Task("8"),
            Task("9"),
        )

        doneTaskAdapter = TaskAdapter(done, notDone)
        doneList = findViewById(R.id.doneList)
        doneList!!.adapter = doneTaskAdapter
        doneList!!.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // set movement left and right forbidden

                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                if (viewHolder is TaskAdapter.TitleViewHolder)
                    return makeMovementFlags(
                        ItemTouchHelper.ACTION_STATE_IDLE,
                        ItemTouchHelper.ACTION_STATE_IDLE
                    )

                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // Called when an item is swiped to the left or right
                val position = viewHolder.adapterPosition

                if (viewHolder is TaskAdapter.TitleViewHolder)
                    return
                // Remove the item from your data set and notify the adapter

                println(position)
                if (position > notDone.size + 1) {
                    done.removeAt(position - notDone.size - 2)
                } else {
                    notDone.removeAt(position - 1)
                }

                doneTaskAdapter.notifyItemRemoved(position)

                println("REMOVED")
            }
        })
        itemTouchHelper.attachToRecyclerView(doneList)

    }

    /**
     * Setting a task to done and moving it to the top of the done list
     * @param index the index of the task in the total list (not done + done + titles)
     **/
    fun setItemDone(index: Int) {
        val task = notDone[index - 1]
        notDone.removeAt(index - 1)
        done.add(0, task)
        doneTaskAdapter.notifyItemMoved(index, notDone.size + 2)
    }

}