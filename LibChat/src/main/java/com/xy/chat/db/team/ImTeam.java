package com.xy.chat.db.team;


import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xy.base.listener.CheckSameListener;

import java.io.Serializable;
import java.util.Objects;

@DatabaseTable(tableName = "ImTeam")
public class ImTeam implements CheckSameListener, Serializable {
    @DatabaseField(generatedId = true)
    public long _id = 0;
    @DatabaseField
    public String imId;
    @DatabaseField
    public String teamId;
    @DatabaseField
    public String teamIcon;
    @DatabaseField
    public String teamName;
    @DatabaseField
    public String introduce;
    @DatabaseField
    public String creatorImId;
    @DatabaseField
    public long memberLimit;
    @DatabaseField
    public long createTime;

    private String nameWithAlias;

    public String getNameWithAlias(){
        if (TextUtils.isEmpty(nameWithAlias)){
            ImTeamAlias teamAlias = ImTeamManger.Companion.getInstance().getTeamAlias(imId,teamId);
            nameWithAlias = teamAlias.nameWithAlias;
        }
        return TextUtils.isEmpty(nameWithAlias) ? teamName : nameWithAlias;
    }


    @Override
    public boolean isCompleteSame(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        ImTeam teamMode = (ImTeam) other;
        return createTime == teamMode.createTime &&
                Objects.equals(imId ,teamMode.imId) &&
                Objects.equals(teamId ,teamMode.teamId) &&
                Objects.equals(teamIcon ,teamMode.teamIcon) &&
                Objects.equals(teamName ,teamMode.teamName) &&
                Objects.equals(creatorImId ,teamMode.creatorImId) &&
                Objects.equals(memberLimit ,teamMode.memberLimit) &&
                Objects.equals(introduce ,teamMode.introduce);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImTeam teamMode = (ImTeam) o;
        return Objects.equals(teamId, teamMode.teamId) && Objects.equals(imId, teamMode.imId);
    }

}
