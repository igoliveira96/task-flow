package br.com.goulart.taskflow.di

import android.content.Context
import androidx.room.Room
import br.com.goulart.taskflow.data.local.TaskFlowDatabase
import br.com.goulart.taskflow.data.local.dao.AssigneeDao
import br.com.goulart.taskflow.data.local.dao.ProjectDao
import br.com.goulart.taskflow.data.local.dao.TaskDao
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
fun provideTaskFlowDatabase(
    @Provided context: Context,
): TaskFlowDatabase = Room.databaseBuilder(
    context,
    TaskFlowDatabase::class.java,
    "taskflow.db",
).build()

@Single
fun provideProjectDao(database: TaskFlowDatabase): ProjectDao = database.projectDao()

@Single
fun provideTaskDao(database: TaskFlowDatabase): TaskDao = database.taskDao()

@Single
fun provideAssigneeDao(database: TaskFlowDatabase): AssigneeDao = database.assigneeDao()
