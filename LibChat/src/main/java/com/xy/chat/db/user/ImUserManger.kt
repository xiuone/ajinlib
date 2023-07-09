package com.xy.chat.db.user

import com.xy.base.BuildConfig
import com.xy.base.db.base.DaoHelp
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.Logger

class ImUserManger : ImUserNotify(){

    private class ImUserAliasDao : DaoHelp<ImUserAlias>("ImUserAlias", BuildConfig.DB_VERSION, ContextHolder.getContext(), ImUserAlias::class.java)
    private class ImUserDao : DaoHelp<ImUser>("ImUser", BuildConfig.DB_VERSION, ContextHolder.getContext(), ImUser::class.java)

    private val imUserAliasDao by lazy { ImUserAliasDao() }
    private val imUserDao by lazy { ImUserDao() }

    /**
     * 获取用户的昵称备注
     */
    fun getImUserAliasName(form:String?,to:String?):String?  = getImUserAlias(form, to)?.nameWithAlias
    /**
     * 获取用户的昵称备注
     */
    fun getImUserAlias(form:String?,to:String?): ImUserAlias? {
        if (form.isNullOrEmpty() || to.isNullOrEmpty())return null
        try {
            val data = imUserAliasDao.queryBuilder?.where()?.eq("tagUserId", to)?.eq("formUserId",form)?.query()
            if (!data.isNullOrEmpty()){
                return data[0]
            }
        }catch (e:Exception){
            Logger.d("getUserAlias======e:${e.message}")
        }
        return null
    }
    /**
     * 获取当前的im信息
     */
    fun getImUser(imUserId: String?): ImUser?{
        if (imUserId.isNullOrEmpty())return null
        try {
            val data = imUserDao.queryBuilder?.where()?.eq("imUserId", imUserId)?.query()
            if (!data.isNullOrEmpty()){
                return data[0]
            }
        }catch (e:Exception){
            Logger.d("getUserAlias======e:${e.message}")
        }
        return null
    }
    /**
     * 更新备注
     */
    fun updateAlias(form:String?,to:String?,name:String?){
        if (form.isNullOrEmpty() || to.isNullOrEmpty() || name.isNullOrEmpty())return
        val userAlias = getImUserAlias(form, to)
        val newImUserAlias = ImUserAlias()
        newImUserAlias.formUserId = form
        newImUserAlias.tagUserId = to
        newImUserAlias.nameWithAlias = name
        imUserAliasDao.save(newImUserAlias)
        if (newImUserAlias.isCompleteSame(userAlias))return
        newImUserAlias._id = userAlias?._id?:0
        val imUser = getImUser(to)
        if (imUser == null){
            Logger.e("updateAlias=====imUser 为null")
        }else{
            onImUserChangeCallBack(imUser)
        }
    }

    /**
     * 更新用户信息
     */
    fun updateImUser(imUserId: String?,userIcon:String?,name:String?){
        if (imUserId.isNullOrEmpty())return
        val imUser = getImUser(imUserId)
        val newImUser = ImUser()
        newImUser.name = name
        newImUser.imUserId = imUserId
        newImUser.userIcon = userIcon
        if (newImUser.isCompleteSame(imUser))return
        newImUser._id = imUser?._id?:0
        imUserDao.save(imUser)
        onImUserChangeCallBack(newImUser)
    }


    companion object{
        val instance: ImUserManger by lazy {  ImUserManger() }
    }
}