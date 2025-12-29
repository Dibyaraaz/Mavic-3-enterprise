package com.example.dji_mavic_app

import android.webkit.JavascriptInterface

class WebAppInterface(private val viewModel: DroneViewModel) {

    @JavascriptInterface
    fun performTakeoff() {
        viewModel.doTakeoff()
    }

    @JavascriptInterface
    fun performLanding() {
        viewModel.doLanding()
    }
}