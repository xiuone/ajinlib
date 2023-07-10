package com.luck.picture.lib.basic

import android.content.Intent
import android.os.Bundle
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.app.PictureAppMaster.pictureSelectorEngine
import com.luck.picture.lib.PictureOnlyCameraFragment.Companion.newInstance
import com.luck.picture.lib.PictureOnlyCameraFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorPreviewFragment.setExternalPreviewData
import com.luck.picture.lib.PictureSelectorSystemFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.FileDirMap
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList

/**
 * @author：luck
 * @date：2021/11/24 10:11 上午
 * @describe：IPictureSelectorCommonEvent
 */
interface IPictureSelectorCommonEvent {
    /**
     * 创建数据查询器
     */
    fun onCreateLoader()

    /**
     * View Layout
     *
     * @return resource Id
     */
    val resourceId: Int

    /**
     * onKey back fragment or finish
     */
    fun onKeyBackFragmentFinish()

    /**
     * fragment onResume
     */
    fun onFragmentResume()

    /**
     * 权限被拒
     */
    fun handlePermissionDenied(permissionArray: Array<String>)

    /**
     * onSavedInstance
     *
     * @param savedInstanceState
     */
    fun reStartSavedInstance(savedInstanceState: Bundle?)

    /**
     * 权限设置结果
     */
    fun handlePermissionSettingResult(permissions: Array<String?>?)

    /**
     * 设置app语言
     */
    fun initAppLanguage()

    /**
     * 重新创建所需引擎
     */
    fun onRecreateEngine()

    /**
     * 选择拍照或拍视频
     */
    fun onSelectedOnlyCamera()

    /**
     * 选择相机类型；拍照、视频、或录音
     */
    fun openSelectedCamera()

    /**
     * 拍照
     */
    fun openImageCamera()

    /**
     * 拍视频
     */
    fun openVideoCamera()

    /**
     * 录音
     */
    fun openSoundRecording()

    /**
     * 选择结果
     *
     * @param currentMedia 当前操作对象
     * @param isSelected   选中状态
     * @return 返回当前选择的状态
     */
    fun confirmSelect(currentMedia: LocalMedia, isSelected: Boolean): Int

    /**
     * 验证共选类型模式可选条件
     *
     * @param media           选中对象
     * @param isSelected      资源是否被选中
     * @param curMimeType     选择的资源类型
     * @param selectVideoSize 已选的视频数量
     * @param fileSize        文件大小
     * @param duration        视频时长
     * @return
     */
    fun checkWithMimeTypeValidity(
        media: LocalMedia?,
        isSelected: Boolean,
        curMimeType: String,
        selectVideoSize: Int,
        fileSize: Long,
        duration: Long
    ): Boolean

    /**
     * 验证单一类型模式可选条件
     *
     * @param media         选中对象
     * @param isSelected    资源是否被选中
     * @param curMimeType   选择的资源类型
     * @param existMimeType 已选的资源类型
     * @param fileSize      文件大小
     * @param duration      视频时长
     * @return
     */
    fun checkOnlyMimeTypeValidity(
        media: LocalMedia?,
        isSelected: Boolean,
        curMimeType: String,
        existMimeType: String?,
        fileSize: Long,
        duration: Long
    ): Boolean

    /**
     * 选择结果数据发生改变
     *
     * @param isAddRemove  isAddRemove  添加还是移除操作
     * @param currentMedia 当前操作的对象
     */
    fun onSelectedChange(isAddRemove: Boolean, currentMedia: LocalMedia?)

    /**
     * 刷新指定数据
     */
    fun onFixedSelectedChange(oldLocalMedia: LocalMedia?)

    /**
     * 分发拍照后生成的LocalMedia
     *
     * @param media
     */
    fun dispatchCameraMediaResult(media: LocalMedia?)

    /**
     * 发送选择数据发生变化的通知
     *
     * @param isAddRemove  添加还是移除操作
     * @param currentMedia 当前操作的对象
     */
    fun sendSelectedChangeEvent(isAddRemove: Boolean, currentMedia: LocalMedia?)

    /**
     * 刷新指定数据
     */
    fun sendFixedSelectedChangeEvent(currentMedia: LocalMedia?)

    /**
     * []
     *
     *
     * isSelectNumberStyle模式下对选择结果编号进行排序
     *
     */
    fun sendChangeSubSelectPositionEvent(adapterChange: Boolean)

    /**
     * 原图选项发生变化
     */
    fun sendSelectedOriginalChangeEvent()

    /**
     * 原图选项发生变化
     */
    fun onCheckOriginalChange()

    /**
     * 编辑资源
     */
    fun onEditMedia(intent: Intent?)

    /**
     * 选择结果回调
     *
     * @param result
     */
    fun onResultEvent(result: ArrayList<LocalMedia>)

    /**
     * 裁剪
     * @param result
     */
    fun onCrop(result: ArrayList<LocalMedia>)

    /**
     * 裁剪
     * @param result
     */
    fun onOldCrop(result: ArrayList<LocalMedia>)

    /**
     * 压缩
     *
     * @param result
     */
    fun onCompress(result: ArrayList<LocalMedia>)

    /**
     * 压缩
     *
     * @param result
     */
    @Deprecated("")
    fun onOldCompress(result: ArrayList<LocalMedia>)

    /**
     * 验证是否需要裁剪
     *
     * @return
     */
    fun checkCropValidity(): Boolean

    /**
     * 验证是否需要裁剪
     *
     * @return
     */
    @Deprecated("")
    fun checkOldCropValidity(): Boolean

    /**
     * 验证是否需要压缩
     *
     * @return
     */
    fun checkCompressValidity(): Boolean

    /**
     * 验证是否需要压缩
     *
     * @return
     */
    @Deprecated("")
    fun checkOldCompressValidity(): Boolean

    /**
     * 验证是否需要做沙盒转换处理
     *
     * @return
     */
    fun checkTransformSandboxFile(): Boolean

    /**
     * 验证是否需要做沙盒转换处理
     *
     * @return
     */
    @Deprecated("")
    fun checkOldTransformSandboxFile(): Boolean

    /**
     * 验证是否需要添加水印
     *
     * @return
     */
    fun checkAddBitmapWatermark(): Boolean

    /**
     * 验证是否需要处理视频缩略图
     */
    fun checkVideoThumbnail(): Boolean

    /**
     * 权限申请
     *
     * @param permissionArray
     */
    fun onApplyPermissionsEvent(event: Int, permissionArray: Array<String?>?)

    /**
     * 权限说明
     *
     * @param isDisplayExplain  是否显示权限说明
     * @param permissionArray   权限组
     */
    fun onPermissionExplainEvent(isDisplayExplain: Boolean, permissionArray: Array<String?>?)

    /**
     * 拦截相机事件
     *
     * @param cameraMode [SelectMimeType]
     */
    fun onInterceptCameraEvent(cameraMode: Int)

    /**
     * 进入Fragment
     */
    fun onEnterFragment()

    /**
     * 退出Fragment
     */
    fun onExitFragment()

    /**
     * show loading
     */
    fun showLoading()

    /**
     * dismiss loading
     */
    fun dismissLoading()
}