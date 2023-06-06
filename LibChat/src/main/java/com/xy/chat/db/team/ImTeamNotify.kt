package com.xy.chat.db.team

import com.xy.base.utils.notify.NotifyBase

abstract class ImTeamNotify :NotifyBase<ImTeamNotify.ImTeamChangeListener>() {
    fun onImTeamChangeCallBack(teamMode: ImTeam) = findItem { it.onImTeamChangeCallBack(teamMode) }

    fun onImTeamChangeCallBack(data:MutableList<ImTeam>) = findItem { it.onImTeamChangeCallBack(data) }

    fun onImTeamListError(error:String?) = findItem { it.onImTeamListError(error) }


    interface ImTeamChangeListener {
        fun onImTeamChangeCallBack(team: ImTeam){}
        fun onImTeamListError(error:String?){}
        fun onImTeamChangeCallBack(data:MutableList<ImTeam>){}
    }
}