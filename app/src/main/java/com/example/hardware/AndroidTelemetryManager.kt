package com.example.hardware

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Inet4Address
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

data class NetworkTelemetry(
    val isOnline: Boolean = false,
    val transportLabel: String = "Offline",
    val localIpv4: String = "Nicht verbunden",
    val isMetered: Boolean = false
)

data class StorageTelemetry(
    val totalBytes: Long = 0L,
    val freeBytes: Long = 0L,
    val usedBytes: Long = 0L
)

data class UsbSerialCandidate(
    val deviceName: String,
    val vendorId: Int,
    val productId: Int,
    val productLabel: String,
    val chipFamilyHint: String
)

data class BleDiscoveredNode(
    val address: String,
    val name: String,
    val rssi: Int,
    val isCyberCandidate: Boolean
)

data class FirmwareInspectionResult(
    val fileName: String,
    val sizeBytes: Long,
    val sha256: String,
    val hasEspMagicByte: Boolean,
    val detectedChipHint: String
)

class AndroidTelemetryManager(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    fun observeNetworkTelemetry(): Flow<NetworkTelemetry> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun snapshot(): NetworkTelemetry {
            val active = cm.activeNetwork
            val caps = if (active != null) cm.getNetworkCapabilities(active) else null
            val links = if (active != null) cm.getLinkProperties(active) else null
            val hasInternet = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            val transport = when {
                caps == null -> "Offline"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WLAN"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobilfunk (4G/5G)"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Aktiv"
            }
            val ipv4 = links?.linkAddresses
                ?.map { it.address }
                ?.firstOrNull { it is Inet4Address && !it.isLoopbackAddress }
                ?.hostAddress ?: "Nicht zugewiesen"
            val metered = cm.isActiveNetworkMetered
            return NetworkTelemetry(
                isOnline = hasInternet,
                transportLabel = transport,
                localIpv4 = ipv4,
                isMetered = metered
            )
        }

        trySend(snapshot())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(snapshot())
            }

            override fun onLost(network: Network) {
                trySend(snapshot())
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(snapshot())
            }
        }
        try {
            cm.registerDefaultNetworkCallback(callback)
        } catch (_: Exception) {
            trySend(snapshot())
        }
        awaitClose {
            try {
                cm.unregisterNetworkCallback(callback)
            } catch (_: Exception) {
            }
        }
    }

    fun readLocalStorageTelemetry(): StorageTelemetry {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val total = stat.totalBytes
            val free = stat.availableBytes
            StorageTelemetry(totalBytes = total, freeBytes = free, usedBytes = (total - free).coerceAtLeast(0L))
        } catch (_: Exception) {
            StorageTelemetry()
        }
    }

    fun scanUsbEsp32Bridges(): List<UsbSerialCandidate> {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager ?: return emptyList()
        return usbManager.deviceList.values.map { device: UsbDevice ->
            val hint = when (device.vendorId) {
                0x303A -> "Espressif ESP32-S3 Native USB-JTAG / CDC"
                0x10C4 -> "Silicon Labs CP210x UART Bridge (ESP32 DevKit)"
                0x1A86 -> "QinHeng CH340/CH343 USB-Serial"
                0x0403 -> "FTDI FT232 USB-UART"
                else -> "Generisches USB-Gerät (VID 0x${device.vendorId.toString(16)})"
            }
            UsbSerialCandidate(
                deviceName = device.deviceName,
                vendorId = device.vendorId,
                productId = device.productId,
                productLabel = device.productName ?: "USB Serial Device",
                chipFamilyHint = hint
            )
        }
    }

    fun isBluetoothReady(): Boolean {
        val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter: BluetoothAdapter? = bm?.adapter
        return adapter != null && adapter.isEnabled
    }

    @SuppressLint("MissingPermission")
    fun scanBleNodes(): Flow<BleDiscoveredNode> = callbackFlow {
        val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val scanner = bm?.adapter?.bluetoothLeScanner
        if (scanner == null) {
            close(IllegalStateException("Bluetooth LE Scanner nicht verfügbar oder deaktiviert."))
            return@callbackFlow
        }

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val devName = result.device.name ?: result.scanRecord?.deviceName ?: "Unbekanntes BLE-Gerät"
                val addr = result.device.address ?: "00:00:00:00:00:00"
                val isCyber = devName.contains("CYBER", ignoreCase = true) ||
                    devName.contains("ESP", ignoreCase = true) ||
                    devName.contains("Pro Controller", ignoreCase = true) ||
                    devName.contains("Joy-Con", ignoreCase = true)
                trySend(
                    BleDiscoveredNode(
                        address = addr,
                        name = devName,
                        rssi = result.rssi,
                        isCyberCandidate = isCyber
                    )
                )
            }

            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException("BLE Scan fehlgeschlagen (Code $errorCode)"))
            }
        }

        try {
            scanner.startScan(callback)
        } catch (se: SecurityException) {
            close(se)
            return@callbackFlow
        } catch (e: Exception) {
            close(e)
            return@callbackFlow
        }

        awaitClose {
            try {
                scanner.stopScan(callback)
            } catch (_: Exception) {
            }
        }
    }

    suspend fun verifyOfficialUrl(url: String): String = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "CyberFlashApp/9.0")
                .head()
                .build()
            httpClient.newCall(request).execute().use { resp ->
                if (resp.isSuccessful || resp.code in 300..399) {
                    "Erreichbar (HTTP ${resp.code})"
                } else if (resp.code == 405 || resp.code == 403) {
                    // Some portals reject HEAD; retry lightweight GET
                    val getReq = Request.Builder()
                        .url(url)
                        .header("User-Agent", "CyberFlashApp/9.0")
                        .get()
                        .build()
                    httpClient.newCall(getReq).execute().use { getResp ->
                        if (getResp.isSuccessful) "Erreichbar (HTTP ${getResp.code})"
                        else "Hinweis: HTTP ${getResp.code}"
                    }
                } else {
                    "HTTP ${resp.code}"
                }
            }
        } catch (e: Exception) {
            "Nicht erreichbar (${e.javaClass.simpleName})"
        }
    }

    suspend fun inspectFirmwareBinary(uri: Uri, displayName: String): FirmwareInspectionResult =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val digest = MessageDigest.getInstance("SHA-256")
            var totalBytes = 0L
            var firstByte: Int? = null
            var chipByte: Int? = null

            resolver.openInputStream(uri)?.use { stream ->
                val buffer = ByteArray(8192)
                var read = stream.read(buffer)
                while (read != -1) {
                    if (firstByte == null && read > 0) {
                        firstByte = buffer[0].toInt() and 0xFF
                        if (read > 12) {
                            // Espressif image header offset 12..13 often indicates chip ID in merged/app images
                            chipByte = buffer[12].toInt() and 0xFF
                        }
                    }
                    digest.update(buffer, 0, read)
                    totalBytes += read
                    read = stream.read(buffer)
                }
            } ?: throw IllegalStateException("Firmware-Datei konnte nicht geöffnet werden.")

            val hashHex = digest.digest().joinToString("") { "%02x".format(it) }
            val hasMagic = firstByte == 0xE9
            val chipHint = when {
                !hasMagic -> "Kein 0xE9 ESP-Header (ggf. Partition/Bootloader oder Nicht-ESP-Datei)"
                chipByte == 0x09 -> "ESP32-S3 Image-Signatur erkannt (0xE9)"
                else -> "Gültiger Espressif Firmware-Header (0xE9)"
            }
            FirmwareInspectionResult(
                fileName = displayName,
                sizeBytes = totalBytes,
                sha256 = hashHex,
                hasEspMagicByte = hasMagic,
                detectedChipHint = chipHint
            )
        }
}
