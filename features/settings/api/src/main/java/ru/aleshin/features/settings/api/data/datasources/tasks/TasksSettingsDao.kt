/*
 * Copyright 2023 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.aleshin.features.settings.api.data.datasources.tasks

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.aleshin.features.settings.api.data.models.TasksSettingsEntity

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
@Dao
interface TasksSettingsDao {

    @Query("SELECT * FROM TasksSettings WHERE id = 0")
    fun fetchSettingsFlow(): Flow<TasksSettingsEntity>

    @Update
    suspend fun updateSettings(entity: TasksSettingsEntity)
}
