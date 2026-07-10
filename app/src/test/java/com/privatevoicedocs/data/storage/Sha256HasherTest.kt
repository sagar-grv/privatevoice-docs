package com.privatevoicedocs.data.storage

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class Sha256HasherTest {
    @Test
    fun `copyAndHash copies bytes and returns deterministic SHA-256`() {
        val source = "abc".encodeToByteArray()
        val destination = ByteArrayOutputStream()

        val result = Sha256Hasher.copyAndHash(ByteArrayInputStream(source), destination)

        assertArrayEquals(source, destination.toByteArray())
        assertEquals(3L, result.bytesCopied)
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            result.sha256,
        )
    }

    @Test
    fun `copyAndHash reports the standard empty-stream digest`() {
        val result = Sha256Hasher.copyAndHash(ByteArrayInputStream(byteArrayOf()), ByteArrayOutputStream())

        assertEquals(0L, result.bytesCopied)
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            result.sha256,
        )
    }
}
