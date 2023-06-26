package com.xy.base.widget.localmedia

import android.widget.ImageView
import com.luck.picture.lib.entity.LocalMedia

interface SelectLocalMediaListener {
    fun onStartSelectMedia()
    fun onMediaClicked(mediaList:MutableList<LocalMedia>,clickedPosition:Int)
    fun onCreateIconView():ImageView?
    fun onCreateMoreIconView():ImageView?
}