package com.joaomgcd.taskerpluginlibrary.runner

import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputBase

class TaskerOutputRename(val oldValue: String, val newValue: CharSequence?)
class TaskerOutputRenames : ArrayList<TaskerOutputRename>() {
    fun rename(infos: TaskerOutputBase<*>) {
        forEach { infos.renameIfNeeded(it.oldValue, it.newValue) }
    }

    companion object {
    }
}