package com.xy.baselib.widget.swipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.view.View
import com.xy.baselib.R
import com.xy.utils.getResColor
import com.xy.utils.getResDimension
import com.xy.baselib.widget.swipe.mode.EdgeType

class SwipeDrawHelper (val view: View){
    private val FRAME = 50
    private val context:Context by lazy { view.context }
    private val showArrowPercent by lazy {  0.4f }
    //最大的长度
    private val showMaxHeight by lazy { context.getResDimension(R.dimen.dp_250) }
    //最大的高度
    private val showMaxWidth by lazy { context.getResDimension(R.dimen.dp_100) }
    //箭头高度
    private val arrowHeight by lazy { context.getResDimension(R.dimen.dp_10) }
    //箭头最大宽度
    private val showArrowMaxWidth by lazy { context.getResDimension(R.dimen.dp_8) }
    //箭头显示的最大高度
    private val arrowStokeWidth by lazy { context.getResDimension(R.dimen.dp_2).toFloat() }
    //最小宽度
    private val  minxWidth by lazy { context.getResDimension(R.dimen.dp_6) }
    //最小高度
    private val minxHeight by lazy { context.getResDimension(R.dimen.dp_50) }
    //箭头最小 透明度
    private val arrowMinAlpha = 55;
    fun onDraw(canvas: Canvas,percent:Float,currentEdgeType : EdgeType,currentPointF:PointF) {
        if (percent != 0f) {
            canvas.drawPath(getSwipePath(percent, currentEdgeType, currentPointF), getPathPaint(percent))
            canvas.drawPath(getArrowPath(percent,currentEdgeType, currentPointF), getArrowPaint(percent))
            return
        }
    }

    private fun getPathPaint(percent: Float): Paint {
        val color = context.getResColor(R.color.SwipeBackColor)
        val pathPaint = Paint()
        pathPaint.color = color
        pathPaint.style = Paint.Style.FILL
        pathPaint.isAntiAlias = true
        pathPaint.alpha = (percent * 100).toInt()
        return pathPaint
    }




    private fun getArrowPath(percent: Float,currentEdgeType : EdgeType,currentPointF:PointF): Path {
        val arrowPath = Path()
        val currentWidth = getCurrentWidth(percent)
        var centerX = currentWidth*0.2F
        val arrowWidth = getCurrentArrowWidth(percent)

        var arrowStartX = centerX - arrowWidth/2
        var arrowCenterX = centerX + arrowWidth/2

        arrowPath.moveTo(resetX(currentEdgeType,arrowStartX), currentPointF.y - arrowHeight/2)
        arrowPath.lineTo(resetX(currentEdgeType,arrowCenterX), currentPointF.y)
        arrowPath.lineTo(resetX(currentEdgeType,arrowStartX), currentPointF.y + arrowHeight/2)
        return arrowPath
    }

    /**
     * 获取当前箭头最大宽度
     */
    private fun getCurrentArrowWidth(percent :Float):Float{
        if (percent < showArrowPercent)
            return 0F;
        return showArrowMaxWidth*percent
    }

    /**
     * 获取当前高度
     */
    private fun getCurrentWidth(percent :Float):Float{
        return (showMaxWidth - minxWidth)*percent+minxWidth
    }

    private fun getCurrentHeight(percent: Float):Float{
        return (showMaxHeight-minxHeight)*(1-percent) + minxHeight
    }

    private fun resetX(currentEdgeType : EdgeType,x:Float):Float{
        return if (currentEdgeType == EdgeType.EDGE_LEFT) x else view.width - x
    }



    private fun getSwipePath(percent:Float,currentEdgeType : EdgeType,currentPointF:PointF):Path{
        val path = Path()
        val controlPoints = ArrayList<PointF>()
        val  maxPeakValue = getCurrentHeight(percent)*1.5F
        controlPoints.add(PointF(resetX(currentEdgeType,0F), currentPointF.y - maxPeakValue))
        controlPoints.add(PointF(resetX(currentEdgeType,0F), currentPointF.y  - maxPeakValue * 0.382F))
        controlPoints.add(PointF(resetX(currentEdgeType,getCurrentWidth(percent)), currentPointF.y))
        controlPoints.add(PointF(resetX(currentEdgeType,0F), currentPointF.y  +maxPeakValue * 0.382F))
        controlPoints.add(PointF(resetX(currentEdgeType,0F), currentPointF.y  + maxPeakValue))
        createLine(path,controlPoints,currentEdgeType)
        return path
    }



    private fun createLine(path: Path,controlPoints:MutableList<PointF>,currentEdgeType : EdgeType) {
        if (controlPoints.size <= 0) return
        var isMoveTo = false
        val order = controlPoints.size - 1
        for (index in 0 until FRAME) {
            val delta: Float = index * 1.0f / FRAME
            val pointX = calculateX(order, 0, delta,controlPoints,currentEdgeType)
            val pointY = calculateY(order, 0, delta,controlPoints)
            if (!isMoveTo) {
                path.moveTo(pointX, pointY)
                isMoveTo = true
            } else {
                path.lineTo(pointX, pointY)
            }
        }
        path.close()
    }


    private fun getArrowPaint(percent: Float): Paint {
        val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arrowPaint.color = context.getResColor(R.color.white)
        arrowPaint.strokeCap = Paint.Cap.ROUND
        arrowPaint.strokeWidth = arrowStokeWidth
        arrowPaint.style = Paint.Style.STROKE
        arrowPaint.alpha = (percent*(255-arrowMinAlpha)).toInt()+ arrowMinAlpha
        return arrowPaint
    }


    /*-------------------------------------------------------网上公开的算法看起来很牛逼---------------------------------------------------------*/
    /**
     * 计算x坐标
     * @param i
     * @param j
     * @param t
     * @return
     */
    private fun calculateX(i: Int, j: Int, t: Float,controlPoints:MutableList<PointF>,currentEdgeType : EdgeType): Float {
        if (i == 0 || i == FRAME) {
            return if (currentEdgeType == EdgeType.EDGE_LEFT) 0F else view.width + 1F
        }
        return if (i == 1) {
            (1 - t) * controlPoints[j].x + t * controlPoints[j + 1].x
        } else {
            (1 - t) * calculateX(i - 1, j, t,controlPoints,currentEdgeType) + t * calculateX(i - 1, j + 1, t,controlPoints,currentEdgeType)
        }
    }

    /**
     * 计算y坐标
     * @param i
     * @param j
     * @param t
     * @return
     */
    private fun calculateY(i: Int, j: Int, t: Float,controlPoints:MutableList<PointF>): Float {
        return if (i == 1) {
            (1 - t) * controlPoints[j].y + t * controlPoints[j + 1].y
        } else {
            (1 - t) * calculateY(i - 1, j, t,controlPoints) + t * calculateY(i - 1, j + 1, t,controlPoints)
        }
    }
}