package com.saurabharora.customtabs

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
                              lifecycle : Lifecycle,
                              connectionCallback: ConnectionCallback? = null,
                              private val callback : CustomTabsCallback? = null) : ServiceConnectionCallback, LifecycleObserver {

    private var customTabsSession: CustomTabsSession? = null
    private var client: CustomTabsClient? = null
    private var connection: CustomTabsServiceConnection? = null
    private var connectionCallback: ConnectionCallback? = null

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
        this.connectionCallback = connectionCallback
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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun removeReferences() {
        connectionCallback = null
    }

    override fun onServiceConnected(client: CustomTabsClient) {
        this.client = client
        this.client?.warmup(0L)

        connectionCallback?.onCustomTabsConnected()
    }

    override fun onServiceDisconnected() {
        client = null
        customTabsSession = null
        connectionCallback?.onCustomTabsDisconnected()
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
}
