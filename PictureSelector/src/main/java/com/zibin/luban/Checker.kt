package com.zibin.luban

import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.*

enum class Checker {
    SINGLE;

    private val JPEG_SIGNATURE = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())

    /**
     * Determine if it is JPG.
     *
     * @param is image file input stream
     */
    fun isJPG(`is`: InputStream?): Boolean {
        return isJPG(toByteArray(`is`))
    }

    /**
     * Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
     */
    fun getOrientation(`is`: InputStream?): Int {
        return getOrientation(toByteArray(`is`))
    }

    private fun isJPG(data: ByteArray?): Boolean {
        if (data == null || data.size < 3) {
            return false
        }
        val signatureB = byteArrayOf(data[0], data[1], data[2])
        return Arrays.equals(JPEG_SIGNATURE, signatureB)
    }

    private fun getOrientation(jpeg: ByteArray?): Int {
        if (jpeg == null) {
            return 0
        }
        var offset = 0
        var length = 0

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.size && jpeg[offset++] and 0xFF == 0xFF) {
            val marker: Int = jpeg[offset] and 0xFF

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue
            }
            offset++

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false)
            if (length < 2 || offset + length > jpeg.size) {
                Log.e(TAG, "Invalid length")
                return 0
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 && pack(
                    jpeg,
                    offset + 2,
                    4,
                    false
                ) == 0x45786966 && pack(jpeg, offset + 6, 2, false) == 0
            ) {
                offset += 8
                length -= 8
                break
            }

            // Skip other markers.
            offset += length
            length = 0
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            var tag = pack(jpeg, offset, 4, false)
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e(TAG, "Invalid byte order")
                return 0
            }
            val littleEndian = tag == 0x49492A00

            // Get the offset and check if it is reasonable.
            var count = pack(jpeg, offset + 4, 4, littleEndian) + 2
            if (count < 10 || count > length) {
                Log.e(TAG, "Invalid offset")
                return 0
            }
            offset += count
            length -= count

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian)
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian)
                if (tag == 0x0112) {
                    val orientation = pack(jpeg, offset + 8, 2, littleEndian)
                    when (orientation) {
                        1 -> return 0
                        3 -> return 180
                        6 -> return 90
                        8 -> return 270
                    }
                    Log.e(TAG, "Unsupported orientation")
                    return 0
                }
                offset += 12
                length -= 12
            }
        }
        Log.e(TAG, "Orientation not found")
        return 0
    }

    fun extSuffix(input: InputStreamProvider): String {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(input.open(), null, options)
            options.outMimeType.replace("image/", ".")
        } catch (e: Exception) {
            JPG
        }
    }

    fun needCompress(leastCompressSize: Int, path: String?): Boolean {
        if (leastCompressSize > 0) {
            val source = File(path)
            return source.exists() && source.length() > leastCompressSize.toLong() shl 10
        }
        return true
    }

    private fun pack(bytes: ByteArray, offset: Int, length: Int, littleEndian: Boolean): Int {
        var offset = offset
        var length = length
        var step = 1
        if (littleEndian) {
            offset += length - 1
            step = -1
        }
        var value = 0
        while (length-- > 0) {
            value = value shl 8 or (bytes[offset] and 0xFF)
            offset += step
        }
        return value
    }

    private fun toByteArray(`is`: InputStream?): ByteArray {
        if (`is` == null) {
            return ByteArray(0)
        }
        val buffer = ByteArrayOutputStream()
        var read: Int
        val data = ByteArray(4096)
        try {
            while (`is`.read(data, 0, data.size).also { read = it } != -1) {
                buffer.write(data, 0, read)
            }
        } catch (ignored: Exception) {
            return ByteArray(0)
        } finally {
            try {
                buffer.close()
            } catch (ignored: IOException) {
            }
        }
        return buffer.toByteArray()
    }

    companion object {
        private const val TAG = "Luban"
        private const val JPG = ".jpg"

        /**
         * is content://
         *
         * @param url
         * @return
         */
        fun isContent(url: String?): Boolean {
            return if (TextUtils.isEmpty(url)) {
                false
            } else url!!.startsWith("content://")
        }
    }
}