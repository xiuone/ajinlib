package com.yalantis.ucrop

import okhttp3.OkHttpClient

class OkHttpClientStore private constructor() {
    /**
     * @return stored OkHttpClient if it was already set,
     * or just an instance created via empty constructor
     * and store it
     */
    /**
     * @param client OkHttpClient for downloading bitmap form remote Uri,
     * it may contain any preferences you need
     */
    var client: OkHttpClient? = null
        get() {
            if (field == null) {
                field = OkHttpClient()
            }
            return field
        }

    companion object {
        val INSTANCE = OkHttpClientStore()
    }
}