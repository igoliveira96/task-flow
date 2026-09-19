package br.com.goulart.taskflow

import android.app.Application
import br.com.goulart.taskflow.di.AppContainer
import br.com.goulart.taskflow.di.DefaultAppContainer

class TaskFlowApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
