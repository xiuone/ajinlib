package com.jianbian.baselib.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.github.siyamed.shapeimageview.RoundedImageView
import com.jianbian.baselib.R
import com.jianbian.baselib.utils.AppUtil

abstract class MultiImgView<T> :MultiView<T> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun actionClicked(view: View, item: T, position: Int) {
        setImageStatus(view,!getViewTag(view))
    }

    override fun actionClicked(views: ArrayList<View>, view: View, item: T, position: Int) {
        actionClicked(view,item,position)
    }

    override fun actionView(item: T, position: Int, selectEd: Boolean): View {
        val data = getData()
        val imageView = ImageView(context)
        val surplus = data.size%ajItemAverageNumber
        var itemWith = 0
        if (surplus == 0 || (data.size - surplus) > position) {
            itemWith = (widthSize - (intervalLeftRight * ajItemAverageNumber - 1)) / ajItemAverageNumber
        }else{
            itemWith = (widthSize - (intervalLeftRight * surplus - 1)) / surplus
        }
        val params = LayoutParams(itemWith, itemWith)
        imageView.layoutParams = params
        if (ajItemHaveFrame)
            imageView.setPadding(ajItemFrameWidth,ajItemFrameWidth,ajItemFrameWidth,ajItemFrameWidth)
        setImageStatus(imageView,selectEd)
        return imageView
    }

    abstract fun actionImageView(imageView: ImageView,item: T,position: Int)

    private fun setImageStatus(view: View,selectEd: Boolean){
        if (view !is ImageView)
            return
        if (ajItemSelectBackground != null && ajItemSelecNotBackground!= null) {
            if (selectEd){
                view.setBackgroundDrawable(ajItemSelectBackground!!)
            }else{
                view.setBackgroundDrawable(ajItemSelecNotBackground!!)
            }
        }
        setTag(view,selectEd)
    }
}