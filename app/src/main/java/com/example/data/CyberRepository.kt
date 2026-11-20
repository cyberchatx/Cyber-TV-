package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class CyberRepository(private val dao: CyberDao) {
    val channels: Flow<List<LiveTvChannelEntity>> = dao.observeChannels()
    val platforms: Flow<List<StreamingPlatformEntity>> = dao.observePlatforms()
    val devices: Flow<List<ManagedDeviceEntity>> = dao.observeDevices()
    val testMatrix: Flow<List<TestMatrixItemEntity>> = dao.observeTestMatrix()
    val adBlockExceptions: Flow<List<AdBlockExceptionEntity>> = dao.observeAdBlockExceptions()
    val flashLogs: Flow<List<FlashLogEntity>> = dao.observeFlashLogs()
    val automations: Flow<List<AutomationRuleEntity>> = dao.observeAutomations()

    // Update & Security Center Flows
    val updatePackages: Flow<List<UpdatePackageEntity>> = dao.observeUpdatePackages()
    val updateHistory: Flow<List<UpdateHistoryEntity>> = dao.observeUpdateHistory()
    val updateSources: Flow<List<UpdateSourceEntity>> = dao.observeUpdateSources()
    val vulnerabilities: Flow<List<VulnerabilityEntity>> = dao.observeVulnerabilities()
    val securityAudits: Flow<List<SecurityAuditEntity>> = dao.observeSecurityAudits()
    val securityEvents: Flow<List<SecurityEventEntity>> = dao.observeSecurityEvents()
    val recoveryPoints: Flow<List<RecoveryPointEntity>> = dao.observeRecoveryPoints()

    suspend fun ensureSeeded() {
        if (dao.countChannels() == 0) {
            dao.insertChannels(CyberSeedData.liveTvChannels)
        }
        if (dao.countPlatforms() == 0) {
            dao.insertPlatforms(CyberSeedData.streamingPlatforms)
        }
        if (dao.countDevices() == 0) {
            dao.insertDevices(CyberSeedData.initialDevices)
        } else {
            // Ensure new ecosystem entries (CYBER OS, CYBER WEBHOST, CYBER FLASH APP) exist after DB migration
            val existingIds = dao.observeDevices().first().map { it.id }.toSet()
            val missing = CyberSeedData.initialDevices.filter { it.id !in existingIds }
            if (missing.isNotEmpty()) {
                dao.insertDevices(missing)
            }
        }
        if (dao.countTestMatrix() == 0) {
            dao.insertTestMatrix(CyberSeedData.initialTestMatrix)
        }
        if (dao.countAutomations() == 0) {
            dao.insertAutomations(CyberSeedData.initialAutomations)
        }
        if (dao.countUpdateSources() == 0) {
            dao.insertUpdateSources(CyberSeedData.initialUpdateSources)
        }
        if (dao.countUpdatePackages() == 0) {
            dao.insertUpdatePackages(CyberSeedData.initialUpdatePackages)
        }
        if (dao.countVulnerabilities() == 0) {
            dao.insertVulnerabilities(CyberSeedData.initialVulnerabilities)
        }
        if (dao.countRecoveryPoints() == 0) {
            CyberSeedData.initialRecoveryPoints.forEach { dao.insertRecoveryPoint(it) }
        }
    }

    suspend fun toggleChannelFavorite(id: String, favorite: Boolean) =
        dao.setChannelFavorite(id, favorite)

    suspend fun markChannelWatched(id: String) =
        dao.markChannelWatched(id, System.currentTimeMillis())

    suspend fun recordChannelCheck(id: String, status: String) =
        dao.updateChannelCheck(id, System.currentTimeMillis(), status)

    suspend fun togglePlatformFavorite(id: String, favorite: Boolean) =
        dao.setPlatformFavorite(id, favorite)

    suspend fun togglePlatformWatchlist(id: String, inList: Boolean) =
        dao.setPlatformWatchlist(id, inList)

    suspend fun recordPlatformCheck(id: String, status: String) =
        dao.updatePlatformCheck(id, System.currentTimeMillis(), status)

    suspend fun upsertDevice(device: ManagedDeviceEntity) =
        dao.upsertDevice(device)

    suspend fun deleteDevice(id: String) =
        dao.deleteDevice(id)

    suspend fun updateDeviceAuthAndStatus(id: String, authStatus: String, status: String) =
        dao.updateDeviceAuthAndStatus(id, authStatus, status)

    suspend fun updateDeviceSecurityAndDiag(id: String, securityStatus: String, diagSummary: String) =
        dao.updateDeviceSecurityAndDiag(id, securityStatus, diagSummary)

    suspend fun updateTestMatrixStatus(id: String, status: String, notes: String) =
        dao.updateTestMatrixStatus(id, status, notes, System.currentTimeMillis())

    suspend fun addAdBlockException(domain: String, reason: String) {
        val cleaned = domain.trim().lowercase().removePrefix("https://").removePrefix("http://").substringBefore("/")
        if (cleaned.isNotBlank()) {
            dao.insertAdBlockException(AdBlockExceptionEntity(domain = cleaned, reason = reason.ifBlank { "Manuelle Ausnahme" }))
        }
    }

    suspend fun removeAdBlockException(domain: String) =
        dao.deleteAdBlockException(domain)

    suspend fun addFlashLog(log: FlashLogEntity) =
        dao.insertFlashLog(log)

    suspend fun clearFlashLogs() =
        dao.clearFlashLogs()

    suspend fun setAutomationEnabled(id: String, enabled: Boolean) =
        dao.setAutomationEnabled(id, enabled)

    // Update & Security Persistence
    suspend fun upsertUpdatePackage(pkg: UpdatePackageEntity) =
        dao.upsertUpdatePackage(pkg)

    suspend fun addUpdateHistory(entry: UpdateHistoryEntity) =
        dao.insertUpdateHistory(entry)

    suspend fun setUpdateSourceEnabled(id: String, enabled: Boolean) =
        dao.setUpdateSourceEnabled(id, enabled)

    suspend fun insertSecurityAudits(audits: List<SecurityAuditEntity>) =
        dao.insertSecurityAudits(audits)

    suspend fun updateVulnerabilityStatus(id: String, status: String) =
        dao.updateVulnerabilityStatus(id, status)

    suspend fun addSecurityEvent(event: SecurityEventEntity) =
        dao.insertSecurityEvent(event)

    suspend fun addRecoveryPoint(point: RecoveryPointEntity) =
        dao.insertRecoveryPoint(point)

    suspend fun buildSafeBackupJson(settings: CyberSettingsState): String {
        val root = JSONObject()
        root.put("schema", "CYBER_TV_BOX_V9_BACKUP")
        root.put("exportedAtMs", System.currentTimeMillis())
        root.put("privacyNotice", "Keine Passwörter, SIM-PINs oder geheime Schlüssel enthalten.")

        val prefsObj = JSONObject().apply {
            put("displayMode", settings.displayMode)
            put("activeTvId", settings.activeTvId)
            put("darkThemeEnabled", settings.darkThemeEnabled)
            put("updateChannel", settings.updateChannel)
            put("wifiOnlyDownloads", settings.wifiOnlyDownloads)
            put("adBlockEnabled", settings.adBlockEnabled)
            put("dnsTrackingFilterEnabled", settings.dnsTrackingFilterEnabled)
            put("kidsModeEnabled", settings.kidsModeEnabled)
            put("highContrastEnabled", settings.highContrastEnabled)
            put("largeTextEnabled", settings.largeTextEnabled)
            put("personalizationEnabled", settings.personalizationEnabled)
            put("visibleWidgetsCsv", settings.visibleWidgetsCsv)
        }
        root.put("settings", prefsObj)

        val devArr = JSONArray()
        devices.first().forEach { d ->
            devArr.put(JSONObject().apply {
                put("id", d.id)
                put("name", d.name)
                put("role", d.deviceRole)
                put("model", d.hardwareModel)
                put("hwVersion", d.hardwareVersion)
                put("room", d.roomName)
                put("endpoint", d.ipOrMac)
                put("firmware", d.firmwareVersion)
                put("authStatus", d.authStatus)
                put("securityStatus", d.securityStatus)
            })
        }
        root.put("devices", devArr)

        val favChannels = JSONArray()
        channels.first().filter { it.isFavorite }.forEach { favChannels.put(it.id) }
        root.put("favoriteChannels", favChannels)

        val favPlatforms = JSONArray()
        platforms.first().filter { it.isFavorite || it.inWatchlist }.forEach { favPlatforms.put(it.id) }
        root.put("favoritePlatforms", favPlatforms)

        val excArr = JSONArray()
        adBlockExceptions.first().forEach { excArr.put(it.domain) }
        root.put("adBlockExceptions", excArr)

        return root.toString(2)
    }
}
