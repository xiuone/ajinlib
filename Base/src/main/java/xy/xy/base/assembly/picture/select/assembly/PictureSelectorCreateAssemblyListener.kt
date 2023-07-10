package com.xy.picture.select.assembly

import xy.xy.base.assembly.picture.select.assembly.PictureSelectorAssembly

interface PictureSelectorCreateAssemblyListener{
    fun onCreatePictureSelectorAssembly(): PictureSelectorAssembly?=null
}