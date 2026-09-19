package br.com.goulart.taskflow

import android.app.Application
import br.com.goulart.taskflow.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TaskFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TaskFlowApplication)
            modules(appModules)
        }
    }
}
