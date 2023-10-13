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
package ru.aleshin.features.settings.impl.presentation.ui.settings.managers

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.features.settings.impl.domain.common.SettingsEitherWrapper
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import ru.aleshin.features.settings.impl.presentation.models.BackupModel
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.06.2023.
 */
internal interface BackupManager {

    suspend fun restoreBackup(uri: Uri): Either<SettingsFailures, BackupModel>

    suspend fun saveBackup(uri: Uri, model: BackupModel): Either<SettingsFailures, Unit>

    class Base @Inject constructor(
        private val applicationContext: Context,
        private val eitherWrapper: SettingsEitherWrapper,
        private val gson: Gson,
    ) : BackupManager {

        override suspend fun restoreBackup(uri: Uri) = eitherWrapper.wrap {
            val contentResolver = applicationContext.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            ZipInputStream(BufferedInputStream(inputStream)).use { input ->
                while (true) {
                    val entry = input.nextEntry ?: break
                    when (entry.name) {
                        Constants.Backup.BACKUP_JSON_NAME -> {
                            var jsonString = input.bufferedReader().use { it.readText() }
                            // TODO: Not work with old model
                            val backup = gson.fromJson(jsonString, BackupModel::class.java) ?: throw IOException()
                            return@wrap backup.copy(
                                categories = backup.categories.filter { it.mainCategory.id != 0 },
                            ) 
                        }
                    }
                }
            }
            throw IOException()
        }

        override suspend fun saveBackup(
            uri: Uri,
            model: BackupModel,
        ) = eitherWrapper.wrap {
            val jsonString = gson.toJson(model)
            val contentResolver = applicationContext.contentResolver
            val outputStream = contentResolver.openOutputStream(uri)
            ZipOutputStream(BufferedOutputStream(outputStream)).use { output ->
                val backupEntry = ZipEntry(Constants.Backup.BACKUP_JSON_NAME)
                output.putNextEntry(backupEntry)
                output.write(jsonString.toByteArray())
                output.finish()
            }
        }
    }
}
