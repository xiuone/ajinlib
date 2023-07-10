package xy.xy.base.widget.label.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import xy.xy.base.utils.exp.setOnClick
import xy.xy.base.widget.label.LabelBuilder
import xy.xy.base.widget.label.listener.LabelListener
import xy.xy.base.widget.label.listener.LabelView
import xy.xy.base.widget.label.listener.LabelViewClickedListener
import kotlin.math.max

class LabelMultiView<T>@JvmOverloads  constructor(context: Context, protected val attrs: AttributeSet? = null, defStyleAttr: Int = 0, )
    : FrameLayout(context, attrs, defStyleAttr) , LabelView<T> {
    private val dataList by lazy { ArrayList<T>() }
    private val builder by lazy { LabelBuilder(this,attrs) }
    private var clickedListener : LabelViewClickedListener<T>? = null
    private var viewListener : LabelListener<T>? = null
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    init {
        builder.init()
    }

    override fun setData(data: MutableList<T>) {
        synchronized(this){
            dataList.clear()
            dataList.addAll(data)
            removeAllViews()
            for (item in data){
                if (childCount in 0 until builder.maxNumber){
                    val childView = viewListener?.onCreateLabelView(item)
                    if (clickedListener != null){
                        childView?.setOnClick{
                            clickedListener?.onLabelClicked(childView,item)
                        }
                    }
                    if (childView != null){
                        this.addView(childView)
                        childView.tag = item
                    }
                }else{
                    break
                }
            }
            resetSize(width-paddingLeft-paddingRight)
        }
    }

    override fun getData(): MutableList<T>  = dataList

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == oldw)return
        checkPosition()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        checkPosition()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mainHandler.removeCallbacksAndMessages(null)
    }


    private fun checkPosition(){
        mainHandler.removeCallbacksAndMessages(null)
        val newWidth = width-paddingLeft-paddingRight
        resetSize(newWidth)
        mainHandler.postDelayed({
            checkPosition()
        },if (newWidth < 0) 20 else 2000)
    }

    /**
     * 重新设置位置
     */
    private fun resetSize(width:Int){
        if (width <=0)return
        val count = childCount
        val hashMap = HashMap<Int, LabelTempMode>()
        for (index in 0 until count){
            val childView = getChildAt(index)
            resetSize(width, hashMap, childView)
        }
    }

    private fun resetSize(width: Int, hashMap:HashMap<Int, LabelTempMode>, childView:View){
        val currentWidth = childView.width
        val lineCount = hashMap.size
        if (lineCount == 0){
           addLine(hashMap,childView)
        }else{
            for (index in 0 until lineCount){
                val mode = hashMap[index]
                if (mode != null) {
                    var needWidth = builder.spaceH * mode.views.size + currentWidth
                    for (childIndex in mode.views){
                        needWidth += mode.views[childCount].width
                    }
                    if (width >= needWidth){
                        addLineChild(hashMap,index,childView,)
                        return
                    }
                }
            }
            addLine(hashMap,childView)
        }
    }

    private fun addLine(hashMap:HashMap<Int, LabelTempMode>, childView:View){
        val newList = ArrayList<View>()
        hashMap[hashMap.size] = LabelTempMode(childView.width,childView.height,newList)

        val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        params.topMargin = getMarginTop(hashMap,hashMap.size)
        params.leftMargin = getMarginLeft(ArrayList())
        childView.layoutParams = params

        newList.add(childView)
    }


    private fun addLineChild(hashMap:HashMap<Int, LabelTempMode>, line:Int, childView:View){
        val mode = hashMap[line]?:return

        val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = getMarginLeft(mode.views)
        childView.layoutParams = params

        mode.views.add(childView)

        resetHeadView(hashMap,line,childView)

        mode.width = mode.width + childView.width
        mode.height = max(mode.height,childView.height)
    }

    private fun getMarginTop(hashMap:HashMap<Int, LabelTempMode>, line:Int):Int{
        var marginTop = 0
        for (index in 0 until line){
            val childMode = hashMap[index]
            if (childMode != null){
                marginTop += childMode.height
            }
        }
        marginTop += line * builder.spaceV
        return marginTop
    }

    private fun getMarginLeft(list:MutableList<View>):Int{
        var marginLeft = 0
        for ((index,view) in list.withIndex()){
            marginLeft += view.width
        }
        return marginLeft + list.size * builder.spaceH
    }

    private fun resetHeadView(hashMap:HashMap<Int, LabelTempMode>, line:Int, childView:View){
        val mode = hashMap[line]?:return
        val newHeight = childView.height
        if (newHeight != mode.height) {
            for ((index, view) in mode.views.withIndex()) {
                val missDistance = max(newHeight, mode.height) - view.height
                val params = view.layoutParams
                if (params is LayoutParams) {
                    params.topMargin = getMarginTop(hashMap, line) + missDistance / 2
                }
            }
        }
        else{
            val params = childView.layoutParams
            if (params is LayoutParams)
                params.topMargin = getMarginTop(hashMap,line)
        }
    }

    override fun setOnClickedListener(listener: LabelViewClickedListener<T>) {
        this.clickedListener = listener
    }

    override fun setOnViewListener(listener: LabelListener<T>) {
        this.viewListener = listener
    }
}