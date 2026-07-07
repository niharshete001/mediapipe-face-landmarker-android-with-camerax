
https://github.com/user-attachments/assets/d381d641-6cb7-45c9-a834-d26131e7f9a5


# Real-Time Face Attention Detection on Android Using MediaPipe and CameraX

## Overview

This project demonstrates a real-time face attention detection system built for Android using **Kotlin**, **CameraX**, and **MediaPipe Face Landmarker**.

The application analyzes live camera frames and detects the user's face position, eye state, and head direction. Based on facial landmark analysis, the system determines the current attention state.

This project is a **computer vision proof of concept** and does not perform face recognition or identify users. It only analyzes facial movements and expressions in real time.

---

## Features

The application can detect the following states:

* ✅ `ATTENTIVE`

  * User is facing the camera correctly.

* 👈 `LOOKING_LEFT`

  * User turns head towards the left.

* 👉 `LOOKING_RIGHT`

  * User turns head towards the right.

* 👆 `LOOKING_UP`

  * User raises head upward.

* 👇 `LOOKING_DOWN`

  * User lowers head downward.

* 👀 `EYES_CLOSED`

  * User keeps eyes closed for multiple frames.

* ❌ `NO_FACE`

  * No face is detected in the camera frame.

---

## Architecture

The processing pipeline follows this flow:

```
CameraX
   |
   ↓
Live Camera Frames
   |
   ↓
MediaPipe Face Landmarker
   |
   ↓
Face Landmarks (468 points)
   |
   ↓
Attention Detection Engine
   |
   ↓
Attention State
   |
   ↓
UI Feedback
```

---

## How It Works

### 1. Camera Frame Processing

CameraX provides continuous frames from the front camera.

Each frame is passed to the MediaPipe Face Landmarker for analysis.

---

### 2. Face Landmark Detection

MediaPipe detects facial landmarks and provides coordinates for different facial regions.

The project uses selected landmarks:

* Eye landmarks for blink detection
* Nose landmark for head movement
* Face boundary landmarks for calculating face center

---

### 3. Eye Closure Detection

The project uses **Eye Aspect Ratio (EAR)** to detect eye closure.

Concept:

```
EAR = Eye Height / Eye Width
```

When the EAR value remains below a threshold for multiple frames, the system identifies:

```
EYES_CLOSED
```

Frame-based validation is used to reduce false detection.

---

### 4. Head Direction Detection

Head direction is estimated by comparing:

```
Nose Position
        +
Face Center Position
```

Based on the movement of the nose relative to the face center:

* Horizontal movement → Left / Right detection
* Vertical movement → Up / Down detection

Threshold values are applied to avoid detecting minor movements.

---

### 5. State Stabilization

Real-time face tracking can produce unstable results because landmarks may fluctuate between frames.

To improve stability, the engine maintains recent detection history and selects the most frequent state.

Example:

```
Frame 1 : LEFT
Frame 2 : LEFT
Frame 3 : ATTENTIVE
Frame 4 : LEFT

Final Result:
LOOKING_LEFT
```

---

## Tech Stack

* Kotlin
* Android SDK
* CameraX
* MediaPipe Face Landmarker
* Computer Vision
* Real-time Image Processing

---

## Project Structure

```
app
 |
 ├── camera
 │    └── CameraX integration
 |
 ├── mediapipe
 │    └── Face Landmarker setup
 |
 ├── detector
 │    └── Attention detection engine
 |
 ├── model
 │    └── Attention states
 |
 └── ui
      └── Camera preview and status display
```

---

## Challenges Addressed

### 1. Landmark Noise

Facial landmarks can fluctuate between frames.

**Solution:**
Applied threshold-based detection and state smoothing.

---

### 2. False Eye Closure Detection

Single-frame eye closure can produce incorrect results.

**Solution:**
Used multiple consecutive frames before marking eyes as closed.

---

### 3. Real-Time Performance

Processing every camera frame can increase CPU usage.

**Solution:**
Optimized frame processing and released camera resources properly.

---

## Future Improvements

Possible enhancements:

* 3D head pose estimation using rotation angles
* Eye gaze tracking
* Multiple face detection
* Attention score calculation
* Integration with online proctoring systems
* Improved accuracy using machine learning models

---

## Disclaimer

This project is created for learning and experimentation purposes.

It does not perform:

* Face recognition
* Identity verification
* User authentication

It only detects facial landmarks and head/eye movement patterns.

---

## Author

Built with Kotlin, CameraX, and MediaPipe to explore real-time computer vision capabilities on Android.


