package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_profiles")
data class VoiceProfileEntity(
    @PrimaryKey val id: String,
    val profileName: String,
    val ownerConsentVersion: String,
    val createdAt: Long,
    val updatedAt: Long,
    val encryptedProfilePath: String?,
    val supportedLanguages: List<String>,
    val defaultSpeakingStyle: String,
    val referenceRecordingsRetained: Boolean,
    val profileStatus: VoiceProfileStatus,
)
