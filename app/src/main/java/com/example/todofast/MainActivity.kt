package com.example.todofast

import TaskAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val values = arrayListOf(
            Task("tafast"),
            Task("tonperefast"),
            Task("todofast"),
            Task("susfast"),
            Task("afast"),
            Task("unimolixfast"),
            Task("the fast"),
            Task("not so fast"),
        )

        val adapter = TaskAdapter(values, this)

        val listView = findViewById<RecyclerView>(R.id.list)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(this)
    }

}