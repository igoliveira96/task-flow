package br.com.goulart.taskflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.goulart.taskflow.data.local.dao.AssigneeDao
import br.com.goulart.taskflow.data.local.dao.ProjectDao
import br.com.goulart.taskflow.data.local.dao.TaskDao
import br.com.goulart.taskflow.data.local.entity.AssigneeEntity
import br.com.goulart.taskflow.data.local.entity.ProjectEntity
import br.com.goulart.taskflow.data.local.entity.TaskEntity

@Database(
    entities = [
        ProjectEntity::class,
        TaskEntity::class,
        AssigneeEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class TaskFlowDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun assigneeDao(): AssigneeDao
}
