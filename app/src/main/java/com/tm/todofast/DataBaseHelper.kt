package com.tm.todofast

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*


class DataBaseHelper(val context: Context) {

    private var nTask: TaskStructure? = null
    private var bdd: SQLiteDatabase? = null
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val allTask: ArrayList<Task>
        get() {
            val retval = ArrayList<Task>()
            openForRead()

            val cursor = bdd!!.query(
                TaskStructure.TABLE_TASK,
                arrayOf(
                    TaskStructure.COL_ID,
                    TaskStructure.COL_DESCRIPTION,
                    TaskStructure.COL_CREATEDAT,
                    TaskStructure.COL_TODO,
                    TaskStructure.COL_DONEAT
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
                        if (cursor.getString(TaskStructure.NUM_COL_DONEAT) == "") null else formatter.parse(
                             cursor.getString(TaskStructure.NUM_COL_DONEAT)
                        )

                    retval.add(
                        Task(
                            cursor.getLong(TaskStructure.NUM_COL_ID),
                            cursor.getString(TaskStructure.NUM_COL_DESCRIPTION),
                            formatter.parse(cursor.getString(TaskStructure.NUM_COL_CREATEDAT))!!,
                            selectedDate,
                            doneAt,

                        )
                    )
                }
            }
            close()
            return retval
        }

    init {
        nTask = TaskStructure(context, NOM_BDD, null, 1)
    }

    fun openForWrite() {
        bdd = nTask!!.writableDatabase
    }

    fun openForRead() {
        bdd = nTask!!.readableDatabase
    }

    fun close() {
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
        values.put(TaskStructure.COL_CREATEDAT, formatDate.format(createdAt))
        values.put(TaskStructure.COL_TODO, selectedDateFormat)
        values.put(TaskStructure.COL_DONEAT, doneAtFormat)

        val retval = bdd!!.insert(TaskStructure.TABLE_TASK, null, values)

        close()

        return Task(retval, text, createdAt, selectedDate, DoneAt)
    }

    companion object {

        private val NOM_BDD = "task.db"
    }

    fun deleteTask(id : Long) {
        openForWrite()
        bdd!!.delete(TaskStructure.TABLE_TASK, TaskStructure.COL_ID + " = " + id, null)
        close()
    }


}