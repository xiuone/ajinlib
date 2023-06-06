package com.xy.chat.db.team;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xy.base.listener.CheckSameListener;

import java.io.Serializable;
import java.util.Objects;

@DatabaseTable(tableName = "ImTeamAlias")
public class ImTeamAlias implements CheckSameListener, Serializable {
    @DatabaseField(generatedId = true)
    public long _id = 0;
    @DatabaseField
    public String imId;
    @DatabaseField
    public String teamId;
    @DatabaseField
    public String nameWithAlias;


    @Override
    public boolean isCompleteSame(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        ImTeamAlias imUser = (ImTeamAlias) other;
        return Objects.equals(imId ,imUser.imId) &&
                Objects.equals(teamId ,imUser.teamId) &&
                Objects.equals(nameWithAlias ,imUser.nameWithAlias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImTeamAlias that = (ImTeamAlias) o;
        return Objects.equals(imId, that.imId) && Objects.equals(teamId, that.teamId);
    }
}
