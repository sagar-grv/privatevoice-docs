package com.privatevoicedocs.app

import android.content.Context
import androidx.room.Room
import com.privatevoicedocs.data.database.AppDatabase
import com.privatevoicedocs.data.repository.OfflineDocumentRepository
import com.privatevoicedocs.data.repository.RoomDocumentLocalDataSource
import com.privatevoicedocs.data.storage.AndroidPrivateDocumentStorage
import com.privatevoicedocs.domain.repository.DocumentRepository

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "private-voice-docs.db",
    ).build()

    val documentRepository: DocumentRepository = OfflineDocumentRepository(
        localDataSource = RoomDocumentLocalDataSource(database.documentDao()),
        storage = AndroidPrivateDocumentStorage(context),
    )
}
