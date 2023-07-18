package picture.luck.picture.lib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import picture.luck.picture.lib.basic.PictureCommonFragment;
import picture.luck.picture.lib.entity.LocalMedia;
import picture.luck.picture.lib.manager.SelectedManager;
import picture.luck.picture.lib.permissions.PermissionChecker;
import picture.luck.picture.lib.permissions.PermissionConfig;
import picture.luck.picture.lib.permissions.PermissionResultCallback;
import picture.luck.picture.lib.utils.SdkVersionUtils;
import picture.luck.picture.lib.utils.ToastUtils;
import xy.xy.base.R;

/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：PictureOnlyCameraFragment
 */
public class PictureOnlyCameraFragment extends PictureCommonFragment {
    public static final String TAG = PictureOnlyCameraFragment.class.getSimpleName();

    public static PictureOnlyCameraFragment newInstance() {
        return new PictureOnlyCameraFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 这里只有非内存回收状态下才走，否则当内存不足Fragment被回收后会重复执行
        if (savedInstanceState == null) {
            if (SdkVersionUtils.isQ()) {
                openSelectedCamera();
            } else {
                String[] writePermissionArray = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                PermissionChecker.getInstance().requestPermissions(this, writePermissionArray, new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        openSelectedCamera();
                    }

                    @Override
                    public void onDenied() {
                        handlePermissionDenied(writePermissionArray);
                    }
                });
            }
        }
    }

    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        int selectResultCode = confirmSelect(media, false);
        if (selectResultCode == SelectedManager.ADD_SUCCESS) {
            dispatchTransformResult();
        } else {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        onPermissionExplainEvent(false, null);
        boolean isHasPermissions;
        if (selectorConfig.onPermissionsEventListener != null) {
            isHasPermissions = selectorConfig.onPermissionsEventListener
                    .hasPermissions(this, permissions);
        } else {
            isHasPermissions = PermissionChecker.isCheckCamera(getContext());
        }
        if (isHasPermissions) {
            openSelectedCamera();
        } else {
            if (!PermissionChecker.isCheckCamera(getContext())) {
                ToastUtils.showToast(getContext(), getString(R.string.ps_camera));
            }
            onKeyBackFragmentFinish();
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = new String[]{};
    }
}
