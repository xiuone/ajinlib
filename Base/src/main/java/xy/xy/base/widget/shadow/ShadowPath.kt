package xy.xy.base.widget.shadow

import android.graphics.Path
import android.graphics.RectF
import android.view.View
import xy.xy.base.utils.Logger
import xy.xy.base.widget.shadow.impl.ShadowBuilderImpl
import kotlin.math.max
import kotlin.math.min

open class ShadowPath(private val builderImpl: ShadowBuilderImpl, private val view: View, private val off:Int = 0 ) : Path() {
    var leftTopRadius = 0F
    var rightTopRadius = 0F
    var leftBottomRadius = 0F
    var rightBottomRadius = 0F

    var startLeft = 0F
    var startTop = 0F
    var startRight = 0F
    var startBottom = 0F


    var arrowWay = ShadowArrow.NONO
    var arrowHeight = 0F
    var arrowWidth = 0F
    var arrowOff = 0F
    var arrowIsCenter = false

    override fun reset() {
        super.reset()
        arrowHeight = builderImpl.builder.arrowHeight
        arrowWidth = builderImpl.builder.arrowWidth-off
        arrowOff = builderImpl.builder.arrowOff
        arrowWay = builderImpl.builder.arrowWay
        arrowIsCenter = builderImpl.builder.arrowIsCenter
        val maxRadius = min(view.height, view.height) / 2F
        leftTopRadius = min(builderImpl.leftTopRadius(),maxRadius)*2
        rightTopRadius = min(builderImpl.rightTopRadius(),maxRadius)*2
        leftBottomRadius = min(builderImpl.leftBottomRadius(),maxRadius)*2
        rightBottomRadius = min(builderImpl.rightBottomRadius(),maxRadius)*2

        startLeft = max(builderImpl.arrowLeft(),builderImpl.shadowLeft())+off
        startTop = max(builderImpl.arrowTop(),builderImpl.shadowTop())+off
        startRight =  view.width - max(builderImpl.arrowRight(),builderImpl.shadowRight())-off
        startBottom =  view.height - max(builderImpl.arrowBottom(),builderImpl.shadowBottom())-off

        Logger.d("moveTo===view.height${view.height}  " +
                "startBottom$startBottom   "+
                "y${startBottom-leftBottomRadius/2}"
        )
        moveTo(startLeft, startBottom-leftBottomRadius/2)
        addLeft()
        addTop()
        addRight()
        addBottom()
        close()
    }


    private fun addLeft(){
        if (arrowWidth > 0 && arrowHeight > 0 && (arrowWay == ShadowArrow.LeftTop || arrowWay == ShadowArrow.LeftBottom)) {
            val leftWidth = startBottom - startTop - leftTopRadius - leftBottomRadius
            var arrowStart = when {
                arrowIsCenter -> startBottom - leftWidth / 2  - leftBottomRadius + arrowWidth/2
                arrowWay == ShadowArrow.LeftTop -> startTop + arrowOff + leftTopRadius + arrowWidth
                else -> startBottom - arrowOff - leftBottomRadius
            }

            if ((arrowStart - arrowWidth - leftTopRadius) < startTop){
                arrowStart = startTop + leftTopRadius  + arrowWidth
            }
            if ((arrowStart + leftBottomRadius)> startBottom){
                arrowStart = startBottom - leftBottomRadius
                arrowWidth = leftWidth
            }
            lineTo(startLeft, arrowStart)
            lineTo(startLeft - arrowHeight, arrowStart - arrowWidth / 2)
            lineTo(startLeft, arrowStart - arrowWidth)
        }
        Logger.d("lineTo===view.height${view.height}  " +
                "startBottom$startBottom   "+
                "y${startTop+leftTopRadius}"
        )
        lineTo(startLeft, startTop+leftTopRadius/2)
        val connerRect = RectF()
        connerRect.set(startLeft, startTop, startLeft + leftTopRadius, startTop+leftTopRadius)
        arcTo(connerRect, 180F, 90F)
    }

    private fun addTop(){
        if (arrowWidth > 0 && arrowHeight > 0 && (arrowWay == ShadowArrow.TopLeft || arrowWay == ShadowArrow.TopRight)) {
            val topWidth = startRight - startLeft - leftTopRadius - rightTopRadius
            var arrowStart = when {
                arrowIsCenter -> startRight - topWidth / 2  - rightTopRadius - arrowWidth/2
                arrowWay == ShadowArrow.TopLeft -> startLeft + leftTopRadius + arrowOff
                else -> startRight - rightTopRadius - arrowOff - arrowWidth
            }

            if ((arrowStart + arrowWidth + rightTopRadius) > startRight){
                arrowStart = startRight - rightTopRadius - arrowWidth
            }
            if ((arrowStart - leftTopRadius) < startLeft){
                arrowStart = startLeft + leftTopRadius
                arrowWidth = topWidth
            }
            lineTo(arrowStart, startTop)
            lineTo(arrowStart+arrowWidth/2, startTop - arrowHeight)
            lineTo(arrowStart+arrowWidth, startTop)
        }

        lineTo(startRight - rightTopRadius, startTop)
        val connerRect = RectF()
        connerRect.set(startRight - rightTopRadius, startTop, startRight, startTop+rightTopRadius)
        arcTo(connerRect, 270F, 90F)
    }


    private fun addRight(){
        if (arrowWidth > 0 && arrowHeight > 0 && (arrowWay == ShadowArrow.RightTop || arrowWay == ShadowArrow.RightBottom)) {
            val rightWidth = startBottom - startTop - rightTopRadius - rightBottomRadius
            var arrowStart = when {
                arrowIsCenter ->  startBottom - rightWidth / 2  - rightBottomRadius - arrowWidth/2
                arrowWay == ShadowArrow.RightTop -> startTop + rightTopRadius + arrowOff
                else -> startBottom - rightBottomRadius - arrowOff - arrowWidth
            }

            if ((arrowStart + arrowWidth + rightBottomRadius) > startBottom){
                arrowStart = startBottom - rightBottomRadius - arrowWidth
            }
            if ((arrowStart - rightTopRadius) < startTop){
                arrowStart = startTop + rightTopRadius
                arrowWidth = rightWidth
            }
            lineTo(startRight, arrowStart)
            lineTo(startRight + arrowHeight, arrowStart + arrowWidth / 2)
            lineTo(startRight, arrowStart + arrowWidth)
        }

        lineTo(startRight, startBottom - rightBottomRadius/2)
        val connerRect = RectF()
        connerRect.set(startRight - rightBottomRadius, startBottom - rightBottomRadius, startRight, startBottom)
        arcTo(connerRect, 0f, 90F)
    }

    private fun addBottom(){
        if (arrowWidth > 0 && arrowHeight > 0 && (arrowWay == ShadowArrow.BottomLeft || arrowWay == ShadowArrow.BottomRight)) {
            val bottomWidth = startRight - startLeft - rightBottomRadius - leftBottomRadius
            var arrowStart = when {
                arrowIsCenter ->  startRight - bottomWidth / 2  - rightBottomRadius + arrowWidth/2
                arrowWay == ShadowArrow.BottomRight ->  startRight - rightBottomRadius - arrowOff
                else -> startLeft + leftBottomRadius + arrowOff
            }

            if ((arrowStart - arrowWidth - leftBottomRadius) < startLeft){
                arrowStart = startLeft + rightBottomRadius + arrowWidth
            }
            if ((arrowStart + rightBottomRadius) > startRight){
                arrowStart = startRight - rightBottomRadius
                arrowWidth = bottomWidth
            }
            lineTo(arrowStart, startBottom)
            lineTo(arrowStart - arrowWidth/2, startBottom + arrowHeight)
            lineTo(arrowStart - arrowWidth, startBottom)
        }

        lineTo(startLeft+leftBottomRadius, startBottom)
        val connerRect = RectF()
        connerRect.set(startLeft, startBottom - leftBottomRadius, startLeft+leftBottomRadius, startBottom)
        arcTo(connerRect, 90F, 90F)
    }
}