package br.com.goulart.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import br.com.goulart.taskflow.data.local.entity.TaskEntity
import br.com.goulart.taskflow.data.local.relation.TaskWithAssignee
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query(
        """
        SELECT * FROM tasks
        WHERE project_id = :projectId
        ORDER BY status, position
        """
    )
    fun getTasksWithAssigneeStream(
        projectId: Long,
    ): Flow<List<TaskWithAssignee>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithAssigneeStream(
        taskId: Long,
    ): Flow<TaskWithAssignee?>

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query(
        """
        UPDATE tasks
        SET status = :status,
            position = :position,
            updated_at = :updatedAt
        WHERE id = :taskId
        """
    )
    suspend fun updateStatus(
        taskId: Long,
        status: String,
        position: Int,
        updatedAt: Long,
    )
}
