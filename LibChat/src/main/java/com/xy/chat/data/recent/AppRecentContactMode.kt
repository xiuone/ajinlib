package com.xy.chat.data.recent

import com.xy.base.widget.recycler.adapter.RecyclerMultiListener
import com.xy.base.listener.CheckSameListener
import com.xy.chat.db.team.ImTeam
import com.xy.chat.db.user.ImUser

data class AppRecentContactMode(var item: RecentContactBean, val user: ImUser?=null, val teamMode: ImTeam?= null)
    : RecyclerMultiListener, CheckSameListener {
    override fun onCreateRecyclerType(): Int = item.sessionType.type

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is AppRecentContactMode) return false
        return item.isCompleteSame(other.item) &&
                user?.isCompleteSame(other.user) == true &&
                teamMode?.isCompleteSame(other.teamMode) == true
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AppRecentContactMode) return false
        return item == other.item
    }
}