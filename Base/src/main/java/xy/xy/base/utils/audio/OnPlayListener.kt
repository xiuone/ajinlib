package xy.xy.base.utils.audio

interface OnPlayListener {
    /**
     * 文件解码完成，准备播放
     */
    fun onPrepared(){}

    /**
     * 播放完成
     */
    fun onCompletion(){}

    /**
     * 中断播放
     */
    fun onInterrupt(){}

    /**
     * 出错
     * 错误原因
     */
    fun onError(error: String?){}

    /**
     * 播放过程
     * 音频当前播放位置
     */
    fun onPlaying(curPosition: Long){}
}