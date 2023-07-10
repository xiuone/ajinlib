package xy.xy.base.widget.navi.main

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import xy.xy.base.utils.exp.setOnClick
import xy.xy.base.widget.shadow.view.ShadowLinearLayout


open class NaviView<T: NaviListener> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, )
    : ShadowLinearLayout(context, attrs, defStyleAttr){

    var viewListener : NaviViewListener<T>?=null
    var clickListener : NaviClickedListener<T>?=null
    fun setNewData(data:MutableList<T>){
        removeAllViews()
        for ((index,item) in data.withIndex()){
            addView(index,item)
        }
    }

    private fun addView(index:Int,item:T){
        val newViewListener = viewListener?:return
        val view = newViewListener.createItemView(item)
        view.layoutParams = LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1F)
        val imageView = newViewListener.createItemImageView(index,view,item)
        val textView = newViewListener.createItemTextView(index,view,item)

        imageView?.setImageResource(item.drawRes())
        textView?.setTextColor(item.textColorRes())
        textView?.text = item.titleStr()
        this.addView(view)
        view.setOnClick{
            if (clickListener?.onClickedNavi(it,index,item) == true){
                resetSelectStatus(index)
            }
        }
    }


    fun resetSelectStatus(selectIndex: Int){
        val newViewListener = viewListener?:return
        for (index in 0 until childCount){
            val view = getChildAt(index)
            val imageView = newViewListener.findItemImageView(index,view )
            val textView = newViewListener.findItemTextView(index,view)
            imageView?.isSelected = selectIndex == index
            textView?.isSelected = selectIndex == index
        }
    }
}