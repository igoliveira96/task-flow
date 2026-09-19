package br.com.goulart.taskflow

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
@ComponentScan("br.com.goulart.taskflow")
class TaskFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<TaskFlowApplication> {
            androidLogger()
            androidContext(this@TaskFlowApplication)
        }
    }
}
