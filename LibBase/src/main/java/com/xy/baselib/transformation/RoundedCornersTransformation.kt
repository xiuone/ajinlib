package com.xy.baselib.transformation

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

class RoundedCornersTransformation(roundingRadius: Int) : BitmapTransformation() {
    private val roundingRadius: Int by lazy { if (roundingRadius == 0) 1 else roundingRadius }
    private val ID = "com.infinitybrowser.baselib.transformation.RoundedCornersTransformation"
    private val ID_BYTES = ID.toByteArray(CHARSET)
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return TransformationUtils.roundedCorners(pool, toTransform, roundingRadius)
    }

    override fun equals(o: Any?): Boolean {
        if (o is RoundedCornersTransformation) {
            return roundingRadius == o.roundingRadius
        }
        return false
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode(), Util.hashCode(roundingRadius))
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        val radiusData = ByteBuffer.allocate(4).putInt(roundingRadius).array()
        messageDigest.update(radiusData)
    }
}