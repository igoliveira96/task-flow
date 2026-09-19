package br.com.goulart.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assignees",
    indices = [
        Index(value = ["email"], unique = true),
    ],
)
data class AssigneeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String?,
)
