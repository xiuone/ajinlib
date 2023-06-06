package com.xy.chat.db.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xy.base.listener.CheckSameListener;

import java.io.Serializable;
import java.util.Objects;

@DatabaseTable(tableName = "ImUserAlias")
public class ImUserAlias implements CheckSameListener, Serializable {
    @DatabaseField(generatedId = true)
    public long _id = 0;
    @DatabaseField
    public String tagUserId;
    @DatabaseField
    public String formUserId;
    @DatabaseField
    public String nameWithAlias;


    @Override
    public boolean isCompleteSame(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        ImUserAlias imUser = (ImUserAlias) other;
        return Objects.equals(tagUserId ,imUser.tagUserId) &&
                Objects.equals(formUserId ,imUser.formUserId) &&
                Objects.equals(nameWithAlias ,imUser.nameWithAlias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImUserAlias that = (ImUserAlias) o;
        return Objects.equals(tagUserId, that.tagUserId) && Objects.equals(formUserId, that.formUserId);
    }
}
