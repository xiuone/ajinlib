package camerax.luck.lib.camerax.listener

/**
 * @author：luck
 * @date：2020-01-04 13:38
 * @describe：相机回调监听
 */
interface CameraListener {
    /**
     * 拍照成功返回
     *
     * @param url
     */
    fun onPictureSuccess(url: String)

    /**
     * 录像成功返回
     *
     * @param url
     */
    fun onRecordSuccess(url: String)

    /**
     * 使用相机出错
     *
     */
    fun onError(videoCaptureError: Int, message: String?, cause: Throwable?)
}