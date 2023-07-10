package xy.xy.base.web.down

/**
 * 下载状态
 */
enum class DownStatusEnum(val type:Int) {
    NONO(-1),loading(2),paused(3),failed(4),over(5);
}