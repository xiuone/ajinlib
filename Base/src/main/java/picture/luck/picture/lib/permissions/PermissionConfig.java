package picture.luck.picture.lib.permissions;

import android.content.Context;
import com.hjq.permissions.Permission;
import picture.luck.picture.lib.config.SelectMimeType;

/**
 * @author：luck
 * @date：2021/12/11 8:24 下午
 * @describe：PermissionConfig
 */
public class PermissionConfig {

    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";
    /**
     * 当前申请权限
     */
    public static String[] CURRENT_REQUEST_PERMISSION = new String[]{};

    /**
     * 相机权限
     */
    public final static String[] CAMERA = new String[]{Permission.CAMERA};

    /**
     * 获取外部读取权限
     */
    public static String[] getReadPermissionArray(Context context, int chooseMode) {
        if (chooseMode == SelectMimeType.ofImage()) {
            return new String[]{Permission.READ_MEDIA_IMAGES};
        } else if (chooseMode == SelectMimeType.ofVideo()) {
            return new String[]{Permission.READ_MEDIA_VIDEO};
        } else if (chooseMode == SelectMimeType.ofAudio()) {
            return new String[]{Permission.READ_MEDIA_AUDIO};
        }
        return new String[]{Permission.READ_MEDIA_IMAGES,Permission.READ_MEDIA_VIDEO,Permission.READ_MEDIA_AUDIO};
    }

}
