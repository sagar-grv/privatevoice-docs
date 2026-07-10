package com.privatevoicedocs.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.privatevoicedocs.data.database.entity.ConversationEntity
import com.privatevoicedocs.data.database.entity.MessageEntity
import com.privatevoicedocs.data.database.entity.ModelPackEntity
import com.privatevoicedocs.data.database.entity.VoiceProfileEntity
import com.privatevoicedocs.data.database.entity.VoiceRecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao interface ConversationDao {
    @Insert suspend fun insert(entity: ConversationEntity)
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC") fun observeAll(): Flow<List<ConversationEntity>>
    @Query("DELETE FROM conversations WHERE id = :id") suspend fun deleteById(id: String)
}

@Dao interface MessageDao {
    @Insert suspend fun insert(entity: MessageEntity)
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt")
    fun observeForConversation(conversationId: String): Flow<List<MessageEntity>>
}

@Dao interface ModelPackDao {
    @Insert suspend fun insert(entity: ModelPackEntity)
    @Query("SELECT * FROM model_packs ORDER BY modelType, modelName") fun observeAll(): Flow<List<ModelPackEntity>>
}

@Dao interface VoiceProfileDao {
    @Insert suspend fun insert(entity: VoiceProfileEntity)
    @Query("SELECT * FROM voice_profiles ORDER BY createdAt DESC") fun observeAll(): Flow<List<VoiceProfileEntity>>
    @Query("DELETE FROM voice_profiles WHERE id = :id") suspend fun deleteById(id: String)
}

@Dao interface VoiceRecordingDao {
    @Insert suspend fun insert(entity: VoiceRecordingEntity)
    @Query("SELECT * FROM voice_recordings WHERE profileId = :profileId ORDER BY createdAt")
    suspend fun forProfile(profileId: String): List<VoiceRecordingEntity>
}
