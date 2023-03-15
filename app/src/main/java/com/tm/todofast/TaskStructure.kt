package com.tm.todofast

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskStructure (context : Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {

    private var db : SQLiteDatabase? = null

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        db = sqLiteDatabase
        db!!.execSQL(CREATE_BDD)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, nVersion: Int, oVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_TASK")
        onCreate(db!!)
    }

    companion object {

        val TABLE_TASK = "task"
        val COL_ID = "id"
        val NUM_COL_ID = 0

        val COL_DESCRIPTION = "description"
        val NUM_COL_DESCRIPTION = 1

        val COL_CREATEDAT = "createdAt"
        val NUM_COL_CREATEDAT = 2

        val COL_TODO = "toDo"
        val NUM_COL_TODO = 3

        val COL_DONEAT = "doneAt"
        val NUM_COL_DONEAT = 4

        private val CREATE_BDD = "CREATE TABLE $TABLE_TASK (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_DESCRIPTION TEXT NOT NULL, " +
                "$COL_CREATEDAT DATE NOT NULL, " +
                "$COL_TODO DATE NOT NULL, " +
                "$COL_DONEAT DATE NOT NULL);"
    }
}