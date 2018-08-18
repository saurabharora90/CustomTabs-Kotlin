// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.saurabharora.customtabs

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.saurabharora.customtabs.internal.CustomTabsHelper
import com.saurabharora.customtabs.internal.ServiceConnection
import com.saurabharora.customtabs.internal.ServiceConnectionCallback

/**
 * This is a helper class to manage the connection to the Custom Tabs Service.
 */
class CustomTabActivityHelper(private val context : Context,
                              private val callback : CustomTabsCallback? = null,
                              lifecycle : Lifecycle) : ServiceConnectionCallback, LifecycleObserver {

    private var customTabsSession: CustomTabsSession? = null
    private var client: CustomTabsClient? = null
    private var connection: CustomTabsServiceConnection? = null

    /**
     * Callback to be called when connected or disconnected from the Custom Tabs Service.
     */
    var connectionCallback: ConnectionCallback? = null

    /**
     * Creates or retrieves an exiting CustomTabsSession.
     *
     * @return a CustomTabsSession.
     */
    val session: CustomTabsSession?
        get() {
            if (client == null) {
                customTabsSession = null
            } else if (customTabsSession == null) {
                customTabsSession = client!!.newSession(callback)
            }
            return customTabsSession
        }

    init {
        lifecycle.addObserver(this)
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun unbindCustomTabsService() {
        connection?.let {
            context.unbindService(it)
            client = null
            customTabsSession = null
            connection = null
        }
    }

    /**
     * Binds the Activity to the Custom Tabs Service.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun bindCustomTabsService() {
        if (client != null) return

        val packageName = CustomTabsHelper.getPackageNameToUse(context) ?: return

        connection = ServiceConnection(this)
        CustomTabsClient.bindCustomTabsService(context, packageName, connection)
    }

    /**
     * @see {@link CustomTabsSession.mayLaunchUrl
     * @return true if call to mayLaunchUrl was accepted.
     */
    fun mayLaunchUrl(uri: Uri, extras: Bundle? = null, otherLikelyBundles: List<Bundle>? = null): Boolean {
        if (client == null) return false

        val session = session ?: return false

        return session.mayLaunchUrl(uri, extras, otherLikelyBundles)
    }

    override fun onServiceConnected(client: CustomTabsClient) {
        this.client = client
        this.client!!.warmup(0L)

        connectionCallback?.onCustomTabsConnected()
    }

    override fun onServiceDisconnected() {
        client = null
        customTabsSession = null
        connectionCallback?.onCustomTabsDisconnected()
    }

    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected.
     */
    interface ConnectionCallback {
        /**
         * Called when the service is connected.
         */
        fun onCustomTabsConnected()

        /**
         * Called when the service is disconnected.
         */
        fun onCustomTabsDisconnected()
    }

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

    companion object {

        /**
         * Opens the URL on a Custom Tab if possible. Otherwise fallback
         *
         * @param activity The host activity.
         * @param customTabsIntent a CustomTabsIntent to be used if Custom Tabs is available.
         * @param uri the Uri to be opened.
         * @param fallback a CustomTabFallback to be used if Custom Tabs is not available.
         */
        fun openCustomTab(activity: Activity,
                          customTabsIntent: CustomTabsIntent,
                          uri: Uri,
                          fallback: CustomTabFallback? = null) {
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)

            //If we cant find a package name, it means there is no browser that supports
            //Chrome Custom Tabs installed. So, we do the fallback
            if (packageName == null) {
                fallback?.openUri(activity, uri)
            } else {
                customTabsIntent.intent.setPackage(packageName)
                customTabsIntent.launchUrl(activity, uri)
            }
        }
    }

}
