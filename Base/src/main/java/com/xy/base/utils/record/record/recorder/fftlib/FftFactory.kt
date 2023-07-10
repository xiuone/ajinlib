package com.xy.base.utils.record.record.recorder.fftlib

import com.xy.base.utils.Logger.d
import com.xy.base.utils.record.record.recorder.fftlib.FftFactory

/**
 * FFT 数据处理工厂
 */
class FftFactory(level: Level?) {
    private val level = Level.Original
    fun makeFftData(pcmData: ByteArray): ByteArray? {
//        Logger.d(TAG, "pcmData length: %s", pcmData.length);
        if (pcmData.size < 1024) {
            d(TAG, "makeFftData")
            return null
        }
        val doubles = ByteUtils.toHardDouble(ByteUtils.toShorts(pcmData))
        val fft = FFT.fft(doubles, 0)
        return when (level) {
            Level.Original -> ByteUtils.toSoftBytes(fft)
            Level.Maximal -> ByteUtils.toHardBytes(fft)
            else -> ByteUtils.toHardBytes(fft)
        }
    }

    private fun doFftMaximal(fft: DoubleArray): ByteArray {
        val bytes = ByteUtils.toSoftBytes(fft)
        val result = ByteArray(bytes.size)
        for (i in bytes.indices) {
            if (isSimpleData(bytes, i)) {
                result[i] = bytes[i]
            } else {
                result[Math.max(i - 1, 0)] = (bytes[i] / 2).toByte()
                result[Math.min(i + 1, result.size - 1)] = (bytes[i] / 2).toByte()
            }
        }
        return result
    }

    private fun isSimpleData(data: ByteArray, i: Int): Boolean {
        val start = Math.max(0, i - 5)
        val end = Math.min(data.size, i + 5)
        var max: Byte = 0
        var min: Byte = 127
        for (j in start until end) {
            if (data[j] > max) {
                max = data[j]
            }
            if (data[j] < min) {
                min = data[j]
            }
        }
        return data[i] == min || data[i] == max
    }

    /**
     * FFT 处理等级
     */
    enum class Level {
        /**
         * 原始数据，不做任何优化
         */
        Original,

        /**
         * 对音乐进行优化
         */
        Music,

        /**
         * 对人声进行优化
         */
        People,

        /**
         * 极限优化
         */
        Maximal
    }

    companion object {
        private val TAG = FftFactory::class.java.simpleName
    }
}