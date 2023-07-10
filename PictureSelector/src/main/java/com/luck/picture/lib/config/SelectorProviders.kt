package com.luck.picture.lib.config

import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders
import java.util.*

/**
 * @author：luck
 * @date：2023/3/31 4:15 下午
 * @describe：SelectorProviders
 */
class SelectorProviders {
    private val selectionConfigsQueue = LinkedList<SelectorConfig>()
    fun addSelectorConfigQueue(config: SelectorConfig) {
        selectionConfigsQueue.add(config)
    }

    val selectorConfig: SelectorConfig
        get() = if (selectionConfigsQueue.size > 0) selectionConfigsQueue.last else SelectorConfig()

    fun destroy() {
        val selectorConfig: SelectorConfig? = selectorConfig
        if (selectorConfig != null) {
            selectorConfig.destroy()
            selectionConfigsQueue.remove(selectorConfig)
        }
    }

    fun reset() {
        for (i in selectionConfigsQueue.indices) {
            val selectorConfig = selectionConfigsQueue[i]
            if (selectorConfig != null) {
                selectorConfig.destroy()
            }
        }
        selectionConfigsQueue.clear()
    }

    companion object {
        @Volatile
        private var selectorProviders: SelectorProviders? = null
        @JvmStatic
        val instance: SelectorProviders?
            get() {
                if (selectorProviders == null) {
                    synchronized(SelectorProviders::class.java) {
                        if (selectorProviders == null) {
                            selectorProviders = SelectorProviders()
                        }
                    }
                }
                return selectorProviders
            }
    }
}