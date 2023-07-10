package xy.xy.base.utils.softkey

import android.animation.AnimatorSet
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import xy.xy.base.utils.anim.ViewAnimHelper
import xy.xy.base.utils.exp.getScreenHeight
import xy.xy.base.utils.exp.getSystemBarHeight
import xy.xy.base.utils.exp.getViewPosRect

class MoveKeyBoardController (val dialog:Any): OnSoftKeyBoardChangeListener {
    private var moveView: View?=null
    private var startMarginTop = 0
    private var moveAnim : AnimatorSet?=null


    fun bindMoveView(moveView: View){
        this.moveView = moveView
        val params: ViewGroup.LayoutParams = moveView.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            startMarginTop = params.topMargin
        }
    }

    override fun keyBoardShow(context: Context, height: Int) {
        super.keyBoardShow(context, height)
        val view = when (dialog) {
            is Activity -> dialog.currentFocus
            is Dialog -> dialog.currentFocus
            else -> return
        }
        val rect: Rect = view?.getViewPosRect()?:return
        val currentValues: Int = context.getScreenHeight()-rect.bottom-context.getSystemBarHeight()
        if (currentValues < height) {
            var target: Int = currentValues - height + startMarginTop
            resetAnim(target)
        }
    }

    override fun keyBoardHide(context: Context, height: Int) {
        super.keyBoardHide(context, height)
        resetAnim(startMarginTop)
    }

    private fun resetAnim(marginTop:Int) {
        ViewAnimHelper.cancel(moveAnim)
        moveAnim = ViewAnimHelper.getAnimation()
        val builder = ViewAnimHelper.getBuilder(moveAnim)
        ViewAnimHelper.setMarginTop(builder,marginTop,moveView)
        moveAnim?.start()
    }
}