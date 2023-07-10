package com.xy.picture.widget

import android.widget.ImageView
import picture.luck.picture.lib.entity.LocalMedia

interface SelectLocalMediaListener {
    fun onStartSelectMedia()
    fun onMediaClicked(mediaList:MutableList<LocalMedia>, clickedPosition:Int)
    fun onCreateIconView():ImageView?
    fun onCreateMoreIconView():ImageView?
}