package br.com.goulart.taskflow

import android.app.Application
import br.com.goulart.taskflow.di.TaskFlowModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [TaskFlowModule::class])
class TaskFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<TaskFlowApplication> {
            androidLogger()
            androidContext(this@TaskFlowApplication)
        }
    }
}
