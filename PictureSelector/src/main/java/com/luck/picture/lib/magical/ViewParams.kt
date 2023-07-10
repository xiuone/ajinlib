package com.luck.picture.lib.magical

import android.os.Parcel
import android.os.Parcelable
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.basic.InterpolatorFactory.newInterpolator
import kotlin.jvm.JvmOverloads
import com.luck.picture.lib.config.SelectorConfig
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.luck.picture.lib.config.SelectorProviders

class ViewParams : Parcelable {
    var left = 0
    var top = 0
    var width = 0
    var height = 0
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(left)
        dest.writeInt(top)
        dest.writeInt(width)
        dest.writeInt(height)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        left = `in`.readInt()
        top = `in`.readInt()
        width = `in`.readInt()
        height = `in`.readInt()
    }

    companion object {
        val CREATOR: Parcelable.Creator<ViewParams> = object : Parcelable.Creator<ViewParams?> {
            override fun createFromParcel(source: Parcel): ViewParams? {
                return ViewParams(source)
            }

            override fun newArray(size: Int): Array<ViewParams?> {
                return arrayOfNulls(size)
            }
        }
    }
}