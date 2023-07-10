package com.yalantis.ucrop.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.yalantis.ucrop.model.ImageState.cropRect
import com.yalantis.ucrop.model.ImageState.currentImageRect
import com.yalantis.ucrop.model.ImageState.currentScale
import com.yalantis.ucrop.model.ImageState.currentAngle
import com.yalantis.ucrop.model.CropParameters.maxResultImageSizeX
import com.yalantis.ucrop.model.CropParameters.maxResultImageSizeY
import com.yalantis.ucrop.model.CropParameters.compressFormat
import com.yalantis.ucrop.model.CropParameters.compressQuality
import com.yalantis.ucrop.model.CropParameters.imageInputPath
import com.yalantis.ucrop.model.CropParameters.imageOutputPath
import com.yalantis.ucrop.model.CropParameters.contentImageInputUri
import com.yalantis.ucrop.model.CropParameters.contentImageOutputUri
import com.yalantis.ucrop.model.CropParameters.exifInfo
import com.yalantis.ucrop.callback.BitmapCropCallback.onBitmapCropped
import com.yalantis.ucrop.callback.BitmapCropCallback.onCropFailure
import okio.sink
import okio.BufferedSource.readAll
import com.yalantis.ucrop.callback.BitmapLoadCallback.onBitmapLoaded
import com.yalantis.ucrop.callback.BitmapLoadCallback.onFailure
import com.yalantis.ucrop.model.ImageState
import com.yalantis.ucrop.model.CropParameters
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.model.ExifInfo
import kotlin.Throws
import com.yalantis.ucrop.task.BitmapCropTask
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.callback.BitmapLoadCallback
import com.yalantis.ucrop.task.BitmapLoadTask.BitmapWorkerResult
import com.yalantis.ucrop.task.BitmapLoadTask
import okhttp3.OkHttpClient
import com.yalantis.ucrop.OkHttpClientStore
import okhttp3.Request
import okhttp3.Response
import okio.BufferedSource
import okio.Sink
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.lang.ref.WeakReference

/**
 * Creates and returns a Bitmap for a given Uri(String url).
 * inSampleSize is calculated based on requiredWidth property. However can be adjusted if OOM occurs.
 * If any EXIF config is found - bitmap is transformed properly.
 */
class BitmapLoadTask(
    context: Context,
    inputUri: Uri, outputUri: Uri?,
    requiredWidth: Int, requiredHeight: Int,
    loadCallback: BitmapLoadCallback
) : AsyncTask<Void?, Void?, BitmapWorkerResult>() {
    private val mContext: WeakReference<Context>
    private var mInputUri: Uri?
    private val mOutputUri: Uri?
    private val mRequiredWidth: Int
    private val mRequiredHeight: Int
    private val mBitmapLoadCallback: BitmapLoadCallback

    class BitmapWorkerResult {
        var mBitmapResult: Bitmap? = null
        var mExifInfo: ExifInfo? = null
        var mBitmapWorkerException: Exception? = null

        constructor(bitmapResult: Bitmap, exifInfo: ExifInfo) {
            mBitmapResult = bitmapResult
            mExifInfo = exifInfo
        }

        constructor(bitmapWorkerException: Exception) {
            mBitmapWorkerException = bitmapWorkerException
        }
    }

    protected override fun doInBackground(vararg params: Void): BitmapWorkerResult {
        val context = mContext.get()
            ?: return BitmapWorkerResult(NullPointerException("context is null"))
        if (mInputUri == null) {
            return BitmapWorkerResult(NullPointerException("Input Uri cannot be null"))
        }
        try {
            processInputUri()
        } catch (e: NullPointerException) {
            return BitmapWorkerResult(e)
        } catch (e: IOException) {
            return BitmapWorkerResult(e)
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            val stream = context.contentResolver.openInputStream(
                mInputUri!!
            )
            BitmapFactory.decodeStream(stream, null, options)
            options.inSampleSize = BitmapLoadUtils.computeSize(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        options.inJustDecodeBounds = false
        var decodeSampledBitmap: Bitmap? = null
        var decodeAttemptSuccess = false
        while (!decodeAttemptSuccess) {
            try {
                val stream = context.contentResolver.openInputStream(
                    mInputUri!!
                )
                try {
                    decodeSampledBitmap = BitmapFactory.decodeStream(stream, null, options)
                    if (options.outWidth == -1 || options.outHeight == -1) {
                        return BitmapWorkerResult(IllegalArgumentException("Bounds for bitmap could not be retrieved from the Uri: [$mInputUri]"))
                    }
                } finally {
                    BitmapLoadUtils.close(stream)
                }
                if (BitmapLoadUtils.checkSize(decodeSampledBitmap, options)) continue
                decodeAttemptSuccess = true
            } catch (error: OutOfMemoryError) {
                Log.e(TAG, "doInBackground: BitmapFactory.decodeFileDescriptor: ", error)
                options.inSampleSize *= 2
            } catch (e: IOException) {
                Log.e(TAG, "doInBackground: ImageDecoder.createSource: ", e)
                return BitmapWorkerResult(
                    IllegalArgumentException(
                        "Bitmap could not be decoded from the Uri: [$mInputUri]",
                        e
                    )
                )
            }
        }
        if (decodeSampledBitmap == null) {
            return BitmapWorkerResult(IllegalArgumentException("Bitmap could not be decoded from the Uri: [$mInputUri]"))
        }
        val exifOrientation = BitmapLoadUtils.getExifOrientation(context, mInputUri!!)
        val exifDegrees = BitmapLoadUtils.exifToDegrees(exifOrientation)
        val exifTranslation = BitmapLoadUtils.exifToTranslation(exifOrientation)
        val exifInfo = ExifInfo(exifOrientation, exifDegrees, exifTranslation)
        val matrix = Matrix()
        if (exifDegrees != 0) {
            matrix.preRotate(exifDegrees.toFloat())
        }
        if (exifTranslation != 1) {
            matrix.postScale(exifTranslation.toFloat(), 1f)
        }
        return if (!matrix.isIdentity) {
            BitmapWorkerResult(
                BitmapLoadUtils.transformBitmap(decodeSampledBitmap, matrix),
                exifInfo
            )
        } else BitmapWorkerResult(decodeSampledBitmap, exifInfo)
    }

    @Throws(NullPointerException::class, IOException::class)
    private fun processInputUri() {
        val inputUriScheme = mInputUri!!.scheme
        Log.d(TAG, "Uri scheme: $inputUriScheme")
        if ("http" == inputUriScheme || "https" == inputUriScheme) {
            try {
                downloadFile(mInputUri!!, mOutputUri)
            } catch (e: NullPointerException) {
                Log.e(TAG, "Downloading failed", e)
                throw e
            } catch (e: IOException) {
                Log.e(TAG, "Downloading failed", e)
                throw e
            }
        } else if ("file" != inputUriScheme && "content" != inputUriScheme) {
            Log.e(TAG, "Invalid Uri scheme $inputUriScheme")
            throw IllegalArgumentException("Invalid Uri scheme$inputUriScheme")
        }
    }

    @Throws(NullPointerException::class, IOException::class)
    private fun downloadFile(inputUri: Uri, outputUri: Uri?) {
        Log.d(TAG, "downloadFile")
        if (outputUri == null) {
            throw NullPointerException("Output Uri is null - cannot download image")
        }
        val context = mContext.get() ?: throw NullPointerException("Context is null")
        val client = OkHttpClientStore.INSTANCE.client
        var source: BufferedSource? = null
        var sink: Sink? = null
        var response: Response? = null
        try {
            val request = Request.Builder()
                .url(inputUri.toString())
                .build()
            response = client.newCall(request).execute()
            source = response.body()!!.source()
            val outputStream = context.contentResolver.openOutputStream(outputUri)
            if (outputStream != null) {
                sink = outputStream.sink()
                source.readAll(sink)
            } else {
                throw NullPointerException("OutputStream for given output Uri is null")
            }
        } finally {
            BitmapLoadUtils.close(source)
            BitmapLoadUtils.close(sink)
            if (response != null) {
                BitmapLoadUtils.close(response.body())
            }
            client.dispatcher().cancelAll()

            // swap uris, because input image was downloaded to the output destination
            // (cropped image will override it later)
            mInputUri = mOutputUri
        }
    }

    override fun onPostExecute(result: BitmapWorkerResult) {
        if (result.mBitmapWorkerException == null) {
            mBitmapLoadCallback.onBitmapLoaded(
                result.mBitmapResult!!,
                result.mExifInfo!!,
                mInputUri!!,
                mOutputUri
            )
        } else {
            mBitmapLoadCallback.onFailure(result.mBitmapWorkerException!!)
        }
    }

    companion object {
        private const val TAG = "BitmapWorkerTask"
    }

    init {
        mContext = WeakReference(context)
        mInputUri = inputUri
        mOutputUri = outputUri
        mRequiredWidth = requiredWidth
        mRequiredHeight = requiredHeight
        mBitmapLoadCallback = loadCallback
    }
}