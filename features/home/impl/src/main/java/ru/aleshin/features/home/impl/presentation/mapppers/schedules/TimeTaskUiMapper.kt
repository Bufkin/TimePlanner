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
package ru.aleshin.features.home.impl.presentation.mapppers.schedules

import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.ParameterizedMapper
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.api.domain.common.TimeTaskStatusChecker
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.impl.presentation.mapppers.categories.mapToDomain
import ru.aleshin.features.home.impl.presentation.mapppers.categories.mapToUi
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface TimeTaskDomainToUiMapper : ParameterizedMapper<TimeTask, TimeTaskUi, Boolean> {
    class Base @Inject constructor(
        private val dateManager: DateManager,
        private val statusManager: TimeTaskStatusChecker,
    ) : TimeTaskDomainToUiMapper {
        override fun map(input: TimeTask, parameter: Boolean) = TimeTaskUi(
            executionStatus = statusManager.fetchStatus(
                input.timeRanges,
                dateManager.fetchCurrentDate(),
            ),
            key = input.key,
            date = input.date,
            startTime = input.timeRanges.from,
            endTime = input.timeRanges.to,
            duration = duration(input.timeRanges),
            leftTime = dateManager.calculateLeftTime(input.timeRanges.to),
            progress = dateManager.calculateProgress(input.timeRanges.from, input.timeRanges.to),
            mainCategory = input.category.mapToUi(),
            subCategory = input.subCategory?.mapToUi(),
            isCompleted = input.isCompleted,
            isImportant = input.isImportant,
            isEnableNotification = input.isEnableNotification,
            isConsiderInStatistics = input.isConsiderInStatistics,
            isTemplate = parameter,
            note = input.note,
        )
    }
}

internal fun TimeTaskUi.mapToDomain() = TimeTask(
    key = key,
    date = date,
    timeRanges = TimeRange(startTime, endTime),
    category = mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    isImportant = isImportant,
    isCompleted = isCompleted,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)
