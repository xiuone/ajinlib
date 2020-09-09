package com.jianbian.baselib.view.multi

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable
import kotlin.collections.ArrayList

abstract class MultiSingleTextView<T> : MultiTextView<T> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs)

     override fun actionClicked(view: View, item: T, position: Int) {
         actionClicked(getItemList(),view,item,position)
     }

     override fun actionClicked(views: ArrayList<View>, view: View, item: T, position: Int) {
         for (index in views.indices){
             if (index<getData().size)
                 setTextViewStatus(views[index],getData()[index],index,false)
         }
         setTextViewStatus(view,item,position,true)
     }
}