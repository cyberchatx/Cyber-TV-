package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CyberDao {
    // Live TV
    @Query("SELECT * FROM live_tv_channels ORDER BY isFavorite DESC, country ASC, name ASC")
    fun observeChannels(): Flow<List<LiveTvChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<LiveTvChannelEntity>)

    @Query("UPDATE live_tv_channels SET isFavorite = :fav WHERE id = :id")
    suspend fun setChannelFavorite(id: String, fav: Boolean)

    @Query("UPDATE live_tv_channels SET lastWatchedEpochMs = :timestamp WHERE id = :id")
    suspend fun markChannelWatched(id: String, timestamp: Long)

    @Query("UPDATE live_tv_channels SET lastCheckedEpochMs = :timestamp, lastCheckStatus = :status WHERE id = :id")
    suspend fun updateChannelCheck(id: String, timestamp: Long, status: String)

    @Query("SELECT COUNT(*) FROM live_tv_channels")
    suspend fun countChannels(): Int

    // Streaming Platforms
    @Query("SELECT * FROM streaming_platforms ORDER BY isFavorite DESC, name ASC")
    fun observePlatforms(): Flow<List<StreamingPlatformEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatforms(platforms: List<StreamingPlatformEntity>)

    @Query("UPDATE streaming_platforms SET isFavorite = :fav WHERE id = :id")
    suspend fun setPlatformFavorite(id: String, fav: Boolean)

    @Query("UPDATE streaming_platforms SET inWatchlist = :inList WHERE id = :id")
    suspend fun setPlatformWatchlist(id: String, inList: Boolean)

    @Query("UPDATE streaming_platforms SET lastCheckedEpochMs = :timestamp, lastCheckStatus = :status WHERE id = :id")
    suspend fun updatePlatformCheck(id: String, timestamp: Long, status: String)

    @Query("SELECT COUNT(*) FROM streaming_platforms")
    suspend fun countPlatforms(): Int

    // Managed Devices
    @Query("SELECT * FROM managed_devices ORDER BY deviceRole ASC, id ASC")
    fun observeDevices(): Flow<List<ManagedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDevice(device: ManagedDeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<ManagedDeviceEntity>)

    @Query("DELETE FROM managed_devices WHERE id = :id")
    suspend fun deleteDevice(id: String)

    @Query("SELECT COUNT(*) FROM managed_devices")
    suspend fun countDevices(): Int

    @Query("UPDATE managed_devices SET authStatus = :authStatus, status = :status WHERE id = :id")
    suspend fun updateDeviceAuthAndStatus(id: String, authStatus: String, status: String)

    @Query("UPDATE managed_devices SET securityStatus = :securityStatus, lastDiagnosticsSummary = :diagSummary WHERE id = :id")
    suspend fun updateDeviceSecurityAndDiag(id: String, securityStatus: String, diagSummary: String)

    // Test Matrix
    @Query("SELECT * FROM test_matrix_items ORDER BY component ASC, id ASC")
    fun observeTestMatrix(): Flow<List<TestMatrixItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestMatrix(items: List<TestMatrixItemEntity>)

    @Query("UPDATE test_matrix_items SET status = :status, notes = :notes, updatedEpochMs = :timestamp WHERE id = :id")
    suspend fun updateTestMatrixStatus(id: String, status: String, notes: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM test_matrix_items")
    suspend fun countTestMatrix(): Int

    // AdBlock Exceptions
    @Query("SELECT * FROM adblock_exceptions ORDER BY addedEpochMs DESC")
    fun observeAdBlockExceptions(): Flow<List<AdBlockExceptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdBlockException(item: AdBlockExceptionEntity)

    @Query("DELETE FROM adblock_exceptions WHERE domain = :domain")
    suspend fun deleteAdBlockException(domain: String)

    // Flash Logs
    @Query("SELECT * FROM flash_logs ORDER BY timestampEpochMs DESC")
    fun observeFlashLogs(): Flow<List<FlashLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashLog(log: FlashLogEntity)

    @Query("DELETE FROM flash_logs")
    suspend fun clearFlashLogs()

    // Automations
    @Query("SELECT * FROM automation_rules ORDER BY id ASC")
    fun observeAutomations(): Flow<List<AutomationRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomations(rules: List<AutomationRuleEntity>)

    @Query("UPDATE automation_rules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setAutomationEnabled(id: String, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM automation_rules")
    suspend fun countAutomations(): Int

    // Update Packages & History
    @Query("SELECT * FROM update_packages ORDER BY isCriticalSecurity DESC, deviceId ASC")
    fun observeUpdatePackages(): Flow<List<UpdatePackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdatePackages(packages: List<UpdatePackageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUpdatePackage(pkg: UpdatePackageEntity)

    @Query("SELECT COUNT(*) FROM update_packages")
    suspend fun countUpdatePackages(): Int

    @Query("SELECT * FROM update_history ORDER BY timestampEpochMs DESC")
    fun observeUpdateHistory(): Flow<List<UpdateHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateHistory(entry: UpdateHistoryEntity)

    // Update Sources
    @Query("SELECT * FROM update_sources ORDER BY isTrustedByDefault DESC, id ASC")
    fun observeUpdateSources(): Flow<List<UpdateSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateSources(sources: List<UpdateSourceEntity>)

    @Query("UPDATE update_sources SET isEnabled = :enabled WHERE id = :id")
    suspend fun setUpdateSourceEnabled(id: String, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM update_sources")
    suspend fun countUpdateSources(): Int

    // Vulnerabilities, Security Audits, Security Events
    @Query("SELECT * FROM vulnerabilities ORDER BY status ASC, severity ASC")
    fun observeVulnerabilities(): Flow<List<VulnerabilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVulnerabilities(items: List<VulnerabilityEntity>)

    @Query("UPDATE vulnerabilities SET status = :status WHERE id = :id")
    suspend fun updateVulnerabilityStatus(id: String, status: String)

    @Query("SELECT COUNT(*) FROM vulnerabilities")
    suspend fun countVulnerabilities(): Int

    @Query("SELECT * FROM security_audits ORDER BY checkedEpochMs DESC, deviceId ASC")
    fun observeSecurityAudits(): Flow<List<SecurityAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityAudits(audits: List<SecurityAuditEntity>)

    @Query("SELECT * FROM security_events ORDER BY timestampEpochMs DESC")
    fun observeSecurityEvents(): Flow<List<SecurityEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityEvent(event: SecurityEventEntity)

    // Recovery Points
    @Query("SELECT * FROM recovery_points ORDER BY createdEpochMs DESC")
    fun observeRecoveryPoints(): Flow<List<RecoveryPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryPoint(point: RecoveryPointEntity)

    @Query("SELECT COUNT(*) FROM recovery_points")
    suspend fun countRecoveryPoints(): Int
}
