package xy.xy.base.assembly.picture.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import xy.xy.base.utils.Logger
import xy.xy.base.utils.anim.AppAnimatorListener
import xy.xy.base.utils.anim.ViewAnimHelper
import xy.xy.base.utils.exp.getViewPosRect
import xy.xy.base.utils.exp.removeParent

class ResetPositionController(private val view:ViewGroup,
                              private val positionListener: ResetPositionListener
) {
    private val rootView = view.rootView
    private val moveView by lazy { FrameLayout(view.context) }
    private var currentDownView:ViewGroup?=null
    private var status = TouchStatus.NONO
    private val downLongTime by lazy { 800 }
    private val moveLongTime by lazy { 500 }
    private var moveAnimatorSet :AnimatorSet?=null
    val contentTag by lazy { "contentTag" }
    val delTag by lazy { "delTag" }
    /**
     * 检查移动
     */
    private var downTime = 0L
    private var lastX = 0F
    private var lastY = 0F
    private var downIndex = -1
    private var moveIndex = -1
    private var moveIndexTime = -1L

    fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN->{
                lastX = ev.x
                lastY = ev.y
                moveIndex = -1
                moveIndexTime = -1
                downTime = System.currentTimeMillis()
                if (status != TouchStatus.NONO){
                    status = TouchStatus.NONO
                    return false
                }
                currentDownView = null
                for (index in 0 until view.childCount){
                    val itemView = view.getChildAt(index)
                    val rectF = positionListener.onPositionRecF(index)
                    if (itemView.tag != null && rectF.contains(ev.x,ev.y)){
                        if (itemView is ViewGroup) {
                            currentDownView = itemView
                            status = TouchStatus.CHECK
                        }
                        return false
                    }
                }
                return false
            }
            MotionEvent.ACTION_MOVE->{
                if (status == TouchStatus.CHECK){
                    checkMove(ev)
                    return true
                }else if (status == TouchStatus.MOVE){
                    startMoveIng(ev)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP->{
                return actionUp()
            }
        }
        return false
    }

    private fun checkMove(ev: MotionEvent){
        val currentX = ev.x
        val currentY = ev.y
        for (index in 0 until view.childCount){
            val itemView = view.getChildAt(index)
            val rectF = positionListener.onPositionRecF(index)
            if (itemView.tag != null && rectF.contains(currentX,currentY)){
                if (currentDownView == itemView){
                    val defMoveTime = (System.currentTimeMillis() - downTime) > downLongTime
                    if (defMoveTime){
                        downIndex = index
                        status = TouchStatus.MOVE
                        val delView = currentDownView?.findViewWithTag<View>(delTag)
                        delView?.visibility = View.GONE
                        val contentView = currentDownView?.findViewWithTag<View>(contentTag)
                        if (rootView is ViewGroup){
                            val rect = contentView?.getViewPosRect()?:return
                            val params = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                            params.topMargin = rect.top
                            params.leftMargin = rect.left
                            moveView.layoutParams = params
                            contentView?.removeParent()
                            moveView.addView(contentView)
                            moveView.requestLayout()
                            moveView.removeParent()
                            rootView.addView(moveView)

                        }
                    }
                    return
                }
            }
        }
        status = TouchStatus.NONO
    }


    private fun startMoveIng(ev: MotionEvent){
        val params = moveView.layoutParams
        if (params is ViewGroup.MarginLayoutParams){
            params.leftMargin = params.leftMargin + (ev.x - lastX).toInt()
            params.topMargin = params.topMargin + (ev.y - lastY).toInt()
            moveView.requestLayout()
            Logger.d("leftMargin:${params.leftMargin},topMargin:${params.topMargin}")
        }
        startCheckMove(ev)
        lastX = ev.x
        lastY = ev.y
    }


    private fun startCheckMove(ev: MotionEvent){
        val currentX = ev.x
        val currentY = ev.y
        for (index in 0 until view.childCount){
            val itemView = view.getChildAt(index)
            val rectF = positionListener.onPositionRecF(index)
            if (itemView.tag != null && rectF.contains(currentX,currentY)){
                if (currentDownView == itemView){
                    moveIndex = -1
                    return
                }
                if (moveIndex == index){
                    val defTime = System.currentTimeMillis() - moveIndexTime
                    if (moveLongTime < defTime && status == TouchStatus.MOVE){
                        status = TouchStatus.MOVE_ING
                        currentDownView?.removeParent()
                        if (downIndex > index){
                            view.addView(currentDownView,index)
                        }else{
                            view.addView(currentDownView,index)
                        }
                        moveIndex = -1
                        startMoveAnim(listener = object :AppAnimatorListener(){
                            override fun onAnimationEnd(p0: Animator) {
                                super.onAnimationEnd(p0)
                                status = TouchStatus.MOVE
                            }
                        })
                    }
                    return
                }
                moveIndexTime = System.currentTimeMillis()
                moveIndex = index
                return
            }
        }
    }


    private fun actionUp():Boolean{
        if (status == TouchStatus.MOVE || status == TouchStatus.MOVE_ING){
            val contentView = moveView?.findViewWithTag<View>(contentTag)
            if (contentView != null){
                contentView.removeParent()
                currentDownView?.addView(contentView,0)
                currentDownView?.findViewWithTag<View>(delTag)?.visibility = View.VISIBLE
            }
            startMoveAnim({
                val contentView = currentDownView?.findViewWithTag<View>(contentTag)
                val delView = currentDownView?.findViewWithTag<View>(delTag)
                addSizeAnim(it,contentView,delView)
            },object :AppAnimatorListener(){
                override fun onAnimationEnd(p0: Animator) {
                    super.onAnimationEnd(p0)
                    status = TouchStatus.NONO
                }
            })
            currentDownView = null
            return true
        }
        currentDownView = null
        return false
    }

    private fun addSizeAnim(builder:AnimatorSet.Builder?,vararg contentViewList: View?){
        for (contentView in contentViewList){
            val params = contentView?.layoutParams
            params?.run {
                val height = params.height
                val with = params.width
                params.height  = 0
                params.width = 0
                contentView.alpha = 0F
                contentView.requestLayout()
                ViewAnimHelper.setHeight(builder,height,contentView)
                ViewAnimHelper.setWidth(builder,with,contentView)
                ViewAnimHelper.setAlpha(builder,1F,contentView)
            }
        }
    }

    /**
     * 开始移动view
     */
    fun startMoveAnim(addAnim:(AnimatorSet.Builder?)->Unit={},listener:AppAnimatorListener? = null){
        ViewAnimHelper.cancel(moveAnimatorSet)
        moveAnimatorSet = ViewAnimHelper.getAnimation()
        val build = ViewAnimHelper.getBuilder(moveAnimatorSet)
        for (index in 0 until view.childCount){
            val childView = view.getChildAt(index)
            val positionArray = positionListener.onPosition(index)
            ViewAnimHelper.setMarginLeft(build, positionArray[0], childView)
            ViewAnimHelper.setMarginTop(build, positionArray[1], childView)
        }
        addAnim(build)
        listener?.run {

            moveAnimatorSet?.addListener(listener)
        }

        moveAnimatorSet?.start()
    }


    fun onDestroy(){
        ViewAnimHelper.cancel(moveAnimatorSet)
    }

    private enum class TouchStatus{
        NONO,
        CHECK,
        MOVE,
        MOVE_ING,
    }
}