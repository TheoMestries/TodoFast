package com.tm.todofast

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tm.todofast.database.DataBaseHelper
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private var taskAdapter: TaskAdapter? = null
    private val dbHelper = DataBaseHelper(this)

    private var recyclerView: RecyclerView? = null

    private var taskManager = TaskListManager()
    private var done: ImageView? = null
    private var circle: ImageView? = null
    private var selectedDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Notifications.askForNotificationPermission(this)

        done = findViewById(R.id.done)
        circle = findViewById(R.id.circle)
        circle?.visibility = View.INVISIBLE
        done?.visibility = View.INVISIBLE
        taskAdapter = TaskAdapter(taskManager)

        recyclerView = findViewById(R.id.taskList)
        recyclerView!!.adapter = taskAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        val addText = findViewById<TextView>(R.id.textTaskName)
        dbHelper.deleteTask(4)
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
        removeNotificationAlarm(task)

        taskAdapter!!.notifyItemRemoved(position)
    }


    /**
     * Setting a task to done and moving it to the top of the done list
     * @param position the index of the task in the total list (not done + done + titles)
     **/

    fun setItemDone(position: Int) {
        playAnimation()

        val task = taskManager.getTask(position)
        task.DoneAt = Calendar.getInstance().time

        val newIndex = taskManager.updateTask(task)
        dbHelper.updateTask(task)
        removeNotificationAlarm(task)//remove the notification because the task is done

        taskAdapter!!.notifyItemMoved(position, newIndex)
    }

    private fun playAnimation() {
        circle?.visibility = View.VISIBLE
        val circleDrawable = circle?.drawable

        if (circleDrawable is Animatable2) {
            circleDrawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    done?.visibility = View.VISIBLE
                    val donedrawable = done?.drawable
                    if (donedrawable is Animatable2) {
                        donedrawable.registerAnimationCallback(object :
                            Animatable2.AnimationCallback() {
                            override fun onAnimationStart(donedrawable: Drawable?) {
                            }

                            override fun onAnimationEnd(donedrawable: Drawable?) {
                                done?.visibility = View.GONE

                            }
                        })
                        donedrawable.start()
                    }
                }

                override fun onAnimationEnd(drawable: Drawable?) {


                    circle?.visibility = View.GONE
                }


            })
            circleDrawable.start()
        }

    }

    fun setItemUnDone(position: Int) {
        val task = taskManager.getTask(position)
        task.DoneAt = null //set to null to tell that it's not done

        val newIndex = taskManager.updateTask(task)
        dbHelper.updateTask(task)

        //add the notification again because it have been removed when the task was set done
        addNotificationAlarm(task)

        taskAdapter!!.notifyItemMoved(position, newIndex)
    }

    /**
     * Called when the user clicks on the button to add a task
     * Add the task to the list and to the database
     */
    fun onBtnAddClick(view: View) {
        val text = findViewById<TextView>(R.id.textTaskName).text.toString()
        val task = dbHelper.insertTask(text, selectedDate, null)

        addNotificationAlarm(task)

        addTask(task)
    }

    private fun addNotificationAlarm(task: Task) {
        if (task.selectedDate != null) {
            val intent = Intent(this, AlarmReceiver::class.java)
                .putExtra("taskId", task.id)
                .putExtra("taskTitle", task.title)

            val pendingIntent =
                PendingIntent.getBroadcast(this, task.id.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.selectedDate!!.time, pendingIntent)
        }
    }

    private fun removeNotificationAlarm(task: Task) {
        //setting the same pending intent as the one used to set the alarm
        val intent = Intent(this, AlarmReceiver::class.java)
            .putExtra("taskId", task.id)
            .putExtra("taskTitle", task.title)

        val pendingIntent =
            PendingIntent.getBroadcast(this, task.id.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Add a task to the list
     * !!! the task will not be added to the database using this method
     * @param task the task to add
     */
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
        val actualDate = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                val timePicker = TimePickerDialog(this, { _, hour, minute ->

                    val cal = Calendar.getInstance()
                    cal[Calendar.YEAR] = year
                    cal[Calendar.MONTH] = month
                    cal[Calendar.DAY_OF_MONTH] = dayOfMonth
                    cal[Calendar.HOUR_OF_DAY] = hour
                    cal[Calendar.MINUTE] = minute

                    selectedDate = cal.time

                    // Create a date format, then a date object with our offset
                    val simpleFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    (view as TextView).text = simpleFormat.format(selectedDate!!)

                }, actualDate.get(Calendar.HOUR_OF_DAY), actualDate.get(Calendar.MINUTE), true)

                timePicker.show()

            },
            actualDate.get(Calendar.YEAR),
            actualDate.get(Calendar.MONTH),
            actualDate.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

}