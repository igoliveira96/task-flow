package br.com.goulart.taskflow.di

import android.content.Context
import androidx.room.Room
import br.com.goulart.taskflow.data.local.TaskFlowDatabase

interface AppContainer {
    val database: TaskFlowDatabase
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val applicationContext = context.applicationContext

    override val database: TaskFlowDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskFlowDatabase::class.java,
            "taskflow.db",
        ).build()
    }
}
