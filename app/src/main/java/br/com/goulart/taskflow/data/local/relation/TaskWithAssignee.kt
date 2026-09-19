package br.com.goulart.taskflow.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.com.goulart.taskflow.data.local.entity.AssigneeEntity
import br.com.goulart.taskflow.data.local.entity.TaskEntity

data class TaskWithAssignee(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "assignee_id",
        entityColumn = "id",
    )
    val assignee: AssigneeEntity?,
)
