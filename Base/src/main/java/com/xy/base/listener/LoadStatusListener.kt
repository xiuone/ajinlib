package com.xy.base.listener

import android.view.ViewGroup

interface LoadStatusListener {
    fun onCreateLoadView():ViewGroup?
    fun onCreateContentView():ViewGroup?
    fun onCreateErrorView():ViewGroup?
    fun onCreateUnNetView():ViewGroup?
    fun onCreateEmptyView():ViewGroup?
}