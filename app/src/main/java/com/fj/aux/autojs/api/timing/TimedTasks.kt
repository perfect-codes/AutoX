package com.fj.aux.autojs.api.timing

import com.stardust.autojs.execution.ExecutionConfig
import com.fj.aux.timing.TimedTask
import com.fj.aux.timing.TimedTaskManager
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

object TimedTasks {

    fun daily(path: String, hour: Int, minute: Int) {
        com.fj.aux.timing.TimedTaskManager.getInstance().addTask(com.fj.aux.timing.TimedTask.dailyTask(LocalTime(hour, minute), path, ExecutionConfig()))
    }

    fun disposable(path: String, millis: Long) {
        com.fj.aux.timing.TimedTaskManager.getInstance().addTask(com.fj.aux.timing.TimedTask.disposableTask(LocalDateTime(millis), path, ExecutionConfig()))
    }

    fun weekly(path: String, millis: Long) {
        //TimedTaskManager.getInstance().addTask(TimedTask.weeklyTask(LocalDateTime(millis), path, ExecutionConfig()))
    }

}