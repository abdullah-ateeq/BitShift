# BitShift - Digital Storage & Data Converter

<p align="center">
  <b>A modern, high-precision Android utility for converting digital storage and data units across Decimal (SI) and Binary (IEC) measurement standards.</b>
</p>

---

## 📱 About The App

**BitShift** is a professional, offline-first Android application designed for developers, system administrators, network engineers, students, and tech enthusiasts. It provides instant, lossless conversion between standard data measurement units—handling everything from individual bits to petabytes.

Built with **Jetpack Compose** and **Material Design 3**, BitShift delivers a clean, intuitive, and responsive user experience with zero latency and 100% privacy.

---

## ✨ Key Features

* **Dual Measurement Standards**:
  * **Decimal (SI Standard)**: Multiples of $10^3 = 1,000$ (Bit, Byte, KB, MB, GB, TB, PB).
  * **Binary (IEC Standard)**: Multiples of $2^{10} = 1,024$ (Bit, Byte, KiB, MiB, GiB, TiB, PiB).
* **High-Precision Engine**: Powered by `BigDecimal` arithmetic to eliminate floating-point rounding anomalies during large-scale unit conversions.
* **Instant Conversions**: Real-time conversion across all units simultaneously as you type.
* **Quick Value Chips & Presets**: Rapidly populate common file sizes (e.g., 512 MB, 1 GB, 1 TB) with a single tap.
* **Conversion History**: Save and review past conversions with local persistence, copy results, or clear history anytime.
* **Customizable Settings**:
  * Precision control (adjust decimal places).
  * Sound and haptic feedback toggles.
  * Default system standard selection (Decimal vs Binary).
* **100% Offline & Private**: Zero network dependencies, zero telemetry, and zero permissions required.

---

## 📏 Measurement Standards Reference

| Standard | System | Base | Units | Primary Usage |
| :--- | :--- | :--- | :--- | :--- |
| **SI (Decimal)** | Metric | $1,000$ ($10^3$) | KB, MB, GB, TB, PB | Storage hardware manufacturers (HDD/SSD), Networking |
| **IEC (Binary)** | Binary | $1,024$ ($2^{10}$) | KiB, MiB, GiB, TiB, PiB | Operating systems (RAM, File systems like Windows/Linux) |

---

## 🛠️ Tech Stack & Architecture

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
* **Architecture**: Unidirectional Data Flow (UDF) with `ViewModel` and `StateFlow`
* **Persistence**: [AndroidX DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) with custom JSON serialization
* **Navigation**: Jetpack Navigation Compose
* **Build System**: Gradle with Kotlin DSL & Version Catalogs

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio**: Ladybug / ME / Jellyfish or newer
* **JDK**: 11 or higher
* **Min SDK**: 24 (Android 7.0 Nougat)
* **Target SDK**: 37 (Android 15+)

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/BitShift.git
   cd BitShift
   ```

2. Open the project in Android Studio.

3. Build the debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

4. Run unit tests:
   ```bash
   ./gradlew test
   ```

---

## 📄 License

```text
Copyright (c) 2025 Abdullah Ateeq

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "A IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
