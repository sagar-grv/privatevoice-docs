package com.privatevoicedocs.document.importing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentImportPolicyTest {
    @Test
    fun `accepts supported PDF and image MIME types case insensitively`() {
        assertTrue(DocumentImportPolicy.isSupported("application/pdf"))
        assertTrue(DocumentImportPolicy.isSupported("IMAGE/JPEG"))
        assertTrue(DocumentImportPolicy.isSupported("image/jpg"))
        assertTrue(DocumentImportPolicy.isSupported("image/png"))
    }

    @Test
    fun `rejects missing and unsupported MIME types`() {
        assertFalse(DocumentImportPolicy.isSupported(null))
        assertFalse(DocumentImportPolicy.isSupported(""))
        assertFalse(DocumentImportPolicy.isSupported("image/gif"))
        assertFalse(DocumentImportPolicy.isSupported("text/plain"))
    }
}
