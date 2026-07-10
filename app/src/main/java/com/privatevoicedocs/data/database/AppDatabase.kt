package com.privatevoicedocs.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.privatevoicedocs.data.database.dao.ChunkDao
import com.privatevoicedocs.data.database.dao.ConversationDao
import com.privatevoicedocs.data.database.dao.DocumentDao
import com.privatevoicedocs.data.database.dao.MessageDao
import com.privatevoicedocs.data.database.dao.ModelPackDao
import com.privatevoicedocs.data.database.dao.PageDao
import com.privatevoicedocs.data.database.dao.VoiceProfileDao
import com.privatevoicedocs.data.database.dao.VoiceRecordingDao
import com.privatevoicedocs.data.database.entity.ChunkEntity
import com.privatevoicedocs.data.database.entity.ConversationEntity
import com.privatevoicedocs.data.database.entity.DocumentEntity
import com.privatevoicedocs.data.database.entity.MessageEntity
import com.privatevoicedocs.data.database.entity.ModelPackEntity
import com.privatevoicedocs.data.database.entity.PageEntity
import com.privatevoicedocs.data.database.entity.VoiceProfileEntity
import com.privatevoicedocs.data.database.entity.VoiceRecordingEntity

@Database(
    entities = [
        DocumentEntity::class,
        PageEntity::class,
        ChunkEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        ModelPackEntity::class,
        VoiceProfileEntity::class,
        VoiceRecordingEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun pageDao(): PageDao
    abstract fun chunkDao(): ChunkDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun modelPackDao(): ModelPackDao
    abstract fun voiceProfileDao(): VoiceProfileDao
    abstract fun voiceRecordingDao(): VoiceRecordingDao
}
