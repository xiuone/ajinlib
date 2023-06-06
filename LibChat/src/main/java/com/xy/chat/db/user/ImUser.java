package com.xy.chat.db.user;


import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xy.base.listener.CheckSameListener;

import java.io.Serializable;
import java.util.Objects;

@DatabaseTable(tableName = "ImUser")
public class ImUser implements CheckSameListener, Serializable {
    @DatabaseField(generatedId = true)
    public long _id = 0;
    @DatabaseField
    public String imUserId;
    @DatabaseField
    public String userIcon;
    @DatabaseField
    public String name;

    private String nameWithAlias;

    public String getNameWithAlias(String formUserId){
        if (TextUtils.isEmpty(nameWithAlias)){
            nameWithAlias = ImUserManger.Companion.getInstance().getImUserAliasName(formUserId,imUserId);
        }
        return TextUtils.isEmpty(nameWithAlias) ? name : nameWithAlias;
    }

    @Override
    public boolean isCompleteSame(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        ImUser imUser = (ImUser) other;
        return Objects.equals(imUserId ,imUser.imUserId) &&
                Objects.equals(userIcon ,imUser.userIcon) &&
                Objects.equals(nameWithAlias ,imUser.nameWithAlias) &&
                Objects.equals(name ,imUser.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImUser imUser = (ImUser) o;
        return Objects.equals(imUserId, imUser.imUserId);
    }

}
