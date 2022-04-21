package com.xy.db.down

import android.content.Context
import com.xy.db.base.BaseDaoManager
import com.xy.db.gen.DownLoadDao
import com.xy.utils.Logger
import com.xy.utils.deleteFile

class DownloadDaoManager(context: Context) : BaseDaoManager<DownLoad,DownLoadDao>(context, DownLoad::class.java) {
    /**
     * 通过数据库找到列表
     */
    fun findListFormDownId(down_Id: Long?):MutableList<DownLoad>{
        if (down_Id == null)return ArrayList()
        return dao?.queryBuilder()?.where(DownLoadDao.Properties.Down_id.eq(down_Id))?.list()?:ArrayList()
    }

    /**
     * 通过任务找到列表
     */
    fun findListFormTaskId(task_Id: Long?):MutableList<DownLoad>{
        if (task_Id == null)return ArrayList()
        return dao?.queryBuilder()?.where(DownLoadDao.Properties.TaskId.eq(task_Id))?.list()?:ArrayList()
    }

    /**
     * 找到运行中的列表
     */
    fun findListFormRun():MutableList<DownLoad>{
        return dao?.queryBuilder()?.where(DownLoadDao.Properties.Status.eq(DownType.DOWN_ING.status))?.list()?:ArrayList()
    }

    /**
     * 根据下载类型
     */
    fun findListFormTYpe(type: Int):MutableList<DownLoad>{
        return dao?.queryBuilder()?.where(DownLoadDao.Properties.Type.eq(type))?.list()?:ArrayList()
    }

    /**
     * 添加
     */
    fun add(taskId:Long?,serviceUrl:String?,localPath:String?,type:Int) : Long{
        return add(taskId, serviceUrl, localPath, null, type)
    }

    fun add(taskId:Long?,serviceUrl:String?,localPath:String?,head:HashMap<String,String>?,type:Int) : Long{
        return add(taskId, serviceUrl, localPath, head,type, 0)
    }

    fun add(taskId:Long?,serviceUrl:String?,localPath:String?,head:HashMap<String,String>?,type:Int,progress:Int) : Long{
        return add(taskId, serviceUrl, localPath, head, progress,type,DownType.DOWN_ING)
    }

    @Synchronized
    fun add(taskId:Long?,serviceUrl:String?,localPath:String?,head:HashMap<String,String>? ,type:Int,progress:Int,state:DownType) : Long{
        val download = DownLoad()
        download.taskId = taskId
        download.serviceUrl = serviceUrl
        download.localPath = localPath
        download.head = head
        download.progress = progress
        download.status = state.status
        download.type = type
        val status = super.add(download)
        Logger.e("保存下载数据状态:$status")
        return status
    }

    /**
     * 更新进度
     */
    fun upProgressFormDownId(down_Id: Long,updateProgress:Int){
        val list = findListFormDownId(down_Id)
        for (item in list){
            upProgress(item,updateProgress)
        }
    }

    fun upProgressFormTaskId(task_Id: Long,updateProgress:Int){
        val list = findListFormTaskId(task_Id)
        for (item in list){
            upProgress(item,updateProgress)
        }
    }

    @Synchronized
    fun upProgress(downLoad: DownLoad?,updateProgress:Int){
        downLoad?.run {
            progress = updateProgress
            update(this)
        }
    }

    /**
     * 更新进度
     */
    fun upStatusFormDownId(down_Id: Long,updateStatus:Int){
        val list = findListFormDownId(down_Id)
        for (item in list){
            upStatus(item,updateStatus)
        }
    }

    fun upStatusFormTaskId(task_Id: Long,updateStatus:Int){
        val list = findListFormTaskId(task_Id)
        for (item in list){
            upStatus(item,updateStatus)
        }
    }

    @Synchronized
    fun upStatus(downLoad: DownLoad?,updateStatus:Int){
        downLoad?.run {
            status = updateStatus
            update(this)
        }
    }


    /**
     * 删除
     */
    fun deleteDownId(down_Id: Long,delFile: Boolean){
        val list = findListFormDownId(down_Id)
        for (item in list){
            delete(item,delFile)
        }
    }

    fun deleteTaskId(task_Id: Long,delFile: Boolean){
        val list = findListFormTaskId(task_Id)
        for (item in list){
            delete(item,delFile)
        }
    }

    @Synchronized
    fun delete(downLoad: DownLoad?,delFile: Boolean){
        downLoad?.run {
            del(this)
            if (delFile) {
                localPath?.deleteFile()
            }
        }
    }

}