package co.aospa.hub.utils

import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.zip.ZipFile
import kotlin.math.ln
import kotlin.math.pow


object FileUtils {
    fun humanSize(bytes: Long, decimals: Int = 2): String {
        if (bytes == 0L) return "0 Bytes"

        val k = 1024
        val dm = if (decimals < 0) 0 else decimals
        val sizes = arrayOf("Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

        val i = (ln(bytes.toDouble()) / ln(k.toDouble())).toInt()

        return String.format("%.${dm}f %s", bytes / k.toDouble().pow(i.toDouble()), sizes[i])
    }

    fun getHumanSize(bytes: Long, type: String): String {
        return when (type) {
            "GB" -> String.format("%.2f", bytes.toFloat() / (1024 * 1024 * 1024))
            "MB" -> String.format("%.2f", bytes.toFloat() / (1024 * 1024))
            else -> "$bytes"
        }
    }

    fun getSizeType(bytes: Long): String {
        return when {
            bytes < 1024L * 1024L -> "MB"
            else -> "GB"
        }
    }

    fun extractFileNameFromUrl(fileUrl: String): String {
        val uri = Uri.parse(fileUrl)
        val pathSegments = uri.pathSegments
        return pathSegments.last()
    }

    fun checkSHA256(file: File, sha256Hash: String): Boolean {
        val md = MessageDigest.getInstance("SHA-256")
        val fis = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            md.update(buffer, 0, bytesRead)
        }
        fis.close()
        val hashBytes = md.digest()
        val sb = StringBuilder()
        for (byte in hashBytes) {
            sb.append(((byte.toInt() and 0xff) + 0x100).toString(16).substring(1))
        }
        val calculatedSHA256 = sb.toString()
        return calculatedSHA256 == sha256Hash
    }

    fun getZipEntryOffset(zipFile: ZipFile, entryPath: String): Long {
        // Each entry has an header of (30 + n + m) bytes
        // 'n' is the length of the file name
        // 'm' is the length of the extra field
        val FIXED_HEADER_SIZE = 30
        val zipEntries = zipFile.entries()
        var offset: Long = 0
        while (zipEntries.hasMoreElements()) {
            val entry = zipEntries.nextElement()
            val n = entry.name.length
            val m = if (entry.extra == null) 0 else entry.extra.size
            val headerSize = FIXED_HEADER_SIZE + n + m
            offset += headerSize.toLong()
            if (entry.name == entryPath) {
                return offset
            }
            offset += entry.compressedSize
        }
        Log.e("GetZipOffset", "Entry $entryPath not found")
        throw IllegalArgumentException("The given entry was not found")
    }
}