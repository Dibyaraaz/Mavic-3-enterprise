package com.example.dji_mavic_app

import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val viewModel by lazy { DroneViewModel(this) }
    // Used 'by lazy' because 'this' isn't ready until onCreate, safer this way.
    private var webServer: LocalWebServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configure WebView (UI for Primary Operator)
        webView = findViewById(R.id.webview)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        // Connect Bridge
        webView.addJavascriptInterface(WebAppInterface(viewModel), "AndroidDrone")
        webView.loadUrl("file:///android_asset/index.html")

        // 2. Start Server (Access for Secondary Operator)
        try {
            webServer = LocalWebServer(this, viewModel)
            webServer?.start()
            Log.d("WebServer", "Server running on port 8080")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 3. Connect Data Stream to UI
        viewModel.onAltitudeChange = { alt ->
            runOnUiThread {
                webView.evaluateJavascript("updateAltitude($alt)", null)
            }
        }

        viewModel.onStatusChange = { status ->
            runOnUiThread {
                webView.evaluateJavascript("updateStatus('$status')", null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webServer?.stop()
    }
}