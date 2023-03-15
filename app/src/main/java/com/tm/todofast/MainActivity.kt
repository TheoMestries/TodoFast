package com.tm.todofast

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var taskAdapter: TaskAdapter = TaskAdapter(arrayListOf(), arrayListOf())
    private val dbHelper = DataBaseHelper(this)

    private var recyclerView: RecyclerView? = null

    private var done = arrayListOf<Task>()
    private var notDone = arrayListOf<Task>()


    private var selectedDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        taskAdapter = TaskAdapter(done, notDone)
        recyclerView = findViewById(R.id.taskList)
        recyclerView!!.adapter = taskAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        val addText = findViewById<TextView>(R.id.textTaskName)

        val allTask = dbHelper.allTask
        for (task in allTask) {
          addTask(task)
        }

        addText.addTextChangedListener {
            val taskName = addText.text.toString()

            findViewById<FloatingActionButton>(R.id.btnAddTask).isEnabled = taskName.isNotEmpty()
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                if (viewHolder is TaskAdapter.TitleViewHolder)
                    // set movement left and right forbidden
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
                val position = viewHolder.adapterPosition

                if (viewHolder is TaskAdapter.TitleViewHolder)
                    return

                removeTask(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun removeTask(position: Int) {
        if(position == -1) //fix the crash of the button clicked twice (really fast)
            return

        if (position > notDone.size + 1) {
            val doneIndex = fromTotalListToDoneIndex(position)

            val task = done[doneIndex]
            dbHelper.deleteTask(task.id)
            done.removeAt(doneIndex)
        } else {
            val task = notDone[position - 1]
            dbHelper.deleteTask(task.id)
            notDone.removeAt(position - 1)
        }

        taskAdapter.notifyItemRemoved(position)
    }


    /**
     * Setting a task to done and moving it to the top of the done list
     * @param index the index of the task in the total list (not done + done + titles)
     **/
    fun setItemDone(index: Int) {
        val task = notDone[index - 1]
        notDone.removeAt(index - 1)
        done.add(0, task)
        task.DoneAt= Calendar.getInstance().time
        dbHelper.updateTask(task)


        taskAdapter.notifyItemMoved(index, notDone.size + 2)
    }

    fun setItemUnDone(position: Int) {
        val doneIndex = fromTotalListToDoneIndex(position)

        val task = done[doneIndex]
        val newIndex = getIndexOfNewNotDoneTask(task)
        task.DoneAt = null
        dbHelper.updateTask(task)

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
        if (task.selectedDate != null) //
            for ((index, item) in notDone.withIndex()) {
                if (item.selectedDate != null) {
                    if (item.selectedDate!!.after(task.selectedDate!!)) {
                        return index
                    }
                }

            }
        return notDone.size
    }

    /**
     * Returns the index of the new done task. Compare the done date of the task with the done dates of the other tasks
     * @param task the task to be added
     * @return the index the task should be added to
     */
    private fun getIndexOfNewDoneTask(task: Task): Int {
        if (task.DoneAt != null) //
            for ((index, item) in done.withIndex()) {
                if (item.DoneAt != null) {
                    if (item.DoneAt!!.after(task.DoneAt!!)) {
                        return index
                    }
                }

            }
        return done.size
    }

    private fun fromTotalListToDoneIndex(position: Int) = position - notDone.size - 2



    fun onBtnAddClick(view: View) {

        val text = findViewById<TextView>(R.id.textTaskName).text.toString()
        val task = dbHelper.insertTask(text, selectedDate,null)
        addTask(task)
    }

    private fun addTask(task: Task) {


        if (task.DoneAt != null) {
            val doneIndex = getIndexOfNewDoneTask(task)
            done.add(doneIndex, task)
            taskAdapter.notifyItemInserted(doneIndex + notDone.size + 2)
            return
        }else {
            val newIndex = getIndexOfNewNotDoneTask(task)
            notDone.add(newIndex, task)
            taskAdapter.notifyItemInserted(newIndex + 1)
        }


        resetAddTask()
    }

    private fun resetAddTask() {
        findViewById<TextView>(R.id.textTaskName).text = ""
        findViewById<TextView>(R.id.editTextDate).text = getString(R.string.base_date_text)
        selectedDate = null
    }

    fun onBtnChooseDateClick(view: View) {
        val builder = MaterialDatePicker.Builder.datePicker().setTitleText("Select deadline")
        val picker = builder.build()

        picker.addOnPositiveButtonClickListener {
            //save date
            picker.selection

            val timeZoneUTC: TimeZone = TimeZone.getDefault()

            // It will be negative, so that's the -1
            val offsetFromUTC: Int = timeZoneUTC.getOffset(Date().time) * -1

            // Create a date format, then a date object with our offset
            val simpleFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDate = Date(it + offsetFromUTC)


            (view as TextView).text = simpleFormat.format(selectedDate!!)
        }

        picker.show(supportFragmentManager, picker.toString())

    }

}