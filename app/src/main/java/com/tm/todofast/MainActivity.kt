package com.tm.todofast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    var taskAdapter: TaskAdapter = TaskAdapter(arrayListOf(), arrayListOf())

    private var recyclerView: RecyclerView? = null

    var done = arrayListOf<Task>()
    var notDone = arrayListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        done = arrayListOf(
            Task("4"),
        )

        notDone = arrayListOf(
            Task("1"),
            Task("2"),
            Task("3"),
            Task("5")
        )

        taskAdapter = TaskAdapter(done, notDone)
        recyclerView = findViewById(R.id.taskList)
        recyclerView!!.adapter = taskAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(this)

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
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // Called when an item is swiped to the left or right
                val position = viewHolder.adapterPosition

                if (viewHolder is TaskAdapter.TitleViewHolder)
                    return
                // Remove the item from your data set and notify the adapter

                println(position)
                if (position > notDone.size + 1) {
                    done.removeAt(fromTotalListToDoneIndex(position))
                } else {
                    notDone.removeAt(position - 1)
                }

                taskAdapter.notifyItemRemoved(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    /**
     * Setting a task to done and moving it to the top of the done list
     * @param index the index of the task in the total list (not done + done + titles)
     **/
    fun setItemDone(index: Int) {
        val task = notDone[index - 1]
        notDone.removeAt(index - 1)
        done.add(0, task)

        taskAdapter.notifyItemMoved(index, notDone.size + 2)

    }

    fun setItemUnDone(position: Int) {
        val doneIndex = fromTotalListToDoneIndex(position)

        val task = done[doneIndex]
        val newIndex = getIndexOfNewNotDoneTask(task)

        done.removeAt(doneIndex)
        notDone.add(newIndex, task)

        taskAdapter.notifyItemMoved(position, newIndex + 1)

    }

    /**
     * Returns the index of the new not done task. Compare the overdue date of the task with the overdue dates of the other tasks
     * @param task the task to be added
     * @return the index the task should be added to
     **/
    private fun getIndexOfNewNotDoneTask(task: Task): Int {
        return 0
        //TODO("Not yet implemented")
    }

    private fun fromTotalListToDoneIndex(position: Int) = position - notDone.size - 2


}