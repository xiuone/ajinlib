package com.xy.base.widget.navi.tool

import com.xy.base.R


data class ToolMode(val iconRes:Int,
                    val str:String?,
                    val type:String,
                    val unReadNumberBackGround:Int =  R.drawable.bg_red_fe40_radius_100,
                    val unReadNumber:Int = 0)