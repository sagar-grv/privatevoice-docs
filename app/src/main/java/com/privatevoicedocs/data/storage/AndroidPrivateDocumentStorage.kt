package com.privatevoicedocs.data.storage

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidPrivateDocumentStorage(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PrivateDocumentStorage {
    private val appContext = context.applicationContext
    private val documentsRoot = File(appContext.filesDir, "documents")
    private val stagingRoot = File(appContext.filesDir, "document-staging")

    override suspend fun inspect(sourceUri: String): SourceMetadata = withContext(ioDispatcher) {
        val uri = sourceUri.toUri()
        var displayName = uri.lastPathSegment ?: "Imported document"
        var size: Long? = null
        appContext.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0 && !cursor.isNull(nameIndex)) displayName = cursor.getString(nameIndex)
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
            }
        }
        SourceMetadata(displayName, appContext.contentResolver.getType(uri), size)
    }

    override suspend fun copy(
        sourceUri: String,
        documentId: String,
        mimeType: String,
    ): StoredDocument = withContext(ioDispatcher) {
        val stageDirectory = File(stagingRoot, documentId)
        val finalDirectory = File(documentsRoot, documentId)
        stageDirectory.deleteRecursively()
        check(stageDirectory.mkdirs()) { "Unable to create private staging directory" }
        val extension = when (mimeType) {
            "application/pdf" -> "pdf"
            "image/png" -> "png"
            else -> "jpg"
        }
        val stageFile = File(stageDirectory, "source.$extension")
        try {
            val input = appContext.contentResolver.openInputStream(sourceUri.toUri())
                ?: throw FileNotFoundException("The selected file cannot be opened")
            val digest = input.use { source -> stageFile.outputStream().use { output ->
                Sha256Hasher.copyAndHash(source, output)
            } }
            require(digest.bytesCopied > 0) { "The selected file is empty" }
            documentsRoot.mkdirs()
            check(!finalDirectory.exists()) { "A private document directory already exists" }
            try {
                Files.move(stageDirectory.toPath(), finalDirectory.toPath(), StandardCopyOption.ATOMIC_MOVE)
            } catch (_: Exception) {
                Files.move(stageDirectory.toPath(), finalDirectory.toPath())
            }
            StoredDocument(
                localFilePath = File(finalDirectory, stageFile.name).absolutePath,
                sha256 = digest.sha256,
                sizeBytes = digest.bytesCopied,
            )
        } catch (error: Throwable) {
            stageDirectory.deleteRecursively()
            throw error
        }
    }

    override suspend fun delete(documentId: String): Boolean = withContext(ioDispatcher) {
        val finalDirectory = File(documentsRoot, documentId)
        val stageDirectory = File(stagingRoot, documentId)
        val finalDeleted = !finalDirectory.exists() || finalDirectory.deleteRecursively()
        val stageDeleted = !stageDirectory.exists() || stageDirectory.deleteRecursively()
        finalDeleted && stageDeleted && !finalDirectory.exists() && !stageDirectory.exists()
    }

    override suspend fun cleanupStaging() = withContext(ioDispatcher) {
        stagingRoot.listFiles()?.forEach(File::deleteRecursively)
        Unit
    }
}
