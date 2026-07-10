package com.privatevoicedocs.data.database

import androidx.room.TypeConverter
import com.privatevoicedocs.data.database.entity.ExtractionMethod
import com.privatevoicedocs.data.database.entity.GenerationStatus
import com.privatevoicedocs.data.database.entity.InstallationStatus
import com.privatevoicedocs.data.database.entity.MessageRole
import com.privatevoicedocs.data.database.entity.ModelType
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import com.privatevoicedocs.data.database.entity.VoiceProfileStatus
import java.util.Base64

class DatabaseConverters {
    @TypeConverter
    fun encodeStringList(values: List<String>): String = values.joinToString(",") {
        Base64.getUrlEncoder().withoutPadding().encodeToString(it.encodeToByteArray())
    }

    @TypeConverter
    fun decodeStringList(value: String): List<String> = if (value.isBlank()) emptyList() else value.split(',').map {
        Base64.getUrlDecoder().decode(it).decodeToString()
    }

    @TypeConverter fun processingStatus(value: ProcessingStatus): String = value.name
    @TypeConverter fun processingStatus(value: String): ProcessingStatus = ProcessingStatus.valueOf(value)
    @TypeConverter fun extractionMethod(value: ExtractionMethod): String = value.name
    @TypeConverter fun extractionMethod(value: String): ExtractionMethod = ExtractionMethod.valueOf(value)
    @TypeConverter fun messageRole(value: MessageRole): String = value.name
    @TypeConverter fun messageRole(value: String): MessageRole = MessageRole.valueOf(value)
    @TypeConverter fun generationStatus(value: GenerationStatus): String = value.name
    @TypeConverter fun generationStatus(value: String): GenerationStatus = GenerationStatus.valueOf(value)
    @TypeConverter fun modelType(value: ModelType): String = value.name
    @TypeConverter fun modelType(value: String): ModelType = ModelType.valueOf(value)
    @TypeConverter fun installationStatus(value: InstallationStatus): String = value.name
    @TypeConverter fun installationStatus(value: String): InstallationStatus = InstallationStatus.valueOf(value)
    @TypeConverter fun voiceProfileStatus(value: VoiceProfileStatus): String = value.name
    @TypeConverter fun voiceProfileStatus(value: String): VoiceProfileStatus = VoiceProfileStatus.valueOf(value)
}
