package com.tm.todofast

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*


class DataBaseHelper(context: Context) {

    private var nTask: TaskStructure? = null
    private var bdd: SQLiteDatabase? = null
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val allTask: ArrayList<Task>
        get() {
            val retval = ArrayList<Task>()
            openForRead()

            val cursor = bdd!!.query(
                TaskStructure.TABLE_TASK,
                arrayOf(
                    TaskStructure.COL_ID,
                    TaskStructure.COL_DESCRIPTION,
                    TaskStructure.COL_CREATED_AT,
                    TaskStructure.COL_TODO,
                    TaskStructure.COL_DONE_AT
                ),
                null,
                null,
                null,
                null, TaskStructure.COL_ID
            )


            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val selectedDate =
                        if (cursor.getString(TaskStructure.NUM_COL_TODO) == "") null else formatter.parse(
                            cursor.getString(TaskStructure.NUM_COL_TODO)
                        )
                    val doneAt =
                        if (cursor.getString(TaskStructure.NUM_COL_DONE_AT) == "") null else formatter.parse(
                             cursor.getString(TaskStructure.NUM_COL_DONE_AT)
                        )

                    retval.add(
                        Task(
                            cursor.getLong(TaskStructure.NUM_COL_ID),
                            cursor.getString(TaskStructure.NUM_COL_DESCRIPTION),
                            formatter.parse(cursor.getString(TaskStructure.NUM_COL_CREATED_AT))!!,
                            selectedDate,
                            doneAt,

                        )
                    )
                }
            }
            cursor.close()
            close()

            return retval
        }

    init {
        nTask = TaskStructure(context, NOM_BDD, null, 1)
    }

    private fun openForWrite() {
        bdd = nTask!!.writableDatabase
    }

    private fun openForRead() {
        bdd = nTask!!.readableDatabase
    }

    private fun close() {
        bdd!!.close()
    }

    fun insertTask(text: String, selectedDate: Date?, DoneAt: Date?): Task {

        openForWrite()

        val values = ContentValues()
        val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val createdAt = Calendar.getInstance().time

        val selectedDateFormat = if (selectedDate == null) "" else formatDate.format(selectedDate)
        val doneAtFormat = if (DoneAt == null) "" else formatDate.format(DoneAt)
        values.put(TaskStructure.COL_DESCRIPTION, text)
        values.put(TaskStructure.COL_CREATED_AT, formatDate.format(createdAt))
        values.put(TaskStructure.COL_TODO, selectedDateFormat)
        values.put(TaskStructure.COL_DONE_AT, doneAtFormat)

        val id = bdd!!.insert(TaskStructure.TABLE_TASK, null, values)

        close()

        return Task(id, text, createdAt, selectedDate, DoneAt)
    }

    companion object {

        private const val NOM_BDD = "task.db"
    }

    fun deleteTask(id : Long) {
        openForWrite()
        bdd!!.delete(TaskStructure.TABLE_TASK, TaskStructure.COL_ID + " = " + id, null)
        close()
    }


}