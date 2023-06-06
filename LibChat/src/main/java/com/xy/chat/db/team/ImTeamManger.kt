package com.xy.chat.db.team

import com.xy.base.BuildConfig
import com.xy.base.db.base.DaoHelp
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.Logger

class ImTeamManger : ImTeamNotify(){

    private class TeamDao : DaoHelp<ImTeam>("ImTeam", BuildConfig.DB_VERSION, ContextHolder.getContext(), ImTeam::class.java)
    private class TeamAliasDao : DaoHelp<ImTeamAlias>("ImTeamAlias", BuildConfig.DB_VERSION, ContextHolder.getContext(), ImTeamAlias::class.java)

    private val teamDao by lazy { TeamDao() }
    private val teamAliasDao by lazy { TeamAliasDao() }


    fun getNewTeam(imId:String?,teamId: String?,teamIcon: String?,teamName: String?,introduce: String?,
                           creatorImId: String?,memberLimit: Long, createTime:Long = System.currentTimeMillis()): ImTeam {
        val newImTeam = ImTeam()
        newImTeam.imId = imId
        newImTeam.teamId = teamId
        newImTeam.teamIcon = teamIcon
        newImTeam.teamName = teamName
        newImTeam.introduce = introduce
        newImTeam.creatorImId = creatorImId
        newImTeam.memberLimit = memberLimit
        newImTeam.createTime = createTime
        return newImTeam
    }

    fun getTeamFormImId(imId: String?): MutableList<ImTeam>{
        if (imId.isNullOrEmpty()) return ArrayList()
        try {
            return teamDao.queryBuilder?.where()?.eq("imId", imId)?.query()?:ArrayList()
        }catch (e:Exception){
            Logger.d("getUserAlias======e:${e.message}")
        }
        return ArrayList()
    }

    fun getTeam(imId: String?,teamId:String?): ImTeam?{
        if (teamId.isNullOrEmpty()) return null
        try {
            val data = teamDao.queryBuilder?.where()?.eq("teamId", teamId)
                ?.and()?.eq("imId", imId)?.query()
            if (!data.isNullOrEmpty()){
                return data[0]
            }
        }catch (e:Exception){
            Logger.d("getUserAlias======e:${e.message}")
        }
        return null
    }

    fun getTeamFormTeamId(teamId:String?): ImTeam?{
        if (teamId.isNullOrEmpty()) return null
        try {
            val data = teamDao.queryBuilder?.where()?.eq("teamId", teamId)?.query()
            if (!data.isNullOrEmpty()){
                return data[0]
            }
        }catch (e:Exception){
            Logger.d("getUserAlias======e:${e.message}")
        }
        return null
    }

    /**
     * 获取用户的昵称备注
     */
    fun getTeamAlias(imId:String?,teamId:String?): ImTeamAlias? {
        if (imId.isNullOrEmpty() || teamId.isNullOrEmpty())return null
        try {
            val data = teamAliasDao.queryBuilder?.where()?.eq("imId", imId)
                ?.and()?.eq("teamId",teamId)?.query()
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
    fun updateAlias(imId:String?,teamId:String?,name:String){
        if (teamId.isNullOrEmpty() || imId.isNullOrEmpty() || name.isNullOrEmpty())return
        val imTeamAlias = getTeamAlias(imId, teamId)
        val newImTeamAlias = ImTeamAlias()
        newImTeamAlias.imId = imId
        newImTeamAlias.teamId = teamId
        newImTeamAlias.nameWithAlias = name
        if (newImTeamAlias.isCompleteSame(imTeamAlias))return
        newImTeamAlias._id = imTeamAlias?._id?:0L
        teamAliasDao.save(newImTeamAlias)
        val imTeam = getTeam(imId,teamId)
        if (imTeam == null){
            Logger.e("updateAlias=====imUser 为null")
        }else{
            onImTeamChangeCallBack(imTeam)
        }
    }


    fun updateTeam(imId:String?,teamId: String?,teamIcon: String?,teamName: String?,introduce: String?,creatorImId: String?,memberLimit: Long,
                   createTime:Long = System.currentTimeMillis()){
        if (teamId.isNullOrEmpty() )return
        val teamMode = getTeam(imId,teamId)
        val newImTeam = getNewTeam(imId, teamId, teamIcon, teamName, introduce, creatorImId, memberLimit, createTime)
        if (newImTeam.isCompleteSame(teamIcon))return
        newImTeam._id = teamMode?._id?:0L
        teamDao.save(newImTeam)
        onImTeamChangeCallBack(newImTeam)
    }


    fun updateUserTeam(imId: String?,data:MutableList<ImTeam>){
        val oldData = getTeamFormImId(imId)
        if (!oldData.isNullOrEmpty()){
            teamDao.delDatas(oldData)
        }
        teamDao.insert(data)
        onImTeamChangeCallBack(data)
    }


    companion object{
        val instance: ImTeamManger by lazy {  ImTeamManger() }
    }
}