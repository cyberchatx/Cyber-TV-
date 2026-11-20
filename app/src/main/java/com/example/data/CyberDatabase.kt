package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        LiveTvChannelEntity::class,
        StreamingPlatformEntity::class,
        ManagedDeviceEntity::class,
        TestMatrixItemEntity::class,
        AdBlockExceptionEntity::class,
        FlashLogEntity::class,
        AutomationRuleEntity::class,
        UpdatePackageEntity::class,
        UpdateHistoryEntity::class,
        VulnerabilityEntity::class,
        SecurityAuditEntity::class,
        SecurityEventEntity::class,
        RecoveryPointEntity::class,
        UpdateSourceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CyberDatabase : RoomDatabase() {
    abstract fun cyberDao(): CyberDao

    companion object {
        @Volatile
        private var INSTANCE: CyberDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN hardwareVersion TEXT NOT NULL DEFAULT 'V2.0'")
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN batteryPercent INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN storageSummary TEXT NOT NULL DEFAULT 'Nicht geprüft'")
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN lastUpdateEpochMs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN securityStatus TEXT NOT NULL DEFAULT 'NICHT_UEBERPRUEFT'")
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN authStatus TEXT NOT NULL DEFAULT 'NICHT_AUTORISIERT'")
                db.execSQL("ALTER TABLE managed_devices ADD COLUMN lastDiagnosticsSummary TEXT NOT NULL DEFAULT 'Noch keine Diagnose ausgeführt'")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `update_packages` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `productId` TEXT NOT NULL,
                        `deviceId` TEXT NOT NULL,
                        `hardwareVersion` TEXT NOT NULL,
                        `firmwareVersion` TEXT NOT NULL,
                        `minRequiredVersion` TEXT NOT NULL,
                        `channel` TEXT NOT NULL,
                        `sourceName` TEXT NOT NULL,
                        `sourceUrl` TEXT NOT NULL,
                        `packageSizeBytes` INTEGER NOT NULL,
                        `sha256Checksum` TEXT NOT NULL,
                        `signatureDigest` TEXT NOT NULL,
                        `publisherKeyId` TEXT NOT NULL,
                        `releaseDate` TEXT NOT NULL,
                        `changelog` TEXT NOT NULL,
                        `dependenciesText` TEXT NOT NULL,
                        `installMethod` TEXT NOT NULL,
                        `isCriticalSecurity` INTEGER NOT NULL,
                        `isInstalled` INTEGER NOT NULL,
                        `verificationStatus` TEXT NOT NULL,
                        `currentStage` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `update_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `deviceId` TEXT NOT NULL,
                        `deviceName` TEXT NOT NULL,
                        `fromVersion` TEXT NOT NULL,
                        `toVersion` TEXT NOT NULL,
                        `channel` TEXT NOT NULL,
                        `stageReached` TEXT NOT NULL,
                        `resultStatus` TEXT NOT NULL,
                        `errorDetail` TEXT NOT NULL,
                        `timestampEpochMs` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `vulnerabilities` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `cveId` TEXT NOT NULL,
                        `affectedComponent` TEXT NOT NULL,
                        `affectedVersion` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `severity` TEXT NOT NULL,
                        `cvssScore` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `publishedDate` TEXT NOT NULL,
                        `fixPackageId` TEXT NOT NULL,
                        `recommendedAction` TEXT NOT NULL,
                        `status` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `security_audits` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `deviceId` TEXT NOT NULL,
                        `deviceName` TEXT NOT NULL,
                        `checkName` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `resultStatus` TEXT NOT NULL,
                        `detail` TEXT NOT NULL,
                        `checkedEpochMs` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `security_events` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `deviceId` TEXT NOT NULL,
                        `severity` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `detail` TEXT NOT NULL,
                        `actionTaken` TEXT NOT NULL,
                        `timestampEpochMs` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recovery_points` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `deviceId` TEXT NOT NULL,
                        `deviceName` TEXT NOT NULL,
                        `hardwareVersion` TEXT NOT NULL,
                        `firmwareVersion` TEXT NOT NULL,
                        `configSnapshotJson` TEXT NOT NULL,
                        `backupSha256` TEXT NOT NULL,
                        `isRollbackSupported` INTEGER NOT NULL,
                        `rollbackNote` TEXT NOT NULL,
                        `createdEpochMs` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `update_sources` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `name` TEXT NOT NULL,
                        `sourceType` TEXT NOT NULL,
                        `endpointUrl` TEXT NOT NULL,
                        `isEnabled` INTEGER NOT NULL,
                        `isTrustedByDefault` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): CyberDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CyberDatabase::class.java,
                    "cyber_tv_box_v9.db"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
