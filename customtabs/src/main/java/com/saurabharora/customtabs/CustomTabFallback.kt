package com.saurabharora.customtabs

import android.app.Activity
import android.net.Uri

/**
 * To be used as a fallback to open the Uri when Custom Tabs is not available.
 */
interface CustomTabFallback {

    /**
     *
     * @param activity The Activity that wants to open the Uri.
     * @param uri The uri to be opened by the fallback.
     */
    fun openUri(activity: Activity, uri: Uri)
}