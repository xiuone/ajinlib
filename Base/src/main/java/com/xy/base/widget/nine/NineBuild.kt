package com.xy.base.widget.nine

import android.util.AttributeSet
import android.view.View
import com.xy.base.utils.exp.getResDimension
import com.xy.base.R

class NineBuild(private val view:View, private val attrs: AttributeSet? = null) {
    private val context by lazy { view.context }
    //间距
    var space  = 0
    var maxSize  = 0
    var minSize  = 0

    var mediaPlayRes = 0
    var mediaPlaySize = 0
    var radius = 0

    fun init(){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineBaseView)
        space = typedArray.getDimensionPixelSize(R.styleable.NineBaseView_nine_spacing_size, context.getResDimension(R.dimen.dp_2))
        maxSize = typedArray.getDimensionPixelSize(R.styleable.NineBaseView_nine_max_size, context.getResDimension(R.dimen.dp_100))
        minSize = typedArray.getDimensionPixelSize(R.styleable.NineBaseView_nine_min_size, context.getResDimension(R.dimen.dp_100))
        mediaPlaySize = typedArray.getDimensionPixelSize(R.styleable.NineBaseView_nine_media_video_play_size, context.getResDimension(R.dimen.dp_0))
        mediaPlayRes = typedArray.getResourceId(R.styleable.NineBaseView_nine_media_video_play_res, R.drawable.bg_transparent)

        typedArray.recycle()

        val roundArray = context.obtainStyledAttributes(attrs, R.styleable.RoundBaseImageView)
        radius = roundArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_radius, radius)
        if (radius <= 0 )
            radius = 0
        roundArray.recycle()
    }

}