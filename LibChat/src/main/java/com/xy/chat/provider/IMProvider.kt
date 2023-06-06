package com.xy.chat.provider

object IMProvider {
    private var imRecentListener:IMRecentListener?=null
    private var teamListener:IMTeamListener?=null
    private var imChatListener:ImChatListener?=null

    fun registerImRecentListener(listener: IMRecentListener){
        this.imRecentListener = listener
    }
    fun getImRecent() = imRecentListener

    fun registerImTeamListener(listener: IMTeamListener){
        this.teamListener = listener
    }

    fun getImTeam() = teamListener

    fun registerImChatListener(listener: ImChatListener){
        this.imChatListener = listener
    }
    fun getImChat() = imChatListener

}