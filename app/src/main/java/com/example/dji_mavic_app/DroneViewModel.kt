package com.example.dji_mavic_app

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.KeyManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class DroneViewModel(private val context: Context) { // <--- Added Context to init DB

    private val isMockMode = true

    var onAltitudeChange: ((Double) -> Unit)? = null
    var onStatusChange: ((String) -> Unit)? = null
    var currentAltitude: Double = 0.0
        private set

    private val handler = Handler(Looper.getMainLooper())
    private var isFlying = false

    // 🔴 Flight Recorder Variables
    private var flightStartTime: Long = 0
    private var maxAltitudeReached: Double = 0.0
    private val db = AppDatabase.getDatabase(context)

    init {
        if (isMockMode) startMockSimulation() else listenToAltitude()
    }

    // --- ACTIONS ---
    fun doTakeoff() {
        if (isFlying) return // Don't takeoff if already flying

        // Start Recording
        flightStartTime = System.currentTimeMillis()
        maxAltitudeReached = 0.0
        isFlying = true

        if (isMockMode) {
            simulateTakeoff()
        } else {
            val key = KeyTools.createKey(FlightControllerKey.KeyStartTakeoff)
            KeyManager.getInstance().performAction(key, null)
        }
    }

    fun doLanding() {
        if (!isFlying) return

        if (isMockMode) {
            simulateLanding()
        } else {
            val key = KeyTools.createKey(FlightControllerKey.KeyStartAutoLanding)
            KeyManager.getInstance().performAction(key, null)
        }
    }

    // 🔴 Save to Database (Called when landing finishes)
    private fun saveFlightLog() {
        isFlying = false
        val duration = (System.currentTimeMillis() - flightStartTime) / 1000
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val log = FlightLog(
            date = date,
            durationSeconds = duration,
            maxAltitude = maxAltitudeReached
        )

        // Database operations must be on a background thread
        Thread {
            db.flightDao().insert(log)
            logToWeb("💾 Flight Saved: ${duration}s, Max Alt: $maxAltitudeReached m")
        }.start()
    }

    // --- HELPER TO TRACK MAX ALTITUDE ---
    private fun updateAltitudeLogic(newAlt: Double) {
        currentAltitude = newAlt
        onAltitudeChange?.invoke(newAlt)

        // Track Highest Point
        if (isFlying) {
            maxAltitudeReached = max(maxAltitudeReached, newAlt)
        }
    }

    // --- MOCK SIMULATION ---
    private fun startMockSimulation() {
        val runnable = object : Runnable {
            override fun run() {
                if (isFlying && currentAltitude > 0) {
                    val noise = (Random().nextDouble() - 0.5) * 0.1
                    updateAltitudeLogic((currentAltitude + noise).coerceAtLeast(0.0))
                }
                handler.postDelayed(this, 200)
            }
        }
        handler.post(runnable)
    }

    private fun simulateTakeoff() {
        logToWeb("Taking off...")
        handler.postDelayed({
            currentAltitude = 1.2
            logToWeb("Hovering")
        }, 2000)
    }

    private fun simulateLanding() {
        logToWeb("Landing...")
        handler.postDelayed({
            currentAltitude = 0.0
            logToWeb("Landed")
            saveFlightLog() // <--- Save when landed
        }, 2000)
    }

    private fun listenToAltitude() {
        val key = KeyTools.createKey(FlightControllerKey.KeyAltitude)
        KeyManager.getInstance().listen(key, this, object : CommonCallbacks.KeyListener<Double> {
            override fun onValueChange(oldValue: Double?, newValue: Double?) {
                newValue?.let { updateAltitudeLogic(it) }
            }
        })
    }

    private fun logToWeb(msg: String) {
        onStatusChange?.invoke(msg)
    }

    // Helper to get logs for the Web Server
    fun getFlightHistory(): List<FlightLog> {
        return db.flightDao().getAll()
    }
}