package com.example.dji_mavic_app

import android.content.Context
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class LocalWebServer(private val context: Context, private val viewModel: DroneViewModel) : NanoHTTPD(8080) {

    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val uri = session.uri

        if (uri == "/api/takeoff") {
            viewModel.doTakeoff()
            return newFixedLengthResponse("Takeoff Sent")
        }
        if (uri == "/api/landing") {
            viewModel.doLanding()
            return newFixedLengthResponse("Landing Sent")
        }
        if (uri == "/api/status") {
            return newFixedLengthResponse(String.format("%.2f", viewModel.currentAltitude))
        }

        // 🔴 NEW: History Endpoint
        if (uri == "/api/history") {
            // Get logs from DB (must assume this is fast enough for now, ideal to async)
            val history = viewModel.getFlightHistory()
            val json = Gson().toJson(history) // Convert to JSON
            return newFixedLengthResponse(Response.Status.OK, "application/json", json)
        }

        if (uri == "/" || uri == "/index.html") {
            val html = loadAsset("index.html")
            return newFixedLengthResponse(html)
        }

        return newFixedLengthResponse("404 Not Found")
    }

    private fun loadAsset(fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) { "Error UI" }
    }
}