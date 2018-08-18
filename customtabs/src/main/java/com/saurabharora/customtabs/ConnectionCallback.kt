package com.saurabharora.customtabs

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