package com.example.dji_mavic_app

import android.app.Application
import android.content.Context
import android.util.Log
import dji.v5.common.error.IDJIError
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback
import dji.v5.common.register.DJISDKInitEvent

class MApplication : Application() {

    // 🔴 SET THIS TO TRUE WHEN USING EMULATOR
    // 🔴 SET THIS TO FALSE WHEN USING RC PRO
    companion object {
        const val USE_MOCK_MODE = true
    }

    override fun onCreate() {
        super.onCreate()

        if (USE_MOCK_MODE) {
            Log.w("MApplication", "⚠️ MOCK MODE ENABLED: DJI SDK Initialization SKIPPED")
            return // <--- THIS STOPS THE CRASH
        }

        // Real SDK Init (Only runs if USE_MOCK_MODE is false)
        SDKManager.getInstance().init(this, object : SDKManagerCallback {
            override fun onRegisterSuccess() { Log.i("DJI_SDK", "SDK Registered!") }
            override fun onRegisterFailure(error: IDJIError) { Log.e("DJI_SDK", "Register Failed: ${error.description()}") }
            override fun onProductConnect(productId: Int) { Log.i("DJI_SDK", "Drone Connected") }
            override fun onProductDisconnect(productId: Int) { Log.i("DJI_SDK", "Drone Disconnected") }
            override fun onProductChanged(productId: Int) {}
            override fun onInitProcess(event: DJISDKInitEvent?, totalProcess: Int) {}
            override fun onDatabaseDownloadProgress(current: Long, total: Long) {}
        })
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        if (!USE_MOCK_MODE) {
            // Only install SecNeo native helper on real hardware
            try {
                com.secneo.sdk.Helper.install(this)
            } catch (e: Exception) {
                Log.e("MApplication", "SecNeo Install Failed", e)
            }
        }
    }
}