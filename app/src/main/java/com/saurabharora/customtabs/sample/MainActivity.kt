package com.saurabharora.customtabs.sample

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.saurabharora.customtabs.extensions.launchWithFallback


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvNormal = findViewById<TextView>(R.id.tvNormal)
        val tvServiceConnection = findViewById<TextView>(R.id.tvServiceConnection)

        tvNormal.setOnClickListener {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchWithFallback(this, Uri.parse(getString(R.string.default_test_url)))
        }

        tvServiceConnection.setOnClickListener {
            startActivity(Intent(this, ServiceConnectionActivity::class.java))
        }
    }
}
