package br.com.goulart.taskflow.di

import androidx.room.Room
import br.com.goulart.taskflow.data.local.TaskFlowDatabase
import br.com.goulart.taskflow.data.local.datasource.AssigneeLocalDataSource
import br.com.goulart.taskflow.data.local.datasource.ProjectLocalDataSource
import br.com.goulart.taskflow.data.local.datasource.RoomAssigneeLocalDataSource
import br.com.goulart.taskflow.data.local.datasource.RoomProjectLocalDataSource
import br.com.goulart.taskflow.data.local.datasource.RoomTaskLocalDataSource
import br.com.goulart.taskflow.data.local.datasource.TaskLocalDataSource
import br.com.goulart.taskflow.data.repository.AssigneeRepository
import br.com.goulart.taskflow.data.repository.DefaultAssigneeRepository
import br.com.goulart.taskflow.data.repository.DefaultProjectRepository
import br.com.goulart.taskflow.data.repository.DefaultTaskRepository
import br.com.goulart.taskflow.data.repository.ProjectRepository
import br.com.goulart.taskflow.data.repository.TaskRepository
import br.com.goulart.taskflow.domain.usecase.GetProjectTasksUseCase
import br.com.goulart.taskflow.domain.usecase.UpdateTaskStatusUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TaskFlowDatabase::class.java,
            "taskflow.db",
        ).build()
    }

    single { get<TaskFlowDatabase>().projectDao() }
    single { get<TaskFlowDatabase>().taskDao() }
    single { get<TaskFlowDatabase>().assigneeDao() }
}

private val localDataSourceModule = module {
    single<ProjectLocalDataSource> { RoomProjectLocalDataSource(get()) }
    single<TaskLocalDataSource> { RoomTaskLocalDataSource(get()) }
    single<AssigneeLocalDataSource> { RoomAssigneeLocalDataSource(get()) }
}

private val repositoryModule = module {
    single<ProjectRepository> { DefaultProjectRepository(get()) }
    single<TaskRepository> { DefaultTaskRepository(get()) }
    single<AssigneeRepository> { DefaultAssigneeRepository(get()) }
}

private val domainModule = module {
    factory { GetProjectTasksUseCase(get()) }
    factory { UpdateTaskStatusUseCase(get()) }
}

val appModules = listOf(
    databaseModule,
    localDataSourceModule,
    repositoryModule,
    domainModule,
)
