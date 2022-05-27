package com.xy.baselib.widget.multiline.ninegrid

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.xy.baselib.R
import com.xy.baselib.transformation.RoundedCornersTransformation
import com.xy.baselib.exp.getResDimension

class NineGridImageLayout<T :OnNineGridImageListener> @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    NineGridBaseLayout<T>(context, attrs, defStyleAttr) {
    protected var simapleIv = true
    private val radius by lazy { context.getResDimension(R.dimen.dp_5) }
    private val transformation by lazy { MultiTransformation(CenterCrop(), RoundedCornersTransformation(radius)) }
    protected val requestOptions: RequestOptions = RequestOptions.bitmapTransform(transformation)
    @Synchronized
    override fun setData(data: MutableList<T>) {
        super.setData(data)
        for (item in data){
            if (simapleIv){
                addImageVIew(this,item)
            }else{
                val frameLayout = FrameLayout(context)
                this.addView(frameLayout)
                addImageVIew(frameLayout,item)
                addExView(frameLayout, item)
            }
        }
    }

    private fun addImageVIew(viewGroup: ViewGroup,item: T){
        val imageView = ImageView(context)
        val url = item.onMediaUrl()
        Glide.with(context).load(url).apply(requestOptions).into(imageView)
        viewGroup.addView(imageView)
    }

    protected open fun addExView(frameLayout:FrameLayout,item: T){}
}