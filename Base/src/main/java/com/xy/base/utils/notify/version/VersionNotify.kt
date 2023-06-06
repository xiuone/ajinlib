package com.xy.base.utils.notify.version

import com.xy.base.utils.notify.NotifyBase
import java.io.File

class VersionNotify:NotifyBase<VersionNotify.VersionListener>() {

    fun getNewVersion(version: VersionMode?) = findItem { it.getNewVersion(version) }
    fun getNewVersionError(error: VersionError) = findItem { it.getNewVersionError(error) }
    fun downVersionStart() = findItem { it.downVersionStart() }
    fun downVersionProgress(progress: Float, fileSize: Long, dowSize: Long) = findItem { it.downVersionProgress(progress,fileSize,dowSize) }
    fun downVersionError() = findItem { it.downVersionError() }
    fun downVersionSuc(file: File?) = findItem { it.downVersionSuc(file) }

    interface VersionListener{
        fun getNewVersion(version: VersionMode?){}
        fun getNewVersionError(error: VersionError){}
        fun downVersionStart(){}
        fun downVersionProgress(progress: Float, fileSize: Long, dowSize: Long){}
        fun downVersionError(){}
        fun downVersionSuc(file: File?){}
    }

    companion object{
        val instance by lazy { VersionNotify() }
    }
}