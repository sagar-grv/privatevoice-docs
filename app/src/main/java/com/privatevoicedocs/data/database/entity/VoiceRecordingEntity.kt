package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "voice_recordings",
    foreignKeys = [ForeignKey(
        entity = VoiceProfileEntity::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("profileId")],
)
data class VoiceRecordingEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val localEncryptedPath: String,
    val languageCode: String,
    val scriptId: String,
    val durationMilliseconds: Long,
    val noiseScore: Float?,
    val clippingDetected: Boolean,
    val accepted: Boolean,
    val createdAt: Long,
)
