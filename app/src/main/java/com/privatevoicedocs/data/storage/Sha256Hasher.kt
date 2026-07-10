package com.privatevoicedocs.data.storage

import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest

data class CopyDigest(
    val bytesCopied: Long,
    val sha256: String,
)

object Sha256Hasher {
    fun copyAndHash(input: InputStream, output: OutputStream): CopyDigest {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0L

        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            if (read == 0) continue
            output.write(buffer, 0, read)
            digest.update(buffer, 0, read)
            total += read
        }
        output.flush()

        return CopyDigest(
            bytesCopied = total,
            sha256 = digest.digest().joinToString(separator = "") { byte -> "%02x".format(byte) },
        )
    }
}
