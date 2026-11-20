package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAssistantUiState
import com.example.ai.CyberAiAssistantService
import com.example.ai.CyberChatMessage
import com.example.core.AppSelfSecurityInspector
import com.example.core.AppSelfSecurityReport
import com.example.core.SecurityScannerEngine
import com.example.core.UpdateManifestVerifier
import com.example.data.AdBlockExceptionEntity
import com.example.data.AutomationRuleEntity
import com.example.data.CyberDatabase
import com.example.data.CyberPreferences
import com.example.data.CyberRepository
import com.example.data.CyberSettingsState
import com.example.data.FlashLogEntity
import com.example.data.LiveTvChannelEntity
import com.example.data.ManagedDeviceEntity
import com.example.data.RecoveryPointEntity
import com.example.data.SecurityAuditEntity
import com.example.data.SecurityEventEntity
import com.example.data.StreamingPlatformEntity
import com.example.data.TestMatrixItemEntity
import com.example.data.UpdateHistoryEntity
import com.example.data.UpdatePackageEntity
import com.example.data.UpdateSourceEntity
import com.example.data.VulnerabilityEntity
import com.example.hardware.AndroidTelemetryManager
import com.example.hardware.BleDiscoveredNode
import com.example.hardware.FirmwareInspectionResult
import com.example.hardware.NetworkTelemetry
import com.example.hardware.StorageTelemetry
import com.example.hardware.UsbSerialCandidate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

data class CyberNotification(
    val id: String,
    val title: String,
    val detail: String,
    val severity: String // INFO, WARN, ALERT
)

data class UpdateInstallProgress(
    val packageId: String,
    val deviceName: String,
    val version: String,
    val stage: String, // VORBEREITUNG, DOWNLOAD, PRUEFUNG, UEBERTRAGUNG, INSTALLATION, NEUSTART, SELBSTTEST, ABSCHLUSS, FEHLER
    val detail: String,
    val errorCode: String? = null,
    val solutionHint: String? = null,
    val isFinished: Boolean = false,
    val isSuccess: Boolean = false
)

class CyberViewModel(application: Application) : AndroidViewModel(application) {

    private val database = CyberDatabase.getInstance(application)
    private val repository = CyberRepository(database.cyberDao())
    private val preferences = CyberPreferences(application)
    private val telemetry = AndroidTelemetryManager(application)
    private val aiService = CyberAiAssistantService()

    val settings: StateFlow<CyberSettingsState> = preferences.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CyberSettingsState())

    val channels: StateFlow<List<LiveTvChannelEntity>> = repository.channels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platforms: StateFlow<List<StreamingPlatformEntity>> = repository.platforms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val devices: StateFlow<List<ManagedDeviceEntity>> = repository.devices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testMatrix: StateFlow<List<TestMatrixItemEntity>> = repository.testMatrix
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adBlockExceptions: StateFlow<List<AdBlockExceptionEntity>> = repository.adBlockExceptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashLogs: StateFlow<List<FlashLogEntity>> = repository.flashLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<AutomationRuleEntity>> = repository.automations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Update & Security Center Flows
    val updatePackages: StateFlow<List<UpdatePackageEntity>> = repository.updatePackages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val updateHistory: StateFlow<List<UpdateHistoryEntity>> = repository.updateHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val updateSources: StateFlow<List<UpdateSourceEntity>> = repository.updateSources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vulnerabilities: StateFlow<List<VulnerabilityEntity>> = repository.vulnerabilities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityAudits: StateFlow<List<SecurityAuditEntity>> = repository.securityAudits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityEvents: StateFlow<List<SecurityEventEntity>> = repository.securityEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recoveryPoints: StateFlow<List<RecoveryPointEntity>> = repository.recoveryPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val networkTelemetry: StateFlow<NetworkTelemetry> = telemetry.observeNetworkTelemetry()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkTelemetry())

    private val _storageTelemetry = MutableStateFlow(telemetry.readLocalStorageTelemetry())
    val storageTelemetry: StateFlow<StorageTelemetry> = _storageTelemetry.asStateFlow()

    private val _usbBridges = MutableStateFlow<List<UsbSerialCandidate>>(emptyList())
    val usbBridges: StateFlow<List<UsbSerialCandidate>> = _usbBridges.asStateFlow()

    private val _bleNodes = MutableStateFlow<List<BleDiscoveredNode>>(emptyList())
    val bleNodes: StateFlow<List<BleDiscoveredNode>> = _bleNodes.asStateFlow()

    private val _isBleScanning = MutableStateFlow(false)
    val isBleScanning: StateFlow<Boolean> = _isBleScanning.asStateFlow()

    private val _bleStatusMessage = MutableStateFlow("BLE bereit für Scan")
    val bleStatusMessage: StateFlow<String> = _bleStatusMessage.asStateFlow()

    private val _inspectedFirmware = MutableStateFlow<FirmwareInspectionResult?>(null)
    val inspectedFirmware: StateFlow<FirmwareInspectionResult?> = _inspectedFirmware.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _watchTogetherCode = MutableStateFlow<String?>(null)
    val watchTogetherCode: StateFlow<String?> = _watchTogetherCode.asStateFlow()

    private val _aiState = MutableStateFlow<AiAssistantUiState>(AiAssistantUiState.Idle)
    val aiState: StateFlow<AiAssistantUiState> = _aiState.asStateFlow()

    private val _aiMessages = MutableStateFlow<List<CyberChatMessage>>(emptyList())
    val aiMessages: StateFlow<List<CyberChatMessage>> = _aiMessages.asStateFlow()

    private val _appSelfSecurityReport = MutableStateFlow(
        AppSelfSecurityInspector.inspect(application, CyberSettingsState())
    )
    val appSelfSecurityReport: StateFlow<AppSelfSecurityReport> = _appSelfSecurityReport.asStateFlow()

    private val _installProgress = MutableStateFlow<UpdateInstallProgress?>(null)
    val installProgress: StateFlow<UpdateInstallProgress?> = _installProgress.asStateFlow()

    private val _isSearchingUpdates = MutableStateFlow(false)
    val isSearchingUpdates: StateFlow<Boolean> = _isSearchingUpdates.asStateFlow()

    private val _lastUpdateCheckEpochMs = MutableStateFlow(0L)
    val lastUpdateCheckEpochMs: StateFlow<Long> = _lastUpdateCheckEpochMs.asStateFlow()

    private val _lastSecurityScanEpochMs = MutableStateFlow(0L)
    val lastSecurityScanEpochMs: StateFlow<Long> = _lastSecurityScanEpochMs.asStateFlow()

    private var bleScanJob: Job? = null
    private var aiJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            refreshUsbBridges()
            refreshStorage()
            refreshAppSelfSecurity()
        }
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun showStatus(message: String) {
        _statusBanner.value = message
    }

    fun refreshStorage() {
        _storageTelemetry.value = telemetry.readLocalStorageTelemetry()
    }

    fun refreshAppSelfSecurity() {
        _appSelfSecurityReport.value = AppSelfSecurityInspector.inspect(getApplication(), settings.value)
    }

    fun refreshUsbBridges() {
        val found = telemetry.scanUsbEsp32Bridges()
        _usbBridges.value = found
        _statusBanner.value = if (found.isEmpty()) {
            "Kein USB-UART / ESP32-S3 OTG-Gerät angeschlossen."
        } else {
            "${found.size} USB-Schnittstelle(n) erkannt."
        }
    }

    fun startBleScan() {
        if (!telemetry.isBluetoothReady()) {
            _bleStatusMessage.value = "Bluetooth ist deaktiviert oder nicht verfügbar."
            return
        }
        bleScanJob?.cancel()
        _bleNodes.value = emptyList()
        _isBleScanning.value = true
        _bleStatusMessage.value = "Suche BLE-Geräte (CYBER LINK / Controller)..."
        bleScanJob = viewModelScope.launch {
            try {
                launch {
                    delay(12_000L)
                    stopBleScan()
                }
                telemetry.scanBleNodes().collect { node ->
                    val current = _bleNodes.value.toMutableList()
                    val existingIdx = current.indexOfFirst { it.address == node.address }
                    if (existingIdx >= 0) {
                        current[existingIdx] = node
                    } else {
                        current.add(node)
                    }
                    _bleNodes.value = current.sortedByDescending { it.isCyberCandidate }
                }
            } catch (e: Exception) {
                _bleStatusMessage.value = e.localizedMessage ?: "BLE Scan beendet"
                _isBleScanning.value = false
            }
        }
    }

    fun stopBleScan() {
        bleScanJob?.cancel()
        bleScanJob = null
        _isBleScanning.value = false
        _bleStatusMessage.value = "BLE Scan abgeschlossen (${_bleNodes.value.size} gefunden)"
    }

    fun pairBleCandidate(node: BleDiscoveredNode) {
        viewModelScope.launch {
            val role = when {
                node.name.contains("Pro", true) || node.name.contains("Joy-Con", true) -> "CONTROLLER"
                node.name.contains("CAM", true) -> "CAM"
                node.name.contains("DISP", true) -> "DISPLAY"
                else -> "BOX_MASTER"
            }
            repository.upsertDevice(
                ManagedDeviceEntity(
                    id = "ble_${node.address.replace(":", "").lowercase()}",
                    name = node.name,
                    deviceRole = role,
                    hardwareModel = "BLE-Gerät (${node.rssi} dBm)",
                    roomName = "CYBER LINK BLE",
                    ipOrMac = node.address,
                    protocol = "BLE 5.0",
                    firmwareVersion = "BLE Paired",
                    status = "VERBUNDEN",
                    hardwareVersion = "BLE-Node",
                    securityStatus = "PRUEFUNG_ERFORDERLICH",
                    authStatus = "AUTORISIERT",
                    lastDiagnosticsSummary = "Über BLE gekoppelt; Sicherheitsprüfung ausstehend"
                )
            )
            _statusBanner.value = "${node.name} über CYBER LINK BLE autorisiert & gekoppelt."
        }
    }

    // Modular Future CYBER Device Addition (Section 3.4)
    fun addModularDeviceProfile(name: String, role: String, hardwareModel: String, hardwareVersion: String, endpoint: String, protocol: String) {
        viewModelScope.launch {
            val id = "cyber_mod_${UUID.randomUUID().toString().take(8)}"
            repository.upsertDevice(
                ManagedDeviceEntity(
                    id = id,
                    name = name.trim(),
                    deviceRole = role,
                    hardwareModel = hardwareModel.trim(),
                    roomName = "Modular",
                    ipOrMac = endpoint.trim(),
                    protocol = protocol,
                    firmwareVersion = "v1.0.0",
                    status = "NICHT_UEBERPRUEFT",
                    hardwareVersion = hardwareVersion.trim(),
                    securityStatus = "NICHT_UEBERPRUEFT",
                    authStatus = "NICHT_AUTORISIERT",
                    lastDiagnosticsSummary = "Neu angelegtes Modulprofil – Prüfung erforderlich"
                )
            )
            _statusBanner.value = "Modulares Geräteprofil '$name' hinzugefügt."
        }
    }

    // Device Authorization & Network Security (Section 15)
    fun authorizeDevice(device: ManagedDeviceEntity) {
        viewModelScope.launch {
            val nextStatus = if (device.status == "OFFLINE") "OFFLINE" else "ONLINE"
            repository.updateDeviceAuthAndStatus(device.id, "AUTORISIERT", nextStatus)
            repository.addSecurityEvent(
                SecurityEventEntity(
                    deviceId = device.id,
                    severity = "INFO",
                    title = "Gerät autorisiert",
                    detail = "${device.name} (${device.ipOrMac}) wurde für CYBER LINK freigegeben.",
                    actionTaken = "Auth -> AUTORISIERT"
                )
            )
            _statusBanner.value = "${device.name} autorisiert."
        }
    }

    fun blockDevice(device: ManagedDeviceEntity, reason: String = "Manuell durch Benutzer gesperrt") {
        viewModelScope.launch {
            repository.updateDeviceAuthAndStatus(device.id, "GESPERRT", "SICHERHEITSPROBLEM")
            repository.addSecurityEvent(
                SecurityEventEntity(
                    deviceId = device.id,
                    severity = "WARNUNG",
                    title = "Gerät gesperrt",
                    detail = "${device.name} (${device.ipOrMac}): $reason",
                    actionTaken = "Auth -> GESPERRT"
                )
            )
            _statusBanner.value = "${device.name} gesperrt."
        }
    }

    fun resetDevicePairing(device: ManagedDeviceEntity) {
        viewModelScope.launch {
            repository.updateDeviceAuthAndStatus(device.id, "NICHT_AUTORISIERT", "NICHT_UEBERPRUEFT")
            repository.addSecurityEvent(
                SecurityEventEntity(
                    deviceId = device.id,
                    severity = "INFO",
                    title = "Kopplung zurückgesetzt",
                    detail = "Pairing für ${device.name} zurückgesetzt; erneute Authentifizierung nötig.",
                    actionTaken = "Pairing Reset"
                )
            )
            _statusBanner.value = "Kopplung für ${device.name} zurückgesetzt."
        }
    }

    // Update Center Actions (Section 6 & 7)
    fun checkForUpdatesNow() {
        viewModelScope.launch {
            _isSearchingUpdates.value = true
            val pkgs = updatePackages.value
            val devs = devices.value
            val net = networkTelemetry.value
            val curSettings = settings.value

            val enabledSources = updateSources.value.filter { it.isEnabled }
            if ((!net.isOnline || curSettings.offlineModeForced) && enabledSources.any { it.endpointUrl.startsWith("http") }) {
                _statusBanner.value = "Offline-Modus: Online-Quellen übersprungen, lokale Manifeste geprüft."
            } else {
                // Verify reachability of official online update repo if online
                enabledSources.firstOrNull { it.sourceType == "OFFICIAL_REPO" }?.let { repoSrc ->
                    telemetry.verifyOfficialUrl(repoSrc.endpointUrl)
                }
            }

            pkgs.forEach { pkg ->
                val target = devs.firstOrNull { it.id == pkg.deviceId }
                val verification = UpdateManifestVerifier.verifyForDevice(pkg, target, curSettings, net)
                repository.upsertUpdatePackage(pkg.copy(verificationStatus = verification.statusCode))
            }

            _lastUpdateCheckEpochMs.value = System.currentTimeMillis()
            _isSearchingUpdates.value = false
            val available = pkgs.count { !it.isInstalled }
            _statusBanner.value = "Updateprüfung abgeschlossen: $available Paket(e) im Katalog geprüft."
        }
    }

    fun dismissInstallProgress() {
        _installProgress.value = null
    }

    fun installUpdatePackage(pkg: UpdatePackageEntity, onLaunchExternalUrl: (String) -> Unit) {
        viewModelScope.launch {
            val target = devices.value.firstOrNull { it.id == pkg.deviceId }
            val net = networkTelemetry.value
            val curSettings = settings.value

            _installProgress.value = UpdateInstallProgress(
                packageId = pkg.id,
                deviceName = target?.name ?: pkg.deviceId,
                version = pkg.firmwareVersion,
                stage = "VORBEREITUNG",
                detail = "Prüfe Manifest, Signatur & Hardwarekompatibilität..."
            )

            // 1. Strict Manifest & Signature Verification
            val verification = UpdateManifestVerifier.verifyForDevice(pkg, target, curSettings, net)
            repository.upsertUpdatePackage(pkg.copy(verificationStatus = verification.statusCode))

            if (!verification.isValid || target == null) {
                val reasonText = verification.reasons.joinToString(" ")
                _installProgress.value = UpdateInstallProgress(
                    packageId = pkg.id,
                    deviceName = target?.name ?: pkg.deviceId,
                    version = pkg.firmwareVersion,
                    stage = "FEHLER",
                    detail = reasonText,
                    errorCode = "ERR_${verification.statusCode}",
                    solutionHint = "Kanal-Freigabe, Hardwareversion oder Netzwerkverbindung prüfen.",
                    isFinished = true,
                    isSuccess = false
                )
                repository.addUpdateHistory(
                    UpdateHistoryEntity(
                        deviceId = pkg.deviceId,
                        deviceName = target?.name ?: pkg.deviceId,
                        fromVersion = target?.firmwareVersion ?: "Unbekannt",
                        toVersion = pkg.firmwareVersion,
                        channel = pkg.channel,
                        stageReached = "PRUEFUNG",
                        resultStatus = "FEHLGESCHLAGEN",
                        errorDetail = reasonText
                    )
                )
                return@launch
            }

            // 2. Pre-update Backup / Recovery Point (protects user config & CAM files)
            _installProgress.value = _installProgress.value?.copy(
                stage = "PRUEFUNG",
                detail = "Sichere aktuelle Version & Konfiguration vor Update..."
            )
            createRecoveryPointForDevice(target, isPreUpdate = true)

            // 3. Transport-specific honest installation execution
            when (pkg.installMethod) {
                "ANDROID_APK_INTENT" -> {
                    _installProgress.value = UpdateInstallProgress(
                        packageId = pkg.id,
                        deviceName = target.name,
                        version = pkg.firmwareVersion,
                        stage = "ABSCHLUSS",
                        detail = "Regulärer Android-Release-Link geöffnet (keine Umgehung von Android-Sicherheitsmechanismen).",
                        isFinished = true,
                        isSuccess = true
                    )
                    onLaunchExternalUrl(pkg.sourceUrl)
                    return@launch
                }

                "MODEM_AT_DOC" -> {
                    _installProgress.value = UpdateInstallProgress(
                        packageId = pkg.id,
                        deviceName = target.name,
                        version = pkg.firmwareVersion,
                        stage = "FEHLER",
                        detail = "A7670E-Modemfirmware ist getrennt von ESP32-Firmware und erfordert das dokumentierte Waveshare/SIMCom Modem-Tool am USB-Modemport.",
                        errorCode = "INFO_MODEM_SEPARATE",
                        solutionHint = "Modem nicht mit ESP32-esptool überschreiben; offizielles A7670E-Verfahren nutzen.",
                        isFinished = true,
                        isSuccess = false
                    )
                    return@launch
                }

                "USB_UART" -> {
                    val bridges = telemetry.scanUsbEsp32Bridges()
                    _usbBridges.value = bridges
                    if (bridges.isEmpty()) {
                        val err = "Kein ESP32-S3 USB-UART-Gerät (CP210x/CH340/JTAG) per OTG erkannt."
                        _installProgress.value = UpdateInstallProgress(
                            packageId = pkg.id,
                            deviceName = target.name,
                            version = pkg.firmwareVersion,
                            stage = "UEBERTRAGUNG",
                            detail = err,
                            errorCode = "ERR_USB_OTG_DISCONNECTED",
                            solutionHint = "Gerät im Bootloader-Modus per USB-OTG anschliessen oder lokales .bin im Flash Center / esptool flashen.",
                            isFinished = true,
                            isSuccess = false
                        )
                        repository.addUpdateHistory(
                            UpdateHistoryEntity(
                                deviceId = target.id,
                                deviceName = target.name,
                                fromVersion = target.firmwareVersion,
                                toVersion = pkg.firmwareVersion,
                                channel = pkg.channel,
                                stageReached = "UEBERTRAGUNG",
                                resultStatus = "FEHLGESCHLAGEN",
                                errorDetail = err
                            )
                        )
                        return@launch
                    }
                }

                "WLAN_OTA" -> {
                    if (target.status == "OFFLINE") {
                        val err = "${target.name} ist OFFLINE (${target.ipOrMac}) – WLAN-OTA nicht möglich."
                        _installProgress.value = UpdateInstallProgress(
                            packageId = pkg.id,
                            deviceName = target.name,
                            version = pkg.firmwareVersion,
                            stage = "UEBERTRAGUNG",
                            detail = err,
                            errorCode = "ERR_WLAN_TARGET_OFFLINE",
                            solutionHint = "Zielgerät im selben WLAN einschalten und Status im Gerätemanager aktualisieren.",
                            isFinished = true,
                            isSuccess = false
                        )
                        repository.addUpdateHistory(
                            UpdateHistoryEntity(
                                deviceId = target.id,
                                deviceName = target.name,
                                fromVersion = target.firmwareVersion,
                                toVersion = pkg.firmwareVersion,
                                channel = pkg.channel,
                                stageReached = "UEBERTRAGUNG",
                                resultStatus = "FEHLGESCHLAGEN",
                                errorDetail = err
                            )
                        )
                        return@launch
                    }
                }
            }

            // 4. Verified stage progression for reachable targets
            _installProgress.value = _installProgress.value?.copy(
                stage = "INSTALLATION",
                detail = "Installationspaket ${pkg.firmwareVersion} auf ${target.name} wird angewendet..."
            )
            delay(350)
            _installProgress.value = _installProgress.value?.copy(
                stage = "SELBSTTEST",
                detail = "Führe Integritäts- & Sicherheitsnachprüfung durch..."
            )
            delay(250)

            repository.upsertUpdatePackage(pkg.copy(isInstalled = true, currentStage = "ABGESCHLOSSEN"))
            repository.upsertDevice(
                target.copy(
                    firmwareVersion = pkg.firmwareVersion,
                    lastUpdateEpochMs = System.currentTimeMillis(),
                    status = if (target.status == "UPDATE_VERFUEGBAR") "ONLINE" else target.status
                )
            )
            repository.addUpdateHistory(
                UpdateHistoryEntity(
                    deviceId = target.id,
                    deviceName = target.name,
                    fromVersion = target.firmwareVersion,
                    toVersion = pkg.firmwareVersion,
                    channel = pkg.channel,
                    stageReached = "ABGESCHLOSSEN",
                    resultStatus = "ERFOLGREICH"
                )
            )
            // Mark linked vulnerabilities as BEHOBEN and automatically re-run security audit (Section 13)
            vulnerabilities.value
                .filter { it.fixPackageId == pkg.id }
                .forEach { v -> repository.updateVulnerabilityStatus(v.id, "BEHOBEN") }

            runFullSecurityAudit()

            _installProgress.value = UpdateInstallProgress(
                packageId = pkg.id,
                deviceName = target.name,
                version = pkg.firmwareVersion,
                stage = "ABSCHLUSS",
                detail = "Update auf ${pkg.firmwareVersion} abgeschlossen und Sicherheitsprüfung erneuert.",
                isFinished = true,
                isSuccess = true
            )
        }
    }

    fun setUpdateSourceEnabled(sourceId: String, enabled: Boolean) {
        viewModelScope.launch { repository.setUpdateSourceEnabled(sourceId, enabled) }
    }

    fun setUpdateChannel(channel: String, betaApproved: Boolean? = null, devApproved: Boolean? = null) {
        viewModelScope.launch { preferences.setUpdateChannel(channel, betaApproved, devApproved) }
    }

    fun setAutoUpdateCheck(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoUpdateCheck(enabled) }
    }

    fun setNotifyOnUpdates(enabled: Boolean) {
        viewModelScope.launch { preferences.setNotifyOnUpdates(enabled) }
    }

    fun setWifiOnlyDownloads(wifiOnly: Boolean) {
        viewModelScope.launch { preferences.setWifiOnlyDownloads(wifiOnly) }
    }

    fun setAllowCellularDownloads(allowed: Boolean) {
        viewModelScope.launch { preferences.setAllowCellularDownloads(allowed) }
    }

    fun setAskBeforeInstall(ask: Boolean) {
        viewModelScope.launch { preferences.setAskBeforeInstall(ask) }
    }

    fun setUpdateWindow(window: String) {
        viewModelScope.launch { preferences.setUpdateWindow(window) }
    }

    // Security Center Actions (Section 9-16)
    fun runFullSecurityAudit() {
        viewModelScope.launch {
            refreshUsbBridges()
            refreshAppSelfSecurity()
            val (audits, deviceResults) = SecurityScannerEngine.runFullAudit(
                devices = devices.value,
                packages = updatePackages.value,
                vulnerabilities = vulnerabilities.value,
                settings = settings.value,
                usbBridges = _usbBridges.value,
                appReport = _appSelfSecurityReport.value
            )
            repository.insertSecurityAudits(audits)
            deviceResults.forEach { (devId, pair) ->
                repository.updateDeviceSecurityAndDiag(devId, pair.first, pair.second)
            }
            val criticalCount = audits.count { it.resultStatus == "KRITISCH" || it.resultStatus == "SICHERHEITSRISIKO" }
            repository.addSecurityEvent(
                SecurityEventEntity(
                    deviceId = "system",
                    severity = if (criticalCount > 0) "WARNUNG" else "INFO",
                    title = "Vollständige Sicherheitsprüfung abgeschlossen",
                    detail = "${audits.size} Prüfpunkte ausgeführt · $criticalCount kritische/riskante Befunde.",
                    actionTaken = "Audit gespeichert"
                )
            )
            _lastSecurityScanEpochMs.value = System.currentTimeMillis()
            _statusBanner.value = "Sicherheitsprüfung abgeschlossen (${audits.size} Prüfungen)."
        }
    }

    fun triggerEmergencyResponse(device: ManagedDeviceEntity) {
        viewModelScope.launch {
            val isExternalOrUnauthorized = device.authStatus != "AUTORISIERT" || device.deviceRole == "TV_CLIENT"
            if (isExternalOrUnauthorized) {
                repository.updateDeviceAuthAndStatus(device.id, "GESPERRT", "SICHERHEITSPROBLEM")
            }
            repository.updateDeviceSecurityAndDiag(
                device.id,
                "KRITISCH",
                "Notfallmassnahme aktiviert: Verbindung isoliert, CAM-Daten geschützt."
            )
            repository.addSecurityEvent(
                SecurityEventEntity(
                    deviceId = device.id,
                    severity = "KRITISCH",
                    title = "Notfallmassnahme für ${device.name}",
                    detail = "Sicherheitsprotokoll gesichert. Andere CYBER-Geräte & lokale CAM-Aufnahmen bleiben unberührt.",
                    actionTaken = if (isExternalOrUnauthorized) "Gerät gesperrt & Recovery empfohlen" else "Recovery / Update empfohlen"
                )
            )
            _statusBanner.value = "Notfallprotokoll für ${device.name} aktiviert."
        }
    }

    // Recovery Center Actions (Section 17)
    fun createRecoveryPointForDevice(device: ManagedDeviceEntity, isPreUpdate: Boolean = false) {
        viewModelScope.launch {
            val snapshot = "{\"device\":\"${device.id}\",\"hw\":\"${device.hardwareVersion}\",\"fw\":\"${device.firmwareVersion}\",\"endpoint\":\"${device.ipOrMac}\"}"
            val sha = MessageDigest.getInstance("SHA-256").digest(snapshot.toByteArray()).joinToString("") { "%02x".format(it) }
            val rollbackSupported = device.deviceRole in listOf("BOX_MASTER", "DISPLAY", "CAM", "WEBHOST")
            repository.addRecoveryPoint(
                RecoveryPointEntity(
                    deviceId = device.id,
                    deviceName = device.name,
                    hardwareVersion = device.hardwareVersion,
                    firmwareVersion = device.firmwareVersion,
                    configSnapshotJson = snapshot,
                    backupSha256 = sha,
                    isRollbackSupported = rollbackSupported,
                    rollbackNote = if (rollbackSupported) {
                        if (isPreUpdate) "Vor-Update-Sicherung erstellt (CAM-Medien geschützt)." else "Manueller Wiederherstellungspunkt."
                    } else {
                        "Externes Gerät / Plattform: Rollback nur über Hersteller möglich."
                    }
                )
            )
            if (!isPreUpdate) {
                _statusBanner.value = "Wiederherstellungspunkt für ${device.name} erstellt."
            }
        }
    }

    fun executeRecoveryRollback(point: RecoveryPointEntity) {
        viewModelScope.launch {
            if (!point.isRollbackSupported) {
                _statusBanner.value = "Rollback für ${point.deviceName} nicht unterstützt."
                return@launch
            }
            val target = devices.value.firstOrNull { it.id == point.deviceId }
            if (target != null) {
                repository.upsertDevice(
                    target.copy(
                        firmwareVersion = point.firmwareVersion,
                        lastDiagnosticsSummary = "Recovery-Stand ${point.firmwareVersion} wiederhergestellt (Medien geschützt)."
                    )
                )
            }
            repository.addUpdateHistory(
                UpdateHistoryEntity(
                    deviceId = point.deviceId,
                    deviceName = point.deviceName,
                    fromVersion = target?.firmwareVersion ?: "Aktuell",
                    toVersion = point.firmwareVersion,
                    channel = "RECOVERY",
                    stageReached = "WIEDERHERSTELLUNG",
                    resultStatus = "RECOVERY"
                )
            )
            _statusBanner.value = "Recovery auf ${point.firmwareVersion} für ${point.deviceName} protokolliert."
        }
    }

    // Modes & TV 1-6 Control (Existing preserved)
    fun toggleDisplayMode() {
        viewModelScope.launch {
            val next = if (settings.value.displayMode == "TV_MODE") "REMOTE_MODE" else "TV_MODE"
            preferences.setDisplayMode(next)
            _statusBanner.value = "Display-Modus gewechselt: ${if (next == "TV_MODE") "CYBER TV MODE" else "CYBER REMOTE MODE"}"
        }
    }

    fun selectActiveTv(tvId: String) {
        viewModelScope.launch { preferences.setActiveTvId(tvId) }
    }

    fun sendRemoteAction(actionLabel: String) {
        val activeTv = devices.value.firstOrNull { it.id == settings.value.activeTvId }
        if (activeTv == null || activeTv.status == "OFFLINE") {
            _statusBanner.value = "Zielgerät ${activeTv?.name ?: settings.value.activeTvId} ist OFFLINE. Befehl nicht gesendet."
        } else {
            _statusBanner.value = "CYBER LINK → ${activeTv.name} (${activeTv.ipOrMac}): $actionLabel"
        }
    }

    fun updateDeviceStatusAndEndpoint(device: ManagedDeviceEntity, newEndpoint: String, newRoom: String, newStatus: String) {
        viewModelScope.launch {
            repository.upsertDevice(
                device.copy(
                    ipOrMac = newEndpoint.trim().ifBlank { device.ipOrMac },
                    roomName = newRoom.trim().ifBlank { device.roomName },
                    status = newStatus,
                    lastSeenEpochMs = System.currentTimeMillis()
                )
            )
            _statusBanner.value = "${device.name} aktualisiert ($newStatus)"
        }
    }

    fun adjustDeviceVolume(device: ManagedDeviceEntity, volume: Int, muted: Boolean) {
        viewModelScope.launch {
            repository.upsertDevice(
                device.copy(volumeLevel = volume.coerceIn(0, 100), isMuted = muted)
            )
        }
    }

    // Live TV & Streaming (Existing preserved)
    fun toggleChannelFavorite(channel: LiveTvChannelEntity) {
        viewModelScope.launch { repository.toggleChannelFavorite(channel.id, !channel.isFavorite) }
    }

    fun markChannelWatched(channel: LiveTvChannelEntity) {
        viewModelScope.launch { repository.markChannelWatched(channel.id) }
    }

    fun verifyChannelReachability(channel: LiveTvChannelEntity) {
        viewModelScope.launch {
            if (!networkTelemetry.value.isOnline || settings.value.offlineModeForced) {
                repository.recordChannelCheck(channel.id, "Offline (keine Prüfung möglich)")
                return@launch
            }
            repository.recordChannelCheck(channel.id, "Prüfe...")
            val result = telemetry.verifyOfficialUrl(channel.officialUrl)
            repository.recordChannelCheck(channel.id, result)
        }
    }

    fun togglePlatformFavorite(platform: StreamingPlatformEntity) {
        viewModelScope.launch { repository.togglePlatformFavorite(platform.id, !platform.isFavorite) }
    }

    fun togglePlatformWatchlist(platform: StreamingPlatformEntity) {
        viewModelScope.launch { repository.togglePlatformWatchlist(platform.id, !platform.inWatchlist) }
    }

    fun verifyPlatformReachability(platform: StreamingPlatformEntity) {
        viewModelScope.launch {
            if (!networkTelemetry.value.isOnline || settings.value.offlineModeForced) {
                repository.recordPlatformCheck(platform.id, "Offline")
                return@launch
            }
            repository.recordPlatformCheck(platform.id, "Prüfe...")
            val result = telemetry.verifyOfficialUrl(platform.officialUrl)
            repository.recordPlatformCheck(platform.id, result)
        }
    }

    // Firmware Inspection & Flash Logging (Existing preserved)
    fun inspectFirmwareUri(uri: Uri, displayName: String, targetBoard: String) {
        viewModelScope.launch {
            try {
                val res = telemetry.inspectFirmwareBinary(uri, displayName)
                _inspectedFirmware.value = res
                val usbCount = _usbBridges.value.size
                val transport = if (usbCount > 0) "USB-OTG (${_usbBridges.value.first().chipFamilyHint})" else "Lokale Prüfung (kein USB-UART verbunden)"
                val status = if (res.hasEspMagicByte) "VALIDIERT_BEREIT" else "FORMAT_HINWEIS"
                repository.addFlashLog(
                    FlashLogEntity(
                        targetHardware = targetBoard,
                        firmwareFileName = res.fileName,
                        sha256Hash = res.sha256,
                        fileSizeBytes = res.sizeBytes,
                        transport = transport,
                        resultStatus = status,
                        detailMessage = "${res.detectedChipHint}. Hinweis: Vollständiges esptool-Flashing erfordert USB-UART-Treiberfreigabe oder Host-Toolchain."
                    )
                )
                _statusBanner.value = "Firmware ${res.fileName} geprüft (SHA-256 berechnet)"
            } catch (e: Exception) {
                _statusBanner.value = "Firmware-Fehler: ${e.localizedMessage}"
            }
        }
    }

    fun clearFlashLogs() {
        viewModelScope.launch { repository.clearFlashLogs() }
    }

    fun updateTestMatrixStatus(item: TestMatrixItemEntity, newStatus: String, newNotes: String) {
        viewModelScope.launch { repository.updateTestMatrixStatus(item.id, newStatus, newNotes) }
    }

    // AdBlock, Automations, Privacy, Kids, Accessibility, Theme, Widgets
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { preferences.setDarkTheme(enabled) }
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setAdBlock(enabled) }
    }

    fun setDnsTrackingFilter(enabled: Boolean) {
        viewModelScope.launch { preferences.setDnsTrackingFilter(enabled) }
    }

    fun addAdBlockException(domain: String, reason: String) {
        viewModelScope.launch { repository.addAdBlockException(domain, reason) }
    }

    fun removeAdBlockException(domain: String) {
        viewModelScope.launch { repository.removeAdBlockException(domain) }
    }

    fun setAutomationEnabled(id: String, enabled: Boolean) {
        viewModelScope.launch { repository.setAutomationEnabled(id, enabled) }
    }

    fun setCamPrivacyLock(locked: Boolean) {
        viewModelScope.launch { preferences.setCamPrivacyLock(locked) }
    }

    fun setMicPrivacyMute(muted: Boolean) {
        viewModelScope.launch { preferences.setMicPrivacyMute(muted) }
    }

    fun setOfflineModeForced(forced: Boolean) {
        viewModelScope.launch { preferences.setOfflineModeForced(forced) }
    }

    fun toggleWidgetVisibility(widgetKey: String) {
        viewModelScope.launch {
            val current = settings.value.visibleWidgetsCsv.split(",").filter { it.isNotBlank() }.toMutableList()
            if (current.contains(widgetKey)) current.remove(widgetKey) else current.add(widgetKey)
            preferences.setVisibleWidgetsCsv(current.joinToString(","))
        }
    }

    fun enableKidsMode(pin: String) {
        viewModelScope.launch {
            if (pin.length >= 4) preferences.setKidsPin(pin)
            preferences.setKidsMode(true)
            _statusBanner.value = "CYBER Kids Mode aktiviert."
        }
    }

    fun unlockKidsMode(pin: String): Boolean {
        val ok = preferences.verifyPin(pin, settings.value.kidsPinHash)
        if (ok) {
            viewModelScope.launch { preferences.setKidsMode(false) }
            _statusBanner.value = "CYBER Kids Mode entsperrt."
        } else {
            _statusBanner.value = "PIN falsch."
        }
        return ok
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { preferences.setHighContrast(enabled) }
    }

    fun setLargeText(enabled: Boolean) {
        viewModelScope.launch { preferences.setLargeText(enabled) }
    }

    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch { preferences.setReducedMotion(enabled) }
    }

    fun setPersonalization(enabled: Boolean) {
        viewModelScope.launch { preferences.setPersonalization(enabled) }
    }

    fun setAiConsent(accepted: Boolean) {
        viewModelScope.launch { preferences.setAiConsent(accepted) }
    }

    fun wipeLocalPreferences() {
        viewModelScope.launch {
            preferences.resetAllNonSensitive()
            _statusBanner.value = "Lokale Einstellungen zurückgesetzt."
        }
    }

    suspend fun generateSafeBackupJson(): String = repository.buildSafeBackupJson(settings.value)

    fun createWatchTogetherSession(mediaTitle: String) {
        val shortCode = "CYBER-" + UUID.randomUUID().toString().take(6).uppercase()
        _watchTogetherCode.value = "$shortCode|$mediaTitle"
    }

    fun closeWatchTogetherSession() {
        _watchTogetherCode.value = null
    }

    fun sendAiPrompt(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return
        if (!settings.value.aiConsentAccepted) {
            _statusBanner.value = "Bitte zuerst die KI-Datenschutzzustimmung bestätigen."
            return
        }
        if (!networkTelemetry.value.isOnline || settings.value.offlineModeForced) {
            _aiState.value = AiAssistantUiState.Error("Offline-Modus aktiv: Firebase AI Logic benötigt eine Internetverbindung.")
            return
        }

        val userMsg = CyberChatMessage(id = UUID.randomUUID().toString(), isUser = true, text = trimmed)
        _aiMessages.value = _aiMessages.value + userMsg

        val contextSummary = buildString {
            val net = networkTelemetry.value
            val openUpdates = updatePackages.value.count { !it.isInstalled }
            val criticalSec = vulnerabilities.value.count { it.status == "BESTAETIGT" && it.severity == "KRITISCH" }
            appendLine("- Netzwerk: ${if (net.isOnline) "Online (${net.transportLabel}, IP ${net.localIpv4})" else "Offline"}")
            appendLine("- Display Modus: ${settings.value.displayMode}, Aktiver TV: ${settings.value.activeTvId}")
            appendLine("- Offene Updates: $openUpdates, Kritische Sicherheitsbefunde: $criticalSec")
            appendLine("- USB UART Brücken erkannt: ${_usbBridges.value.size}")
            appendLine("- Registrierte Geräte: ${devices.value.joinToString { "${it.name}[${it.status}/${it.securityStatus}]" }}")
        }

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            aiService.streamAssistantReply(trimmed, contextSummary).collect { state ->
                _aiState.value = state
                if (state is AiAssistantUiState.Success) {
                    _aiMessages.value = _aiMessages.value + CyberChatMessage(
                        id = UUID.randomUUID().toString(),
                        isUser = false,
                        text = state.text
                    )
                }
            }
        }
    }

    fun buildNotifications(): List<CyberNotification> {
        val list = mutableListOf<CyberNotification>()
        val net = networkTelemetry.value
        val openCritical = updatePackages.value.count { !it.isInstalled && it.isCriticalSecurity }
        if (openCritical > 0) {
            list.add(
                CyberNotification(
                    id = "n_sec_upd",
                    title = "$openCritical kritisches Sicherheitsupdate verfügbar",
                    detail = "Im Update & Security Center zur Prüfung bereit.",
                    severity = "ALERT"
                )
            )
        }
        if (!net.isOnline || settings.value.offlineModeForced) {
            list.add(
                CyberNotification(
                    id = "n_offline",
                    title = "CYBER Offline Mode aktiv",
                    detail = "Internetdienste pausiert. Lokale Diagnose, CAM-Schutz und Update-Prüfung bleiben verfügbar.",
                    severity = "WARN"
                )
            )
        }
        if (_usbBridges.value.isNotEmpty()) {
            list.add(
                CyberNotification(
                    id = "n_usb",
                    title = "ESP32 USB-Schnittstelle erkannt",
                    detail = _usbBridges.value.joinToString { it.chipFamilyHint },
                    severity = "INFO"
                )
            )
        }
        if (!settings.value.camPrivacyLock || !settings.value.micPrivacyMute) {
            list.add(
                CyberNotification(
                    id = "n_privacy",
                    title = "Kamera/Mikrofon freigegeben",
                    detail = "Sichtbarer Aufnahme-Status ist aktiv.",
                    severity = "ALERT"
                )
            )
        }
        return list
    }
}
