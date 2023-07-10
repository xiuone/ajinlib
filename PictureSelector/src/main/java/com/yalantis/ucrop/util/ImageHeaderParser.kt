/*
 * Copyright 2015 Google, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY GOOGLE, INC. ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GOOGLE, INC. OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Google, Inc.
 *
 * Adapted for the uCrop library.
 */
package com.yalantis.ucrop.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.util.Log
import com.yalantis.ucrop.callback.BitmapLoadCallback
import com.yalantis.ucrop.task.BitmapLoadTask
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.EglUtils
import kotlin.Throws
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import com.yalantis.ucrop.util.RotationGestureDetector.OnRotationGestureListener
import com.yalantis.ucrop.util.RotationGestureDetector
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

/**
 * A class for parsing the exif orientation from an image header.
 */
class ImageHeaderParser(`is`: InputStream) {
    private val reader: Reader

    /**
     * Parse the orientation from the image header. If it doesn't handle this image type (or this is
     * not an image) it will return a default value rather than throwing an exception.
     *
     * @return The exif orientation if present or -1 if the header couldn't be parsed or doesn't
     * contain an orientation
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val orientation: Int
        get() {
            val magicNumber = reader.uInt16
            return if (!handles(magicNumber)) {
                if (Log.isLoggable(
                        TAG,
                        Log.DEBUG
                    )
                ) {
                    Log.d(
                        TAG,
                        "Parser doesn't handle magic number: $magicNumber"
                    )
                }
                UNKNOWN_ORIENTATION
            } else {
                val exifSegmentLength = moveToExifSegmentAndGetLength()
                if (exifSegmentLength == -1) {
                    if (Log.isLoggable(
                            TAG,
                            Log.DEBUG
                        )
                    ) {
                        Log.d(
                            TAG,
                            "Failed to parse exif segment length, or exif segment not found"
                        )
                    }
                    return UNKNOWN_ORIENTATION
                }
                val exifData = ByteArray(exifSegmentLength)
                parseExifSegment(exifData, exifSegmentLength)
            }
        }

    @Throws(IOException::class)
    private fun parseExifSegment(tempArray: ByteArray, exifSegmentLength: Int): Int {
        val read = reader.read(tempArray, exifSegmentLength)
        if (read != exifSegmentLength) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(
                    TAG, "Unable to read exif segment data"
                            + ", length: " + exifSegmentLength
                            + ", actually read: " + read
                )
            }
            return UNKNOWN_ORIENTATION
        }
        val hasJpegExifPreamble = hasJpegExifPreamble(tempArray, exifSegmentLength)
        return if (hasJpegExifPreamble) {
            parseExifSegment(
                RandomAccessReader(
                    tempArray,
                    exifSegmentLength
                )
            )
        } else {
            if (Log.isLoggable(
                    TAG,
                    Log.DEBUG
                )
            ) {
                Log.d(
                    TAG,
                    "Missing jpeg exif preamble"
                )
            }
            UNKNOWN_ORIENTATION
        }
    }

    private fun hasJpegExifPreamble(exifData: ByteArray?, exifSegmentLength: Int): Boolean {
        var result = exifData != null && exifSegmentLength > JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.size
        if (result) {
            for (i in JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.indices) {
                if (exifData!![i] != JPEG_EXIF_SEGMENT_PREAMBLE_BYTES[i]) {
                    result = false
                    break
                }
            }
        }
        return result
    }

    /**
     * Moves reader to the start of the exif segment and returns the length of the exif segment or
     * `-1` if no exif segment is found.
     */
    @Throws(IOException::class)
    private fun moveToExifSegmentAndGetLength(): Int {
        var segmentId: Short
        var segmentType: Short
        var segmentLength: Int
        while (true) {
            segmentId = reader.uInt8
            if (segmentId.toInt() != SEGMENT_START_ID) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Unknown segmentId=$segmentId")
                }
                return -1
            }
            segmentType = reader.uInt8
            if (segmentType.toInt() == SEGMENT_SOS) {
                return -1
            } else if (segmentType.toInt() == MARKER_EOI) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Found MARKER_EOI in exif segment")
                }
                return -1
            }

            // Segment length includes bytes for segment length.
            segmentLength = reader.uInt16 - 2
            if (segmentType.toInt() != EXIF_SEGMENT_TYPE) {
                val skipped = reader.skip(segmentLength.toLong())
                if (skipped != segmentLength.toLong()) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(
                            TAG, "Unable to skip enough data"
                                    + ", type: " + segmentType
                                    + ", wanted to skip: " + segmentLength
                                    + ", but actually skipped: " + skipped
                        )
                    }
                    return -1
                }
            } else {
                return segmentLength
            }
        }
    }

    private class RandomAccessReader(data: ByteArray?, length: Int) {
        private val data: ByteBuffer
        fun order(byteOrder: ByteOrder?) {
            data.order(byteOrder)
        }

        fun length(): Int {
            return data.remaining()
        }

        fun getInt32(offset: Int): Int {
            return data.getInt(offset)
        }

        fun getInt16(offset: Int): Short {
            return data.getShort(offset)
        }

        init {
            this.data = ByteBuffer.wrap(data)
                .order(ByteOrder.BIG_ENDIAN)
                .limit(length) as ByteBuffer
        }
    }

    private interface Reader {
        @get:Throws(IOException::class)
        val uInt16: Int

        @get:Throws(IOException::class)
        val uInt8: Short

        @Throws(IOException::class)
        fun skip(total: Long): Long

        @Throws(IOException::class)
        fun read(buffer: ByteArray?, byteCount: Int): Int
    }

    private class StreamReader     // Motorola / big endian byte order.
        (private val `is`: InputStream) : Reader {
        @get:Throws(IOException::class)
        override val uInt16: Int
            get() = `is`.read() shl 8 and 0xFF00 or (`is`.read() and 0xFF)

        @get:Throws(IOException::class)
        override val uInt8: Short
            get() = (`is`.read() and 0xFF).toShort()

        @Throws(IOException::class)
        override fun skip(total: Long): Long {
            if (total < 0) {
                return 0
            }
            var toSkip = total
            while (toSkip > 0) {
                val skipped = `is`.skip(toSkip)
                if (skipped > 0) {
                    toSkip -= skipped
                } else {
                    // Skip has no specific contract as to what happens when you reach the end of
                    // the stream. To differentiate between temporarily not having more data and
                    // having finished the stream, we read a single byte when we fail to skip any
                    // amount of data.
                    val testEofByte = `is`.read()
                    if (testEofByte == -1) {
                        break
                    } else {
                        toSkip--
                    }
                }
            }
            return total - toSkip
        }

        @Throws(IOException::class)
        override fun read(buffer: ByteArray?, byteCount: Int): Int {
            var toRead = byteCount
            var read: Int
            while (toRead > 0 && `is`.read(buffer, byteCount - toRead, toRead)
                    .also { read = it } != -1
            ) {
                toRead -= read
            }
            return byteCount - toRead
        }
    }

    companion object {
        private const val TAG = "ImageHeaderParser"

        /**
         * A constant indicating we were unable to parse the orientation from the image either because
         * no exif segment containing orientation data existed, or because of an I/O error attempting to
         * read the exif segment.
         */
        const val UNKNOWN_ORIENTATION = -1
        private const val EXIF_MAGIC_NUMBER = 0xFFD8

        // "MM".
        private const val MOTOROLA_TIFF_MAGIC_NUMBER = 0x4D4D

        // "II".
        private const val INTEL_TIFF_MAGIC_NUMBER = 0x4949
        private const val JPEG_EXIF_SEGMENT_PREAMBLE = "Exif\u0000\u0000"
        private val JPEG_EXIF_SEGMENT_PREAMBLE_BYTES = JPEG_EXIF_SEGMENT_PREAMBLE.toByteArray(
            Charset.forName("UTF-8")
        )
        private const val SEGMENT_SOS = 0xDA
        private const val MARKER_EOI = 0xD9
        private const val SEGMENT_START_ID = 0xFF
        private const val EXIF_SEGMENT_TYPE = 0xE1
        private const val ORIENTATION_TAG_TYPE = 0x0112
        private val BYTES_PER_FORMAT = intArrayOf(0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8)
        private fun parseExifSegment(segmentData: RandomAccessReader): Int {
            val headerOffsetSize = JPEG_EXIF_SEGMENT_PREAMBLE.length
            val byteOrderIdentifier = segmentData.getInt16(headerOffsetSize)
            val byteOrder: ByteOrder
            byteOrder =
                if (byteOrderIdentifier.toInt() == MOTOROLA_TIFF_MAGIC_NUMBER) {
                    ByteOrder.BIG_ENDIAN
                } else if (byteOrderIdentifier.toInt() == INTEL_TIFF_MAGIC_NUMBER) {
                    ByteOrder.LITTLE_ENDIAN
                } else {
                    if (Log.isLoggable(
                            TAG,
                            Log.DEBUG
                        )
                    ) {
                        Log.d(
                            TAG,
                            "Unknown endianness = $byteOrderIdentifier"
                        )
                    }
                    ByteOrder.BIG_ENDIAN
                }
            segmentData.order(byteOrder)
            val firstIfdOffset = segmentData.getInt32(headerOffsetSize + 4) + headerOffsetSize
            val tagCount = segmentData.getInt16(firstIfdOffset).toInt()
            var tagOffset: Int
            var tagType: Int
            var formatCode: Int
            var componentCount: Int
            for (i in 0 until tagCount) {
                tagOffset = calcTagOffset(firstIfdOffset, i)
                tagType = segmentData.getInt16(tagOffset).toInt()

                // We only want orientation.
                if (tagType != ORIENTATION_TAG_TYPE) {
                    continue
                }
                formatCode = segmentData.getInt16(tagOffset + 2).toInt()

                // 12 is max format code.
                if (formatCode < 1 || formatCode > 12) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Got invalid format code = $formatCode")
                    }
                    continue
                }
                componentCount = segmentData.getInt32(tagOffset + 4)
                if (componentCount < 0) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Negative tiff component count")
                    }
                    continue
                }
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(
                        TAG,
                        "Got tagIndex=" + i + " tagType=" + tagType + " formatCode=" + formatCode
                                + " componentCount=" + componentCount
                    )
                }
                val byteCount = componentCount + BYTES_PER_FORMAT[formatCode]
                if (byteCount > 4) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(
                            TAG,
                            "Got byte count > 4, not orientation, continuing, formatCode=$formatCode"
                        )
                    }
                    continue
                }
                val tagValueOffset = tagOffset + 8
                if (tagValueOffset < 0 || tagValueOffset > segmentData.length()) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Illegal tagValueOffset=$tagValueOffset tagType=$tagType")
                    }
                    continue
                }
                if (byteCount < 0 || tagValueOffset + byteCount > segmentData.length()) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Illegal number of bytes for TI tag data tagType=$tagType")
                    }
                    continue
                }

                //assume componentCount == 1 && fmtCode == 3
                return segmentData.getInt16(tagValueOffset).toInt()
            }
            return -1
        }

        private fun calcTagOffset(ifdOffset: Int, tagIndex: Int): Int {
            return ifdOffset + 2 + 12 * tagIndex
        }

        private fun handles(imageMagicNumber: Int): Boolean {
            return imageMagicNumber and EXIF_MAGIC_NUMBER == EXIF_MAGIC_NUMBER || imageMagicNumber == MOTOROLA_TIFF_MAGIC_NUMBER || imageMagicNumber == INTEL_TIFF_MAGIC_NUMBER
        }

        /**
         * Copy exif information represented by originalExif into the file represented by imageOutputPath.
         *
         * @param originalExif The exif info from the original input file
         * @param width output image new width
         * @param height output image new height
         * @param imageOutputPath The path to the output file
         */
        fun copyExif(
            originalExif: ExifInterface,
            width: Int,
            height: Int,
            imageOutputPath: String?
        ) {
            try {
                val newExif = ExifInterface(
                    imageOutputPath!!
                )
                copyExifAttributes(originalExif, newExif, width, height)
            } catch (e: IOException) {
                Log.d(TAG, e.message!!)
            }
        }

        /**
         * Copy exif information from the file represented by imageInputUri into the file represented by imageOutputPath and
         * overwrites it's width and height with the given ones.
         *
         * @param context The context from which to obtain a content resolver
         * @param width output image new width
         * @param height output image new height
         * @param imageInputUri The [Uri] that represents the input file
         * @param imageOutputPath The path to the output file
         */
        fun copyExif(
            context: Context?,
            width: Int,
            height: Int,
            imageInputUri: Uri?,
            imageOutputPath: String?
        ) {
            if (context == null) {
                Log.d(TAG, "context is null")
                return
            }
            var ins: InputStream? = null
            try {
                ins = context.contentResolver.openInputStream(imageInputUri!!)
                val originalExif = ExifInterface(
                    ins!!
                )
                val newExif = ExifInterface(
                    imageOutputPath!!
                )
                copyExifAttributes(originalExif, newExif, width, height)
            } catch (e: IOException) {
                Log.d(TAG, e.message, e)
            } finally {
                if (ins != null) {
                    try {
                        ins.close()
                    } catch (e: IOException) {
                        Log.d(TAG, e.message, e)
                    }
                }
            }
        }

        /**
         * Copy exif information from the file represented by imageInputUri into the file represented by imageOutputUri and
         * overwrites it's width and height with the given ones.
         * This is done by [ExifInterface] through a seekable [FileDescriptor] and this is only possible
         * starting on Lollipop version of Android.
         *
         * @param context The context from which to obtain a content resolver
         * @param width output image new width
         * @param height output image new height
         * @param imageInputUri The [Uri] that represents the input file
         * @param imageOutputUri The [Uri] that represents the output file
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun copyExif(
            context: Context?,
            width: Int,
            height: Int,
            imageInputUri: Uri?,
            imageOutputUri: Uri?
        ) {
            if (context == null) {
                Log.d(TAG, "context is null")
                return
            }
            var ins: InputStream? = null
            var outFd: ParcelFileDescriptor? = null
            try {
                ins = context.contentResolver.openInputStream(imageInputUri!!)
                val originalExif = ExifInterface(
                    ins!!
                )
                outFd = context.contentResolver.openFileDescriptor(imageOutputUri!!, "rw")
                val newExif = ExifInterface(
                    outFd!!.fileDescriptor
                )
                copyExifAttributes(originalExif, newExif, width, height)
            } catch (e: IOException) {
                Log.d(TAG, e.message, e)
            } finally {
                if (ins != null) {
                    try {
                        ins.close()
                    } catch (e: IOException) {
                        Log.d(TAG, e.message, e)
                    }
                }
                if (outFd != null) {
                    try {
                        outFd.close()
                    } catch (e: IOException) {
                        Log.d(TAG, e.message, e)
                    }
                }
            }
        }

        /**
         * Copy exif information represented by originalExif into the file represented by imageOutputUri and overwrites it's
         * width and height with the given ones.
         * This is done by [ExifInterface] through a seekable [FileDescriptor] and this is only possible
         * starting on Lollipop version of Android.
         *
         * @param context The context from which to obtain a content resolver
         * @param originalExif The exif info from the original input file
         * @param width output image new width
         * @param height output image new height
         * @param imageOutputUri The [Uri] that represents the output file
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun copyExif(
            context: Context?,
            originalExif: ExifInterface,
            width: Int,
            height: Int,
            imageOutputUri: Uri?
        ) {
            if (context == null) {
                Log.d(TAG, "context is null")
                return
            }
            var outFd: ParcelFileDescriptor? = null
            try {

                // In order to the ExifInterface be able to validate JPEG info from the file, the FileDescriptor must to be
                // opened en "rw" (read and write) mode
                outFd = context.contentResolver.openFileDescriptor(imageOutputUri!!, "rw")
                val newExif = ExifInterface(
                    outFd!!.fileDescriptor
                )
                copyExifAttributes(originalExif, newExif, width, height)
            } catch (e: IOException) {
                Log.d(TAG, e.message!!)
            } finally {
                if (outFd != null) {
                    try {
                        outFd.close()
                    } catch (e: IOException) {
                        Log.d(TAG, e.message, e)
                    }
                }
            }
        }

        /**
         * Copy Exif attributes from the originalExif to the newExif and overwrites it's width and height with the given ones.
         *
         * @param originalExif Original exif information
         * @param newExif New exif information
         * @param width Width for overwriting into the newExif
         * @param height Height for overwriting into the newExif
         * @throws IOException If it occurs some IO error while trying to save the new exif info.
         */
        @Throws(IOException::class)
        private fun copyExifAttributes(
            originalExif: ExifInterface,
            newExif: ExifInterface,
            width: Int,
            height: Int
        ) {
            val attributes = arrayOf(
                ExifInterface.TAG_F_NUMBER,
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_DATETIME_DIGITIZED,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_FLASH,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_PROCESSING_METHOD,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_SUBSEC_TIME,
                ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
                ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
                ExifInterface.TAG_WHITE_BALANCE
            )
            var value: String?
            for (attribute in attributes) {
                value = originalExif.getAttribute(attribute)
                if (!TextUtils.isEmpty(value)) {
                    newExif.setAttribute(attribute, value)
                }
            }
            newExif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, width.toString())
            newExif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, height.toString())
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, "0")
            newExif.saveAttributes()
        }
    }

    init {
        reader = StreamReader(`is`)
    }
}