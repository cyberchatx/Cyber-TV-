package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.cyberDataStore by preferencesDataStore(name = "cyber_preferences")

data class CyberSettingsState(
    val displayMode: String = "TV_MODE", // TV_MODE or REMOTE_MODE
    val activeTvId: String = "tv_1",
    val darkThemeEnabled: Boolean = true,
    val adBlockEnabled: Boolean = true,
    val dnsTrackingFilterEnabled: Boolean = true,
    val kidsModeEnabled: Boolean = false,
    val kidsPinHash: String = "",
    val highContrastEnabled: Boolean = false,
    val largeTextEnabled: Boolean = false,
    val reducedMotionEnabled: Boolean = false,
    val aiConsentAccepted: Boolean = false,
    val personalizationEnabled: Boolean = true,
    val camPrivacyLock: Boolean = true,
    val micPrivacyMute: Boolean = true,
    val offlineModeForced: Boolean = false,
    val visibleWidgetsCsv: String = "CLOCK,LIVE_TV,STREAMING,MULTI_TV,CAM_PRIVACY,NETWORK,STORAGE,DIAGNOSTICS",
    // Update Center Settings (Section 6.6 & 6.7)
    val updateChannel: String = "STABLE", // STABLE, BETA, DEVELOPMENT
    val betaChannelApproved: Boolean = false,
    val devChannelApproved: Boolean = false,
    val autoUpdateCheck: Boolean = true,
    val notifyOnUpdates: Boolean = true,
    val wifiOnlyDownloads: Boolean = true,
    val allowCellularDownloads: Boolean = false,
    val askBeforeInstall: Boolean = true,
    val updateWindowLabel: String = "02:00 - 05:00"
)

class CyberPreferences(private val context: Context) {
    private object Keys {
        val DISPLAY_MODE = stringPreferencesKey("display_mode")
        val ACTIVE_TV_ID = stringPreferencesKey("active_tv_id")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val ADBLOCK_ENABLED = booleanPreferencesKey("adblock_enabled")
        val DNS_TRACKING_FILTER = booleanPreferencesKey("dns_tracking_filter")
        val KIDS_MODE = booleanPreferencesKey("kids_mode")
        val KIDS_PIN_HASH = stringPreferencesKey("kids_pin_hash")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val AI_CONSENT = booleanPreferencesKey("ai_consent")
        val PERSONALIZATION = booleanPreferencesKey("personalization")
        val CAM_PRIVACY_LOCK = booleanPreferencesKey("cam_privacy_lock")
        val MIC_PRIVACY_MUTE = booleanPreferencesKey("mic_privacy_mute")
        val OFFLINE_MODE_FORCED = booleanPreferencesKey("offline_mode_forced")
        val VISIBLE_WIDGETS = stringPreferencesKey("visible_widgets")
        val UPDATE_CHANNEL = stringPreferencesKey("update_channel")
        val BETA_APPROVED = booleanPreferencesKey("beta_approved")
        val DEV_APPROVED = booleanPreferencesKey("dev_approved")
        val AUTO_UPDATE_CHECK = booleanPreferencesKey("auto_update_check")
        val NOTIFY_ON_UPDATES = booleanPreferencesKey("notify_on_updates")
        val WIFI_ONLY_DOWNLOADS = booleanPreferencesKey("wifi_only_downloads")
        val ALLOW_CELLULAR_DOWNLOADS = booleanPreferencesKey("allow_cellular_downloads")
        val ASK_BEFORE_INSTALL = booleanPreferencesKey("ask_before_install")
        val UPDATE_WINDOW = stringPreferencesKey("update_window")
    }

    val settingsFlow: Flow<CyberSettingsState> = context.cyberDataStore.data.map { prefs ->
        CyberSettingsState(
            displayMode = prefs[Keys.DISPLAY_MODE] ?: "TV_MODE",
            activeTvId = prefs[Keys.ACTIVE_TV_ID] ?: "tv_1",
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: true,
            adBlockEnabled = prefs[Keys.ADBLOCK_ENABLED] ?: true,
            dnsTrackingFilterEnabled = prefs[Keys.DNS_TRACKING_FILTER] ?: true,
            kidsModeEnabled = prefs[Keys.KIDS_MODE] ?: false,
            kidsPinHash = prefs[Keys.KIDS_PIN_HASH] ?: "",
            highContrastEnabled = prefs[Keys.HIGH_CONTRAST] ?: false,
            largeTextEnabled = prefs[Keys.LARGE_TEXT] ?: false,
            reducedMotionEnabled = prefs[Keys.REDUCED_MOTION] ?: false,
            aiConsentAccepted = prefs[Keys.AI_CONSENT] ?: false,
            personalizationEnabled = prefs[Keys.PERSONALIZATION] ?: true,
            camPrivacyLock = prefs[Keys.CAM_PRIVACY_LOCK] ?: true,
            micPrivacyMute = prefs[Keys.MIC_PRIVACY_MUTE] ?: true,
            offlineModeForced = prefs[Keys.OFFLINE_MODE_FORCED] ?: false,
            visibleWidgetsCsv = prefs[Keys.VISIBLE_WIDGETS]
                ?: "CLOCK,LIVE_TV,STREAMING,MULTI_TV,CAM_PRIVACY,NETWORK,STORAGE,DIAGNOSTICS",
            updateChannel = prefs[Keys.UPDATE_CHANNEL] ?: "STABLE",
            betaChannelApproved = prefs[Keys.BETA_APPROVED] ?: false,
            devChannelApproved = prefs[Keys.DEV_APPROVED] ?: false,
            autoUpdateCheck = prefs[Keys.AUTO_UPDATE_CHECK] ?: true,
            notifyOnUpdates = prefs[Keys.NOTIFY_ON_UPDATES] ?: true,
            wifiOnlyDownloads = prefs[Keys.WIFI_ONLY_DOWNLOADS] ?: true,
            allowCellularDownloads = prefs[Keys.ALLOW_CELLULAR_DOWNLOADS] ?: false,
            askBeforeInstall = prefs[Keys.ASK_BEFORE_INSTALL] ?: true,
            updateWindowLabel = prefs[Keys.UPDATE_WINDOW] ?: "02:00 - 05:00"
        )
    }

    suspend fun setDisplayMode(mode: String) {
        context.cyberDataStore.edit { it[Keys.DISPLAY_MODE] = mode }
    }

    suspend fun setActiveTvId(tvId: String) {
        context.cyberDataStore.edit { it[Keys.ACTIVE_TV_ID] = tvId }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setAdBlock(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.ADBLOCK_ENABLED] = enabled }
    }

    suspend fun setDnsTrackingFilter(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.DNS_TRACKING_FILTER] = enabled }
    }

    suspend fun setKidsMode(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.KIDS_MODE] = enabled }
    }

    suspend fun setKidsPin(pin: String) {
        val hash = sha256(pin)
        context.cyberDataStore.edit { it[Keys.KIDS_PIN_HASH] = hash }
    }

    fun verifyPin(pin: String, storedHash: String): Boolean {
        if (storedHash.isBlank()) return true
        return sha256(pin) == storedHash
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
    }

    suspend fun setLargeText(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.LARGE_TEXT] = enabled }
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.REDUCED_MOTION] = enabled }
    }

    suspend fun setAiConsent(accepted: Boolean) {
        context.cyberDataStore.edit { it[Keys.AI_CONSENT] = accepted }
    }

    suspend fun setPersonalization(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.PERSONALIZATION] = enabled }
    }

    suspend fun setCamPrivacyLock(locked: Boolean) {
        context.cyberDataStore.edit { it[Keys.CAM_PRIVACY_LOCK] = locked }
    }

    suspend fun setMicPrivacyMute(muted: Boolean) {
        context.cyberDataStore.edit { it[Keys.MIC_PRIVACY_MUTE] = muted }
    }

    suspend fun setOfflineModeForced(forced: Boolean) {
        context.cyberDataStore.edit { it[Keys.OFFLINE_MODE_FORCED] = forced }
    }

    suspend fun setVisibleWidgetsCsv(csv: String) {
        context.cyberDataStore.edit { it[Keys.VISIBLE_WIDGETS] = csv }
    }

    suspend fun setUpdateChannel(channel: String, betaApproved: Boolean? = null, devApproved: Boolean? = null) {
        context.cyberDataStore.edit { prefs ->
            prefs[Keys.UPDATE_CHANNEL] = channel
            if (betaApproved != null) prefs[Keys.BETA_APPROVED] = betaApproved
            if (devApproved != null) prefs[Keys.DEV_APPROVED] = devApproved
        }
    }

    suspend fun setAutoUpdateCheck(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.AUTO_UPDATE_CHECK] = enabled }
    }

    suspend fun setNotifyOnUpdates(enabled: Boolean) {
        context.cyberDataStore.edit { it[Keys.NOTIFY_ON_UPDATES] = enabled }
    }

    suspend fun setWifiOnlyDownloads(wifiOnly: Boolean) {
        context.cyberDataStore.edit { it[Keys.WIFI_ONLY_DOWNLOADS] = wifiOnly }
    }

    suspend fun setAllowCellularDownloads(allowed: Boolean) {
        context.cyberDataStore.edit { it[Keys.ALLOW_CELLULAR_DOWNLOADS] = allowed }
    }

    suspend fun setAskBeforeInstall(ask: Boolean) {
        context.cyberDataStore.edit { it[Keys.ASK_BEFORE_INSTALL] = ask }
    }

    suspend fun setUpdateWindow(window: String) {
        context.cyberDataStore.edit { it[Keys.UPDATE_WINDOW] = window }
    }

    suspend fun resetAllNonSensitive() {
        context.cyberDataStore.edit { it.clear() }
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
