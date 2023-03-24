package com.tm.todofast.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskStructure(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) :
    SQLiteOpenHelper(context, name, factory, version) {

    private var db: SQLiteDatabase? = null

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        db = sqLiteDatabase
        db!!.execSQL(CREATE_BDD)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, nVersion: Int, oVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_TASK")
        onCreate(db!!)
    }

    companion object {

        const val TABLE_TASK = "task"
        const val COL_ID = "id"
        const val NUM_COL_ID = 0

        const val COL_DESCRIPTION = "description"
        const val NUM_COL_DESCRIPTION = 1

        const val COL_CREATED_AT = "createdAt"
        const val NUM_COL_CREATED_AT = 2

        const val COL_TODO = "toDo"
        const val NUM_COL_TODO = 3

        const val COL_DONE_AT = "doneAt"
        const val NUM_COL_DONE_AT = 4

        private const val CREATE_BDD = "CREATE TABLE $TABLE_TASK (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_DESCRIPTION TEXT NOT NULL, " +
                "$COL_CREATED_AT DATETIME NOT NULL, " +
                "$COL_TODO DATE NOT NULL, " +
                "$COL_DONE_AT DATETIME NOT NULL);"
    }
}