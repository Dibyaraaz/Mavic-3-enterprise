# Mobile Platform for Drone Control

**Supervisor:** Leonel Domingues Deusdado

## Overview

This project develops a **hybrid Android application** for controlling and monitoring a DJI drone by integrating **web technologies** with the **DJI Mobile SDK**. The goal is to provide hands-on experience in cross-platform development, web–native communication, and real-time interaction with drone hardware.

The system supports a **dual-operator control model**, where a primary pilot uses the DJI Remote Controller (RC), and a secondary operator interacts with the drone through a web-based mobile interface.

---

## Hardware & Technologies

- **Drone:** DJI Mavic 3 Enterprise  
- **Controller:** DJI RC Pro (DJI Pilot 2)  
- **Platform:** Android (Hybrid App)  
- **SDK:** DJI Mobile SDK V5  
- **Languages:** Kotlin, HTML, CSS, JavaScript  

---

## Key Features

- Hybrid Android app with embedded WebView  
- JavaScript–Native bridge for web-to-SDK communication  
- Web-based interface for basic flight commands  
- Real-time telemetry (GPS, battery, signal strength)  
- Simple waypoint mission support  
- Dual control with RC priority  
- Authentication to prevent unauthorized access  

---

## Architecture

- **Web Layer:** HTML/CSS/JavaScript UI inside a WebView  
- **Native Layer:** Kotlin logic using DJI Mobile SDK  
- **Bridge:** Bidirectional JavaScript–Kotlin communication  

The primary device connects directly to the drone, while secondary users access the control interface remotely through the primary device’s network.

---

## Safety & Control Priority

- Physical RC commands always override software commands  
- Emergency actions cannot be overridden remotely  
- Ensures safe and reliable drone operation  

---

## Project Structure

```text
app/
 ├── assets/index.html        # Web UI
 ├── MainActivity.kt          # WebView & server
 ├── WebAppInterface.kt       # JS–Native bridge
 ├── DroneViewModel.kt        # Drone logic
 └── LocalWebServer.kt        # Remote access
