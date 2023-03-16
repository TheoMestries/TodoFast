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
import com.tm.todofast.database.DataBaseHelper
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var taskAdapter: TaskAdapter? = null
    private val dbHelper = DataBaseHelper(this)

    private var recyclerView: RecyclerView? = null

    private var taskManager = TaskListManager()


    private var selectedDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        taskAdapter = TaskAdapter(taskManager)

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
        if (position == -1) //fix the crash of the button clicked twice (really fast)
            return

        val task = taskManager.removeTask(position)
        dbHelper.deleteTask(task.id)

        taskAdapter!!.notifyItemRemoved(position)
    }


    /**
     * Setting a task to done and moving it to the top of the done list
     * @param position the index of the task in the total list (not done + done + titles)
     **/
    fun setItemDone(position: Int) {
        val task = taskManager.getTask(position)
        task.DoneAt = Calendar.getInstance().time

        val newIndex = taskManager.updateTask(task)
        dbHelper.updateTask(task)

        taskAdapter!!.notifyItemMoved(position, newIndex)
    }

    fun setItemUnDone(position: Int) {
        val task = taskManager.getTask(position)
        task.DoneAt = null //set to null to tell that it's not done

        val newIndex = taskManager.updateTask(task)
        dbHelper.updateTask(task)

        taskAdapter!!.notifyItemMoved(position, newIndex)
    }

    fun onBtnAddClick(view: View) {
        val text = findViewById<TextView>(R.id.textTaskName).text.toString()
        val task = dbHelper.insertTask(text, selectedDate, null)
        addTask(task)
    }

    private fun addTask(task: Task) {
        val recyclerIndex = taskManager.addTask(task)

        taskAdapter!!.notifyItemInserted(recyclerIndex)

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