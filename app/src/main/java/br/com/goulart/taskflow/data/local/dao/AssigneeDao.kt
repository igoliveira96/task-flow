package br.com.goulart.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.goulart.taskflow.data.local.entity.AssigneeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssigneeDao {

    @Query("SELECT * FROM assignees ORDER BY name")
    fun getAssigneesStream(): Flow<List<AssigneeEntity>>

    @Query("SELECT * FROM assignees WHERE id = :id")
    fun getAssigneeStream(id: Long): Flow<AssigneeEntity?>

    @Insert
    suspend fun insert(assignee: AssigneeEntity): Long

    @Update
    suspend fun update(assignee: AssigneeEntity)

    @Delete
    suspend fun delete(assignee: AssigneeEntity)
}
