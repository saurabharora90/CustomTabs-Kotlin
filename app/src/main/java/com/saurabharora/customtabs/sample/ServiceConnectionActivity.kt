package com.saurabharora.customtabs.sample

import android.net.Uri
import com.saurabharora.customtabs.CustomTabActivityHelper
import androidx.browser.customtabs.CustomTabsIntent
import android.widget.EditText
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.saurabharora.customtabs.ConnectionCallback


/**
 * This Activity connect to the Chrome Custom Tabs Service on startup, and allows you to decide
 * when to call mayLaunchUrl.
 */
class ServiceConnectionActivity : AppCompatActivity(), View.OnClickListener, ConnectionCallback {

    private lateinit var urlEditText: EditText
    private lateinit var mayLaunchUrlButton: View

    private val  customTabActivityHelper: CustomTabActivityHelper =
        CustomTabActivityHelper(this, lifecycle, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serviceconnection)

        urlEditText = findViewById(R.id.url)
        mayLaunchUrlButton = findViewById(R.id.button_may_launch_url)
        mayLaunchUrlButton.isEnabled = false
        mayLaunchUrlButton.setOnClickListener(this)

        findViewById<Button>(R.id.start_custom_tab).setOnClickListener(this)
    }

    override fun onCustomTabsConnected() {
        mayLaunchUrlButton.isEnabled = true
    }

    override fun onCustomTabsDisconnected() {
        mayLaunchUrlButton.isEnabled = false
    }

    override fun onClick(view: View) {
        val viewId = view.id
        val uri = Uri.parse(urlEditText.text.toString())
        when (viewId) {
            R.id.button_may_launch_url -> customTabActivityHelper.mayLaunchUrl(uri)
            R.id.start_custom_tab -> {
                val customTabsIntent = CustomTabsIntent.Builder(customTabActivityHelper.session)
                        .build()
                CustomTabActivityHelper.openCustomTab(
                        this, customTabsIntent, uri)
            }
        }//Unkown View Clicked
    }
}