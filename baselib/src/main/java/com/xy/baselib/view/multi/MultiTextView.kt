package com.xy.baselib.view.multi

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import kotlin.collections.ArrayList
abstract class MultiTextView<T> : MultiView<T> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs)

     override fun actionClicked(view: View, item: T, position: Int) {
         setTextViewStatus(view,item,position,!getViewTag(view))
     }

     override fun actionClicked(views: ArrayList<View>, view: View, item: T, position: Int) {
         actionClicked(view,item,position)
     }

     override fun actionView(data:MutableList<T>,item: T, position: Int, selectEd: Boolean): View {
         val textView = TextView(context)
         actionTextView(textView,item,position)
         if (ajItemHaveAverage){
             var itemWith = (widthSize - (intervalLeftRight * ajItemAverageNumber - 1)) / ajItemAverageNumber
             textView.layoutParams = LayoutParams(itemWith,ViewGroup.LayoutParams.WRAP_CONTENT)
         }else{
             textView.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
         }
         textView.setPadding(ajItemPaddingLeft,ajItemPaddingTop,ajItemPaddingRight,ajItemPaddingBottom)
         textView.isSingleLine = true
         textView.gravity = Gravity.CENTER
         textView.setTextColor(ajItemTextSelectNotColor)
         if (ajItemSelecNotBackground != null) {
             textView.setBackgroundResource(ajItemSelecNotBackground)
         }
         textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ajItemTextSelectNotSize)
         setTag(textView,selectEd)

         if (measureView(textView) < ajItemMinSize && ajItemMinSize >0){
             textView.minWidth = ajItemMinSize
         }
         return textView
     }

    fun setTextViewStatus(view: View,item: T,position: Int,selectEd: Boolean){
        if (view !is TextView)
            return
        if (selectEd){
            view.setTextColor(ajItemTextSelectColor)
            if (ajItemSelectBackground != null) {
                view.setBackgroundResource(ajItemSelectBackground)
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, ajItemTextSelectSize)
        }else{
            view.setTextColor(ajItemTextSelectNotColor)
            if (ajItemSelecNotBackground != null) {
                view.setBackgroundResource(ajItemSelecNotBackground)
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, ajItemTextSelectNotSize)
        }
        setTag(view,selectEd)
    }

    override fun actionEnd() {

    }

    abstract fun actionTextView(textView: TextView,item: T,position: Int)
 }