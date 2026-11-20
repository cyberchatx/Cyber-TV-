package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "live_tv_channels")
data class LiveTvChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val language: String,
    val category: String,
    val provider: String,
    val officialUrl: String,
    val availabilityNote: String,
    val geoblockingNote: String,
    val isFree: Boolean,
    val accountRequired: Boolean,
    val supportedClients: String,
    val currentProgramTitle: String,
    val nextProgramTitle: String,
    val epgAvailable: Boolean,
    val replaySupported: Boolean,
    val isFavorite: Boolean = false,
    val lastWatchedEpochMs: Long = 0L,
    val lastCheckedEpochMs: Long = 0L,
    val lastCheckStatus: String = "Offizieller Link hinterlegt"
)

@Entity(tableName = "streaming_platforms")
data class StreamingPlatformEntity(
    @PrimaryKey val id: String,
    val name: String,
    val officialUrl: String,
    val category: String,
    val country: String,
    val language: String,
    val isFree: Boolean,
    val loginNote: String,
    val supportedDevices: String,
    val isFavorite: Boolean = false,
    val inWatchlist: Boolean = false,
    val lastCheckedEpochMs: Long = 0L,
    val lastCheckStatus: String = "Offizielle Plattform"
)

@Entity(tableName = "managed_devices")
data class ManagedDeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val deviceRole: String, // BOX_MASTER, DISPLAY, CAM, TV_CLIENT, CONTROLLER, SMART_HOME, APP_SELF, WEBHOST, FUTURE_CYBER
    val hardwareModel: String,
    val roomName: String,
    val ipOrMac: String,
    val protocol: String, // BLE, WLAN, USB-OTG, MQTT/REST, ANDROID
    val firmwareVersion: String,
    val status: String, // ONLINE, OFFLINE, STANDBY, USB_DETECTED, VERBUNDEN, UPDATE_VERFUEGBAR, UPDATE_LAEUFT, SICHERHEITSPROBLEM, FEHLER, NICHT_UEBERPRUEFT
    val volumeLevel: Int = 50,
    val isMuted: Boolean = false,
    val lastSeenEpochMs: Long = System.currentTimeMillis(),
    // Extended Universal Device Manager fields (Section 4 & 15)
    val hardwareVersion: String = "V2.0",
    val batteryPercent: Int = -1, // -1 = nicht verfügbar / Netzbetrieb
    val storageSummary: String = "Nicht geprüft",
    val lastUpdateEpochMs: Long = 0L,
    val securityStatus: String = "NICHT_UEBERPRUEFT", // GESCHUETZT, PRUEFUNG_ERFORDERLICH, UPDATE_VERFUEGBAR, SICHERHEITSRISIKO, KRITISCH, NICHT_UEBERPRUEFT
    val authStatus: String = "NICHT_AUTORISIERT", // AUTORISIERT, GESPERRT, UNBEKANNT, NICHT_AUTORISIERT
    val lastDiagnosticsSummary: String = "Noch keine Diagnose ausgeführt"
)

@Entity(tableName = "test_matrix_items")
data class TestMatrixItemEntity(
    @PrimaryKey val id: String,
    val component: String,
    val featureName: String,
    val status: String, // NICHT_IMPLEMENTIERT, IMPLEMENTIERT, BUILD_ERFOLGREICH, SIMULIERT_GETESTET, HARDWARETEST_BESTANDEN, FEHLGESCHLAGEN
    val notes: String,
    val updatedEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "adblock_exceptions")
data class AdBlockExceptionEntity(
    @PrimaryKey val domain: String,
    val reason: String,
    val addedEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "flash_logs")
data class FlashLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetHardware: String,
    val firmwareFileName: String,
    val sha256Hash: String,
    val fileSizeBytes: Long,
    val transport: String,
    val resultStatus: String,
    val detailMessage: String,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val triggerText: String,
    val actionText: String,
    val isEnabled: Boolean,
    val requiresConfirmation: Boolean
)

// --- NEW ENTITIES FOR UNIVERSAL UPDATE & SECURITY CENTER ---

@Entity(tableName = "update_packages")
data class UpdatePackageEntity(
    @PrimaryKey val id: String,
    val productId: String, // e.g. CYBER-BOX-A7670E
    val deviceId: String, // maps to ManagedDeviceEntity.id
    val hardwareVersion: String,
    val firmwareVersion: String,
    val minRequiredVersion: String,
    val channel: String, // STABLE, BETA, DEVELOPMENT
    val sourceName: String, // Offizielles CYBER-Repo, GitHub Releases, CYBER WEBHOST, Lokale Datei
    val sourceUrl: String,
    val packageSizeBytes: Long,
    val sha256Checksum: String,
    val signatureDigest: String,
    val publisherKeyId: String,
    val releaseDate: String,
    val changelog: String,
    val dependenciesText: String,
    val installMethod: String, // USB_UART, WLAN_OTA, MODEM_AT_DOC, LOCAL_FILE, ANDROID_APK_INTENT, UNSUPPORTED_EXTERNAL
    val isCriticalSecurity: Boolean = false,
    val isInstalled: Boolean = false,
    val verificationStatus: String = "NICHT_GEPRUEFT", // NICHT_GEPRUEFT, VERIFIZIERT, SIGNATUR_FEHLER, INKOMPATIBEL
    val currentStage: String = "BEREIT" // BEREIT, VORBEREITUNG, DOWNLOAD, PRUEFUNG, UEBERTRAGUNG, INSTALLATION, NEUSTART, SELBSTTEST, ABGESCHLOSSEN, FEHLER
)

@Entity(tableName = "update_history")
data class UpdateHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceId: String,
    val deviceName: String,
    val fromVersion: String,
    val toVersion: String,
    val channel: String,
    val stageReached: String,
    val resultStatus: String, // ERFOLGREICH, ABGEBROCHEN, FEHLGESCHLAGEN, RECOVERY
    val errorDetail: String = "",
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "vulnerabilities")
data class VulnerabilityEntity(
    @PrimaryKey val id: String,
    val cveId: String, // Real advisory ID or internal advisory ref
    val affectedComponent: String,
    val affectedVersion: String,
    val description: String,
    val severity: String, // KRITISCH, HOCH, MITTEL, NIEDRIG
    val cvssScore: String,
    val source: String,
    val publishedDate: String,
    val fixPackageId: String, // Links directly to UpdatePackageEntity.id if available
    val recommendedAction: String,
    val status: String // BESTAETIGT, MOEGLICH, NICHT_UEBERPRUEFT, BEHOBEN
)

@Entity(tableName = "security_audits")
data class SecurityAuditEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val deviceName: String,
    val checkName: String,
    val category: String,
    val resultStatus: String, // GESCHUETZT, PRUEFUNG_ERFORDERLICH, UPDATE_VERFUEGBAR, SICHERHEITSRISIKO, KRITISCH, NICHT_UEBERPRUEFBAR
    val detail: String,
    val checkedEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "security_events")
data class SecurityEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceId: String,
    val severity: String, // INFO, WARNUNG, KRITISCH
    val title: String,
    val detail: String,
    val actionTaken: String,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "recovery_points")
data class RecoveryPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceId: String,
    val deviceName: String,
    val hardwareVersion: String,
    val firmwareVersion: String,
    val configSnapshotJson: String,
    val backupSha256: String,
    val isRollbackSupported: Boolean,
    val rollbackNote: String,
    val createdEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "update_sources")
data class UpdateSourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sourceType: String, // OFFICIAL_REPO, GITHUB_RELEASES, WEBHOST, LOCAL_STORAGE, SD_CARD
    val endpointUrl: String,
    val isEnabled: Boolean = true,
    val isTrustedByDefault: Boolean = false
)

data class SwissApnProfile(
    val carrier: String,
    val apn: String,
    val authType: String,
    val note: String
)

data class HardwareSpecRow(
    val component: String,
    val boardName: String,
    val mcuAndMemory: String,
    val interfaces: String,
    val mandatoryBoundaries: String
)

data class CompetitorRow(
    val feature: String,
    val swisscomSunriseSalt: String,
    val zattooTeleboy: String,
    val appleGoogleFire: String,
    val cyberStatus: String,
    val cyberLimit: String
)

data class BlueprintArtifact(
    val id: String,
    val title: String,
    val targetPath: String,
    val language: String,
    val summary: String,
    val codeContent: String
)

enum class TestStatusOption(val code: String, val label: String) {
    NICHT_IMPLEMENTIERT("NICHT_IMPLEMENTIERT", "NICHT IMPLEMENTIERT"),
    IMPLEMENTIERT("IMPLEMENTIERT", "IMPLEMENTIERT"),
    BUILD_ERFOLGREICH("BUILD_ERFOLGREICH", "BUILD ERFOLGREICH"),
    SIMULIERT_GETESTET("SIMULIERT_GETESTET", "SIMULIERT GETESTET"),
    HARDWARETEST_BESTANDEN("HARDWARETEST_BESTANDEN", "HARDWARETEST BESTANDEN"),
    FEHLGESCHLAGEN("FEHLGESCHLAGEN", "FEHLGESCHLAGEN");

    companion object {
        fun fromCode(code: String): TestStatusOption =
            entries.firstOrNull { it.code == code } ?: IMPLEMENTIERT
    }
}
