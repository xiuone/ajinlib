package com.yalantis.ucrop

import com.yalantis.ucrop.UCropFragment.UCropResult

open interface UCropFragmentCallback {
    /**
     * Return loader status
     * @param showLoader
     */
    fun loadingProgress(showLoader: Boolean)

    /**
     * Return cropping result or error
     * @param result
     */
    fun onCropFinish(result: UCropResult)
}