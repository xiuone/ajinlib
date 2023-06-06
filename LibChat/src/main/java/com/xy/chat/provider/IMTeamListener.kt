package com.xy.chat.provider

interface IMTeamListener {
    /**
     * 获取Team
     */
    fun getTeamList(userId:String?)

    /**
     * 查询团队信息
     */
    fun queryTeam(userId:String?,teamId:String?)
    /**
     * 查询团队成员列表
     */
    fun queryTeamMember(teamId: String?)

    /**
     * 解散群
     */
    fun dissolveTeam(teamId: String?)

    /**
     * 退出群聊
     */
    fun exitTeam(teamId: String?)

    /**
     * 更新名字和简介
     */
    fun upTeamAnnouncement(teamId: String?,announcement:String?)

    /**
     * 更新名字和简介
     */
    fun upTeamName(teamId: String?,name:String?)

    fun removeTeamMembers(teamId:String?, accounts: MutableList<String>)
    fun addTeamMembers(teamId:String?, accounts: MutableList<String>)

}