package xy.xy.base.db.media;

import androidx.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import xy.xy.base.listener.CheckSameListener;
import xy.xy.base.widget.nine.NineListener;

import java.io.Serializable;
import java.util.Objects;


@DatabaseTable(tableName = "MediaMode")
public class MediaMode implements NineListener, Serializable , CheckSameListener {
    @DatabaseField(generatedId = true)
    public long _id = 0;
    @DatabaseField
    public long id;
    @DatabaseField
    public String url;
    @DatabaseField
    public String tag;
    @DatabaseField
    public String thumbnail;
    @DatabaseField
    public int width;
    @DatabaseField
    public int height;
    @DatabaseField
    public int isBurn;
    @DatabaseField
    public String type;
    @DatabaseField
    public long duration;

    @Override
    public int onWidth() {
        return width;
    }

    @Override
    public int onHeight() {
        return height;
    }

    @Nullable
    @Override
    public String onThumb() {
        return thumbnail == null?url:thumbnail;
    }

    @Override
    public boolean isVideo() {
        return type != null && type.equals(MediaTypeEnum.video.getType());
    }


    @Override
    public boolean isCompleteSame(@Nullable Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return id == ((MediaMode) other).id && Objects.equals(url,((MediaMode) other).url);
    }
}
