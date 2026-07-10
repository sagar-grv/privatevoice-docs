package com.privatevoicedocs.document.importing

object DocumentImportPolicy {
    val pickerMimeTypes: Array<String> = arrayOf(
        "application/pdf",
        "image/jpeg",
        "image/png",
    )

    private val supportedMimeTypes = setOf(
        "application/pdf",
        "image/jpeg",
        "image/jpg",
        "image/png",
    )

    fun isSupported(mimeType: String?): Boolean =
        mimeType?.trim()?.lowercase() in supportedMimeTypes
}
