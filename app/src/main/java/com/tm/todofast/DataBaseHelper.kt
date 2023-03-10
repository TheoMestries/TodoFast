package com.tm.todofast

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_TABLE_QUERY)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "my_database"
        private const val DATABASE_VERSION = 1
        private const val CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS my_table(id INTEGER PRIMARY KEY, description TEXT, CreatedAt DATE, ToDo DATE  )"
        private const val DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS my_table"
    }

    fun addItemDataBase(description: String, toDo: Date?): Task {
        val db = writableDatabase
        val createdAt = Calendar.getInstance().time
        val contentValues = ContentValues().apply {
            put("description", description)
            put("CreatedAt", createdAt.time)
            put("ToDo", toDo?.time)
        }
        val id = db.insert("my_table", null, contentValues)
        db.close()
        return Task(id , description, createdAt,toDo)
    }
}