package br.com.goulart.taskflow.data.mapper

import br.com.goulart.taskflow.data.local.entity.AssigneeEntity
import br.com.goulart.taskflow.data.local.entity.ProjectEntity
import br.com.goulart.taskflow.data.local.entity.TaskEntity
import br.com.goulart.taskflow.data.local.relation.TaskWithAssignee
import br.com.goulart.taskflow.data.model.Assignee
import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus

internal fun ProjectEntity.asExternalModel() = Project(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
)

internal fun Project.asEntity() = ProjectEntity(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
)

internal fun AssigneeEntity.asExternalModel() = Assignee(
    id = id,
    name = name,
    email = email,
)

internal fun Assignee.asEntity() = AssigneeEntity(
    id = id,
    name = name,
    email = email,
)

internal fun TaskWithAssignee.asExternalModel() = Task(
    id = task.id,
    code = task.code,
    projectId = task.projectId,
    assignee = assignee?.asExternalModel(),
    title = task.title,
    description = task.description,
    status = TaskStatus.fromStorageValue(task.status),
    position = task.position,
    createdAt = task.createdAt,
    updatedAt = task.updatedAt,
)

internal fun Task.asEntity() = TaskEntity(
    id = id,
    code = code,
    projectId = projectId,
    assigneeId = assignee?.id,
    title = title,
    description = description,
    status = status.storageValue,
    position = position,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
