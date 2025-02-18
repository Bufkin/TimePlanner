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
package ru.aleshin.features.home.api.data.mappers.template

import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.features.home.api.data.mappers.categories.mapToDomain
import ru.aleshin.features.home.api.data.models.template.TemplateCompound
import ru.aleshin.features.home.api.data.models.template.TemplateDetails
import ru.aleshin.features.home.api.data.models.template.TemplateEntity
import ru.aleshin.features.home.api.domain.entities.template.Template

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
fun TemplateDetails.mapToDomain() = Template(
    startTime = template.startTime.mapToDate(),
    endTime = template.endTime.mapToDate(),
    category = mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(mainCategory.mapToDomain()),
    isImportant = template.isImportant,
    isEnableNotification = template.isEnableNotification,
    isConsiderInStatistics = template.isConsiderInStatistics,
    templateId = template.id,
    repeatEnabled = template.repeatEnabled,
    repeatTimes = repeatTime.let { list ->
        list.map { repeatTimeEntity -> repeatTimeEntity.mapToDomain() }
    },
)

fun Template.mapToData() = TemplateCompound(
    template = TemplateEntity(
        id = templateId,
        startTime = startTime.time,
        endTime = endTime.time,
        categoryId = category.id,
        subCategoryId = subCategory?.id,
        isImportant = isImportant,
        isEnableNotification = isEnableNotification,
        isConsiderInStatistics = isConsiderInStatistics,
        repeatEnabled = repeatEnabled,
    ),
    repeatTimes = repeatTimes.map {
        it.mapToData(templateId)
    },
)
