package com.yalantis.ucrop.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Oleksii Shliama [https://github.com/shliama] on 6/24/16.
 */
class AspectRatio : Parcelable {
    val aspectRatioTitle: String?
    val aspectRatioX: Float
    val aspectRatioY: Float

    constructor(aspectRatioTitle: String?, aspectRatioX: Float, aspectRatioY: Float) {
        this.aspectRatioTitle = aspectRatioTitle
        this.aspectRatioX = aspectRatioX
        this.aspectRatioY = aspectRatioY
    }

    protected constructor(`in`: Parcel) {
        aspectRatioTitle = `in`.readString()
        aspectRatioX = `in`.readFloat()
        aspectRatioY = `in`.readFloat()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(aspectRatioTitle)
        dest.writeFloat(aspectRatioX)
        dest.writeFloat(aspectRatioY)
    }

    override fun describeContents(): Int {
        return 0
    }
}