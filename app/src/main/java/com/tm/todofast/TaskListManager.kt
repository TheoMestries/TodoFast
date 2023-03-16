package com.tm.todofast

import java.util.*

class TaskListManager {
    private var done = arrayListOf<Task>()
    private var notDone = arrayListOf<Task>()
    private var lateNotDone = arrayListOf<Task>()

    /**
     * Add a task to the right list
     * @param task the task to add
     * @return the index of the task in the recycler view list
     */
    fun addTask(task: Task): Int {
        // done task
        if (task.DoneAt != null) {
            done.add(0, task)
            return lateNotDone.size + notDone.size + 3
        }

        // not done task
        if (task.selectedDate == null || task.selectedDate!!.after(Calendar.getInstance().time)) {
            val newIndex = getNewTaskIndexToNotDone(task, false)
            notDone.add(newIndex, task)
            return lateNotDone.size + newIndex + 2
        }

        // late not done task
        val newIndex = getNewTaskIndexToNotDone(task, true)
        lateNotDone.add(newIndex, task)
        return newIndex + 1
    }

    /**
     * Remove a task from the right list
     * @param index the index of the task in the recycler view list
     * @return the task that was removed
     */
    fun removeTask(index: Int): Task {
        if (index <= lateNotDone.size) {
            return lateNotDone.removeAt(index - 1)
        }

        if (index <= lateNotDone.size + notDone.size + 1) {
            return notDone.removeAt(index - lateNotDone.size - 2)
        }

        if (index <= lateNotDone.size + notDone.size + done.size + 2) {
            return done.removeAt(index - lateNotDone.size - notDone.size - 3)
        }

        throw IndexOutOfBoundsException("The index is out of bounds or a title")
    }

    /**
     * Returns the index of the new not done task. Compare the overdue date of the task with the overdue dates of the other tasks
     * @param task the task to be added
     * @param lateList if the task should be added to the late list or the not late one
     * @return the index the task should be added to
     */
    private fun getNewTaskIndexToNotDone(task: Task, lateList: Boolean): Int {
        val list = if (lateList) lateNotDone else notDone

        if (task.selectedDate != null) //
            for ((index, item) in list.withIndex()) {
                if (item.selectedDate == null || item.selectedDate!!.after(task.selectedDate!!)) {
                    return index
                }

            }
        return list.size
    }

    fun updateTask(task: Task): Int {
        val firstIndex = indexById(task.id)//searching by id because the task isn't the same object
        if (firstIndex == -1) {
            throw Exception("You tried to update a task that is not in the list")
        }
        removeTask(firstIndex)
        return addTask(task)
    }


    private fun indexByTask(task: Task): Int {
        if (task.DoneAt != null) {
            return done.indexOf(task) + lateNotDone.size + notDone.size + 3
        }
        if (task.selectedDate != null && task.selectedDate!!.after(Calendar.getInstance().time)) {
            return notDone.indexOf(task) + lateNotDone.size + 2
        }
        return lateNotDone.indexOf(task) + 1
    }

    fun indexById(id: Long): Int {
        for ((index, task) in lateNotDone.withIndex()) {
            if (task.id == id) {
                return index + 1
            }
        }
        for ((index, task) in notDone.withIndex()) {
            if (task.id == id) {
                return index + lateNotDone.size + 2
            }
        }
        for ((index, task) in done.withIndex()) {
            if (task.id == id) {
                return index + lateNotDone.size + notDone.size + 3
            }
        }
        return -1
    }

    fun getTask(position: Int): Task {
        if (position > lateNotDone.size + notDone.size + 1) {
            return done[position - lateNotDone.size - notDone.size - 3]
        }
        if (position > lateNotDone.size + 1) {
            return notDone[position - lateNotDone.size - 2]
        }

        return lateNotDone[position - 1]
    }

    fun getRecyclerListSize(): Int {
        return lateNotDone.size + notDone.size + done.size + 3
    }

    fun isTitle(position: Int): Boolean {
        return position == 0
                || position == lateNotDone.size + 1
                || position == lateNotDone.size + notDone.size + 2
    }

    fun isDone(position: Int): Boolean {
        return position > lateNotDone.size + notDone.size + 1
    }

    fun getTitle(position: Int): String {
        return when (position) {
            0 -> {
                "Overdue"
            }
            lateNotDone.size + 1 -> {
                "Not done"
            }
            lateNotDone.size + notDone.size + 2 -> {
                "Done"
            }
            else -> {
                throw Exception("Too much titles")
            }
        }
    }
}