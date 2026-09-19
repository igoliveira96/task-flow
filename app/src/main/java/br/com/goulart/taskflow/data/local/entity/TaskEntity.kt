package br.com.goulart.taskflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = AssigneeEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignee_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("project_id"),
        Index("assignee_id"),
        Index(value = ["code"], unique = true),
    ],
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    @ColumnInfo(name = "assignee_id")
    val assigneeId: Long?,
    val title: String,
    val description: String,
    val status: String,
    val position: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
