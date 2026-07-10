package com.privatevoicedocs.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.privatevoicedocs.data.database.entity.ChunkEntity
import com.privatevoicedocs.data.database.entity.DocumentEntity
import com.privatevoicedocs.data.database.entity.ExtractionMethod
import com.privatevoicedocs.data.database.entity.PageEntity
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun documentDeletionCascadesToPagesAndChunks() = runTest {
        val now = 1_700_000_000_000L
        database.documentDao().insert(
            DocumentEntity(
                id = "document-1",
                displayName = "Policy.pdf",
                originalUri = "content://fixture/policy",
                localFilePath = "/private/document-1/source.pdf",
                fileHash = "hash-1",
                mimeType = "application/pdf",
                fileSize = 100,
                pageCount = 1,
                processingStatus = ProcessingStatus.IMPORTED,
                processingProgress = 0,
                processingError = null,
                createdAt = now,
                updatedAt = now,
            ),
        )
        database.pageDao().insert(
            PageEntity(
                id = "page-1",
                documentId = "document-1",
                pageNumber = 1,
                rawText = "raw",
                cleanedText = "cleaned",
                extractionMethod = ExtractionMethod.DIGITAL_TEXT,
                ocrConfidence = null,
                processingStatus = ProcessingStatus.READY,
            ),
        )
        database.chunkDao().insert(
            ChunkEntity(
                id = "chunk-1",
                documentId = "document-1",
                pageNumber = 1,
                chunkIndex = 0,
                sectionHeading = null,
                content = "cleaned",
                tokenEstimate = 2,
                startOffset = 0,
                endOffset = 7,
                embeddingBlob = null,
                createdAt = now,
            ),
        )

        assertEquals(1, database.documentDao().observeAll().first().size)
        assertEquals(1, database.pageDao().countForDocument("document-1"))
        assertEquals(1, database.chunkDao().countForDocument("document-1"))

        database.documentDao().deleteById("document-1")

        assertEquals(0, database.pageDao().countForDocument("document-1"))
        assertEquals(0, database.chunkDao().countForDocument("document-1"))
    }
}
