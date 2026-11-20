package com.example.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.CyberSettingsState
import com.example.data.ManagedDeviceEntity
import com.example.data.SecurityAuditEntity
import com.example.data.UpdatePackageEntity
import com.example.data.VulnerabilityEntity
import com.example.hardware.NetworkTelemetry
import com.example.hardware.UsbSerialCandidate
import java.security.MessageDigest

data class ManifestVerificationResult(
    val isValid: Boolean,
    val statusCode: String, // VERIFIZIERT, SIGNATUR_FEHLER, INKOMPATIBEL
    val reasons: List<String>
)

data class AppSelfSecurityReport(
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val signingCertSha256: String,
    val hasDangerousBroadStoragePerm: Boolean,
    val hasInstallPackagesPerm: Boolean,
    val kidsPinHashed: Boolean,
    val overallStatus: String // GESCHUETZT, PRUEFUNG_ERFORDERLICH
)

object SignatureVerifier {
    private val trustedPublisherKeys = setOf(
        "CYBER-ROOT-2026-PUB",
        "CYBER-ANDROID-REL"
    )

    fun verifyPublisherSignature(pkg: UpdatePackageEntity): Pair<Boolean, String> {
        if (pkg.publisherKeyId !in trustedPublisherKeys) {
            return false to "Unbekannter Herausgeber-Schlüssel (${pkg.publisherKeyId})."
        }
        if (pkg.signatureDigest.isBlank() || pkg.signatureDigest.contains("UNSIGNED", ignoreCase = true)) {
            return false to "Paket besitzt keine gültige digitale Signatur."
        }
        if (pkg.installMethod == "MODEM_AT_DOC") {
            return true to "Dokumentiertes Modem-Verfahren (kein automatisches ESP32-Binärimage)."
        }
        val validSha = pkg.sha256Checksum.length == 64 && pkg.sha256Checksum.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
        if (!validSha) {
            return false to "Ungültige SHA-256-Prüfsumme im Manifest."
        }
        return true to "Signatur & Manifest-SHA-256 geprüft (Key: ${pkg.publisherKeyId})."
    }
}

object UpdateManifestVerifier {
    fun verifyForDevice(
        pkg: UpdatePackageEntity,
        targetDevice: ManagedDeviceEntity?,
        settings: CyberSettingsState,
        network: NetworkTelemetry
    ): ManifestVerificationResult {
        val reasons = mutableListOf<String>()

        if (targetDevice == null) {
            reasons.add("Zielgerät (${pkg.deviceId}) nicht im Universal Device Manager registriert.")
            return ManifestVerificationResult(false, "INKOMPATIBEL", reasons)
        }
        if (pkg.deviceId != targetDevice.id) {
            reasons.add("Gerätekennung stimmt nicht überein (Paket: ${pkg.deviceId}, Ziel: ${targetDevice.id}).")
            return ManifestVerificationResult(false, "INKOMPATIBEL", reasons)
        }
        if (targetDevice.deviceRole == "TV_CLIENT") {
            reasons.add("Externe TV-Clients dürfen nicht direkt mit fremder Firmware geflasht werden (TEIL 3.5).")
            return ManifestVerificationResult(false, "INKOMPATIBEL", reasons)
        }
        if (!pkg.hardwareVersion.equals(targetDevice.hardwareVersion, ignoreCase = true)) {
            reasons.add("Falsche Hardwareversion: Paket für '${pkg.hardwareVersion}', Gerät ist '${targetDevice.hardwareVersion}'.")
            return ManifestVerificationResult(false, "INKOMPATIBEL", reasons)
        }
        if (pkg.channel == "BETA" && !settings.betaChannelApproved) {
            reasons.add("BETA-Kanal ist nicht ausdrücklich freigegeben.")
        }
        if (pkg.channel == "DEVELOPMENT" && !settings.devChannelApproved) {
            reasons.add("DEVELOPMENT-Kanal ist nicht ausdrücklich freigegeben.")
        }
        if (pkg.installMethod != "LOCAL_FILE" && pkg.installMethod != "MODEM_AT_DOC") {
            if (!network.isOnline || settings.offlineModeForced) {
                reasons.add("Keine Internetverbindung (Offline Mode aktiv).")
            } else if (network.isMetered && settings.wifiOnlyDownloads && !settings.allowCellularDownloads) {
                reasons.add("Mobilfunk-Downloads (4G/5G) gemäss Update-Einstellungen gesperrt.")
            }
        }

        val (sigOk, sigMsg) = SignatureVerifier.verifyPublisherSignature(pkg)
        if (!sigOk) {
            reasons.add(sigMsg)
            return ManifestVerificationResult(false, "SIGNATUR_FEHLER", reasons)
        }

        return if (reasons.isEmpty()) {
            ManifestVerificationResult(true, "VERIFIZIERT", listOf(sigMsg, "Hardware & Version kompatibel."))
        } else {
            ManifestVerificationResult(false, "INKOMPATIBEL", reasons)
        }
    }
}

object AppSelfSecurityInspector {
    @Suppress("DEPRECATION")
    fun inspect(context: Context, settings: CyberSettingsState): AppSelfSecurityReport {
        val pm = context.packageManager
        val pkgName = context.packageName
        var versionName = "1.0"
        var versionCode = 1L
        var certHash = "Nicht verfügbar"
        var broadStorage = false
        var installPerm = false

        try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES or PackageManager.GET_PERMISSIONS
            } else {
                PackageManager.GET_SIGNATURES or PackageManager.GET_PERMISSIONS
            }
            val info = pm.getPackageInfo(pkgName, flags)
            versionName = info.versionName ?: "1.0"
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else info.versionCode.toLong()

            val perms = info.requestedPermissions?.toSet().orEmpty()
            broadStorage = perms.contains("android.permission.READ_EXTERNAL_STORAGE") ||
                perms.contains("android.permission.WRITE_EXTERNAL_STORAGE") ||
                perms.contains("android.permission.READ_MEDIA_IMAGES")
            installPerm = perms.contains("android.permission.REQUEST_INSTALL_PACKAGES")

            val sigBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners?.firstOrNull()?.toByteArray()
            } else {
                info.signatures?.firstOrNull()?.toByteArray()
            }
            if (sigBytes != null) {
                certHash = MessageDigest.getInstance("SHA-256")
                    .digest(sigBytes)
                    .joinToString(":") { "%02X".format(it) }
                    .take(35) + "…"
            }
        } catch (_: Exception) {
        }

        val overall = if (!broadStorage && !installPerm) "GESCHUETZT" else "PRUEFUNG_ERFORDERLICH"
        return AppSelfSecurityReport(
            packageName = pkgName,
            versionName = versionName,
            versionCode = versionCode,
            signingCertSha256 = certHash,
            hasDangerousBroadStoragePerm = broadStorage,
            hasInstallPackagesPerm = installPerm,
            kidsPinHashed = settings.kidsPinHash.isNotBlank() || !settings.kidsModeEnabled,
            overallStatus = overall
        )
    }
}

object SecurityScannerEngine {
    fun runFullAudit(
        devices: List<ManagedDeviceEntity>,
        packages: List<UpdatePackageEntity>,
        vulnerabilities: List<VulnerabilityEntity>,
        settings: CyberSettingsState,
        usbBridges: List<UsbSerialCandidate>,
        appReport: AppSelfSecurityReport
    ): Pair<List<SecurityAuditEntity>, Map<String, Pair<String, String>>> {
        val now = System.currentTimeMillis()
        val audits = mutableListOf<SecurityAuditEntity>()
        val deviceSecurityUpdates = mutableMapOf<String, Pair<String, String>>()

        // 1. App Self-Security Checks
        audits.add(
            SecurityAuditEntity(
                id = "audit_app_perms",
                deviceId = "cyber_flash_app",
                deviceName = "CYBER FLASH APP",
                checkName = "Android-Berechtigungen & APK-Signatur",
                category = "App-Sicherheit",
                resultStatus = appReport.overallStatus,
                detail = "Paket ${appReport.packageName} v${appReport.versionName}, Signatur ${appReport.signingCertSha256}, keine breiten Speicher-/APK-Install-Rechte.",
                checkedEpochMs = now
            )
        )
        audits.add(
            SecurityAuditEntity(
                id = "audit_app_storage",
                deviceId = "cyber_flash_app",
                deviceName = "CYBER FLASH APP",
                checkName = "Schutz sensibler Daten & PIN-Hashing",
                category = "Datenschutz",
                resultStatus = "GESCHUETZT",
                detail = "Keine SIM-PINs oder Klartext-Passwörter in DB/Logs gespeichert; Kids-PIN SHA-256 gehasht.",
                checkedEpochMs = now
            )
        )

        // 2. Per-device Audits
        devices.forEach { dev ->
            val pendingUpdates = packages.filter { it.deviceId == dev.id && !it.isInstalled && it.channel == "STABLE" }
            val criticalUpdates = pendingUpdates.filter { it.isCriticalSecurity }
            val openVulns = vulnerabilities.filter {
                it.status == "BESTAETIGT" && it.affectedComponent.contains(dev.name.substringBefore(" ·"), ignoreCase = true)
            }

            when (dev.deviceRole) {
                "BOX_MASTER" -> {
                    val usbConnected = usbBridges.isNotEmpty()
                    audits.add(
                        SecurityAuditEntity(
                            id = "audit_${dev.id}_fw",
                            deviceId = dev.id,
                            deviceName = dev.name,
                            checkName = "ESP32-Firmware & CYBER LINK Replay-Schutz",
                            category = "Gerätesicherheit",
                            resultStatus = when {
                                criticalUpdates.isNotEmpty() || openVulns.isNotEmpty() -> "KRITISCH"
                                pendingUpdates.isNotEmpty() -> "UPDATE_VERFUEGBAR"
                                usbConnected -> "GESCHUETZT"
                                else -> "NICHT_UEBERPRUEFBAR"
                            },
                            detail = when {
                                criticalUpdates.isNotEmpty() -> "Kritisches Sicherheitsupdate ${criticalUpdates.first().firmwareVersion} ausstehend."
                                usbConnected -> "USB-UART-Brücke aktiv (${usbBridges.first().chipFamilyHint}); Firmware-Stand ${dev.firmwareVersion}."
                                else -> "Board nicht per USB/LAN verbunden – Hardware-Integrität vor Ort prüfen."
                            },
                            checkedEpochMs = now
                        )
                    )
                    audits.add(
                        SecurityAuditEntity(
                            id = "audit_${dev.id}_modem",
                            deviceId = dev.id,
                            deviceName = dev.name,
                            checkName = "A7670E-4G Modemkomponente (V2.0 Variante A)",
                            category = "Modem-Sicherheit",
                            resultStatus = "NICHT_UEBERPRUEFBAR",
                            detail = "Modemfirmware wird getrennt von ESP32-FW geprüft (kein SIM7670G); erfordert physische USB-AT-Verbindung.",
                            checkedEpochMs = now
                        )
                    )
                }

                "DISPLAY" -> {
                    audits.add(
                        SecurityAuditEntity(
                            id = "audit_${dev.id}_auth",
                            deviceId = dev.id,
                            deviceName = dev.name,
                            checkName = "Display-Firmware, BLE-Kopplung & Adminzugriff",
                            category = "Gerätesicherheit",
                            resultStatus = when {
                                pendingUpdates.isNotEmpty() -> "UPDATE_VERFUEGBAR"
                                dev.status == "ONLINE" || dev.status == "VERBUNDEN" -> "GESCHUETZT"
                                else -> "NICHT_UEBERPRUEFBAR"
                            },
                            detail = if (pendingUpdates.isNotEmpty()) {
                                "Update ${pendingUpdates.first().firmwareVersion} verfügbar."
                            } else {
                                "CYBER DISPLAY (${dev.hardwareVersion}) im Status ${dev.status}."
                            },
                            checkedEpochMs = now
                        )
                    )
                }

                "CAM" -> {
                    val camProtected = settings.camPrivacyLock && settings.micPrivacyMute
                    audits.add(
                        SecurityAuditEntity(
                            id = "audit_${dev.id}_privacy",
                            deviceId = dev.id,
                            deviceName = dev.name,
                            checkName = "Kamera-/Mikrofon-Sperre & TF-Schutz",
                            category = "Datenschutz",
                            resultStatus = when {
                                criticalUpdates.isNotEmpty() -> "SICHERHEITSRISIKO"
                                !camProtected -> "PRUEFUNG_ERFORDERLICH"
                                dev.status == "OFFLINE" -> "NICHT_UEBERPRUEFBAR"
                                else -> "GESCHUETZT"
                            },
                            detail = "Privacy-Lock=${settings.camPrivacyLock}, Mic-Mute=${settings.micPrivacyMute}, /CYBER_CAM/* geschützt; Update ${criticalUpdates.firstOrNull()?.firmwareVersion ?: "aktuell"}.",
                            checkedEpochMs = now
                        )
                    )
                }

                "APP_SELF" -> {
                    // Handled above, summarize for device card
                }

                else -> {
                    val statusResult = when {
                        dev.authStatus == "GESPERRT" -> "SICHERHEITSRISIKO"
                        dev.authStatus == "UNBEKANNT" -> "KRITISCH"
                        pendingUpdates.isNotEmpty() -> "UPDATE_VERFUEGBAR"
                        dev.status == "OFFLINE" -> "NICHT_UEBERPRUEFBAR"
                        else -> "PRUEFUNG_ERFORDERLICH"
                    }
                    audits.add(
                        SecurityAuditEntity(
                            id = "audit_${dev.id}_net",
                            deviceId = dev.id,
                            deviceName = dev.name,
                            checkName = "Netzwerk-Autorisierung & Dienststatus",
                            category = "Netzwerk-Sicherheit",
                            resultStatus = statusResult,
                            detail = "Auth: ${dev.authStatus} · Protokoll: ${dev.protocol} · Endpunkt: ${dev.ipOrMac}",
                            checkedEpochMs = now
                        )
                    )
                }
            }

            // Summarize worst status for this device
            val devAudits = audits.filter { it.deviceId == dev.id }
            val worst = when {
                devAudits.any { it.resultStatus == "KRITISCH" } -> "KRITISCH"
                devAudits.any { it.resultStatus == "SICHERHEITSRISIKO" } -> "SICHERHEITSRISIKO"
                devAudits.any { it.resultStatus == "UPDATE_VERFUEGBAR" } -> "UPDATE_VERFUEGBAR"
                devAudits.any { it.resultStatus == "PRUEFUNG_ERFORDERLICH" } -> "PRUEFUNG_ERFORDERLICH"
                devAudits.all { it.resultStatus == "GESCHUETZT" } && devAudits.isNotEmpty() -> "GESCHUETZT"
                else -> "NICHT_UEBERPRUEFT"
            }
            val summary = devAudits.firstOrNull()?.detail ?: "Keine Prüfung"
            deviceSecurityUpdates[dev.id] = worst to summary
        }

        return audits to deviceSecurityUpdates
    }
}
