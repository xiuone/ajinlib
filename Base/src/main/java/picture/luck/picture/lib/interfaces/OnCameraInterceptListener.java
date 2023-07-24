package picture.luck.picture.lib.interfaces;

import androidx.fragment.app.Fragment;

import camerax.luck.lib.camerax.type.CustomCameraType;

/**
 * @author：luck
 * @date：2021/11/23 10:41 上午
 * @describe：OnCameraInterceptListener
 */
public interface OnCameraInterceptListener {

    void openCamera(Fragment fragment, CustomCameraType cameraMode, int requestCode);
}
