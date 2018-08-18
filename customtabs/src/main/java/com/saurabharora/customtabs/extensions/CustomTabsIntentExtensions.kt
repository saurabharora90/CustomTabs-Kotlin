package com.saurabharora.customtabs.extensions

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.saurabharora.customtabs.CustomTabFallback
import com.saurabharora.customtabs.internal.CustomTabsHelper

/**
 * Opens the URL on a Custom Tab if possible. Otherwise fallback
 *
 * @param activity The host activity.
 * @param uri the Uri to be opened.
 * @param fallback a CustomTabFallback to be used if Custom Tabs is not available.
 */
fun CustomTabsIntent.launchWithFallback(activity: Activity,
                                        uri: Uri,
                                        fallback: CustomTabFallback? = null) {
    val packageName = CustomTabsHelper.getPackageNameToUse(activity)

    //If we cant find a package name, it means there is no browser that supports
    //Chrome Custom Tabs installed. So, we do the fallback
    if (packageName == null) {
        fallback?.openUri(activity, uri)
    } else {
        intent.setPackage(packageName)
        launchUrl(activity, uri)
    }
}