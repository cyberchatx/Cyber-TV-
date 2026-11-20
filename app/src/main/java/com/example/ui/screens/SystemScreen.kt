package com.example.ui.screens

import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Hardware
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.AdBlockExceptionEntity
import com.example.data.CyberSeedData
import com.example.data.CyberSettingsState
import com.example.hardware.NetworkTelemetry
import com.example.hardware.StorageTelemetry
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCoral
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMint
import com.example.ui.theme.ObsidianVoid
import kotlinx.coroutines.launch

@Composable
fun SystemScreen(
    settings: CyberSettingsState,
    network: NetworkTelemetry,
    storage: StorageTelemetry,
    adBlockExceptions: List<AdBlockExceptionEntity>,
    onSetDarkTheme: (Boolean) -> Unit,
    onSetAdBlock: (Boolean) -> Unit,
    onSetDnsTracking: (Boolean) -> Unit,
    onAddAdBlockException: (String, String) -> Unit,
    onRemoveAdBlockException: (String) -> Unit,
    onSetCamLock: (Boolean) -> Unit,
    onSetMicMute: (Boolean) -> Unit,
    onSetOfflineForced: (Boolean) -> Unit,
    onEnableKidsMode: (String) -> Unit,
    onUnlockKidsMode: (String) -> Boolean,
    onSetHighContrast: (Boolean) -> Unit,
    onSetLargeText: (Boolean) -> Unit,
    onSetReducedMotion: (Boolean) -> Unit,
    onSetPersonalization: (Boolean) -> Unit,
    onWipeLocalPreferences: () -> Unit,
    onBuildBackupJson: suspend () -> String,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val json = onBuildBackupJson()
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray())
                    }
                    onShowMessage("Sicheres JSON-Backup exportiert (ohne Geheimnisse/PINs).")
                } catch (e: Exception) {
                    onShowMessage("Backup-Fehler: ${e.localizedMessage}")
                }
            }
        }
    }

    val tabs = listOf(
        "Netzwerk & APNs",
        "Ad Block",
        "Speicher & Backup",
        "Cam & Audio",
        "Kids & Privacy",
        "Hardware & Markt"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CyberCyan
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    modifier = Modifier.testTag("system_tab_$idx"),
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Network Intelligence & Swiss APNs (TEIL 29 & TEIL 30)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard(highlightBorder = true) {
                            SectionHeader(
                                title = "CYBER NETWORK INTELLIGENCE",
                                subtitle = "Live-Android-Konnektivität & Offline-Steuerung",
                                icon = Icons.Rounded.Wifi,
                                trailing = {
                                    StatusBadge(
                                        label = if (network.isOnline && !settings.offlineModeForced) "ONLINE" else "OFFLINE",
                                        statusType = if (network.isOnline && !settings.offlineModeForced) "ONLINE" else "STANDBY"
                                    )
                                }
                            )
                            Spacer(Modifier.height(6.dp))
                            Text("Transport: ${network.transportLabel} · Lokale IPv4: ${network.localIpv4}", style = MaterialTheme.typography.bodySmall)
                            Text("Getaktete Verbindung: ${if (network.isMetered) "Ja" else "Nein"}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("CYBER Offline Mode erzwingen", style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = settings.offlineModeForced,
                                    onCheckedChange = onSetOfflineForced,
                                    modifier = Modifier.testTag("offline_mode_switch")
                                )
                            }
                        }
                    }

                    item {
                        SectionHeader(
                            title = "Schweizer Mobilfunk-APNs (A7670E-4G)",
                            subtitle = "Keine universelle automatische SIM-Konfiguration versprechen; APN gemäss Anbieter prüfen"
                        )
                    }

                    items(CyberSeedData.swissApnProfiles, key = { it.carrier }) { apn ->
                        CyberHoloCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(apn.carrier, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                                Text("APN: ${apn.apn}", style = MaterialTheme.typography.labelMedium, color = CyberMint)
                            }
                            Text("Auth: ${apn.authType}", style = MaterialTheme.typography.labelSmall)
                            Text(apn.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            1 -> {
                // Ad Block & Exceptions (TEIL 16)
                var newDomain by remember { mutableStateOf("") }
                var newReason by remember { mutableStateOf("") }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard(highlightBorder = settings.adBlockEnabled) {
                            SectionHeader(
                                title = "CYBER AD BLOCK",
                                subtitle = "Ein-/Ausschaltbarer DNS- & Tracking-Schutz",
                                icon = Icons.Rounded.Shield
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("AD BLOCK EIN / AUS")
                                Switch(
                                    checked = settings.adBlockEnabled,
                                    onCheckedChange = onSetAdBlock,
                                    modifier = Modifier.testTag("adblock_switch")
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tracking-Filter (DNS)")
                                Switch(
                                    checked = settings.dnsTrackingFilterEnabled,
                                    onCheckedChange = onSetDnsTracking
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Grenzen (TEIL 16): DNS-Filter blockieren keine direkte In-Stream-Werbung, keine Paywalls und umgehen kein DRM.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberAmber
                            )
                        }
                    }

                    item {
                        CyberHoloCard {
                            Text("Plattform-Ausnahme hinzufügen", style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newDomain,
                                onValueChange = { newDomain = it },
                                label = { Text("Domain (z.B. srf.ch)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newReason,
                                onValueChange = { newReason = it },
                                label = { Text("Grund") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onAddAdBlockException(newDomain, newReason)
                                    newDomain = ""
                                    newReason = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Ausnahme speichern")
                            }
                        }
                    }

                    items(adBlockExceptions, key = { it.domain }) { exc ->
                        CyberHoloCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(exc.domain, style = MaterialTheme.typography.titleSmall)
                                    Text(exc.reason, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { onRemoveAdBlockException(exc.domain) }) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Ausnahme entfernen", tint = CyberCoral)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Storage & Backup (TEIL 31 & TEIL 36)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "CYBER STORAGE · LOKAL & TF",
                                subtitle = "Echte Gerätespeicherwerte via Android StatFs + getrennte TF-Struktur",
                                icon = Icons.Rounded.SdStorage
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("App/Gerät Gesamt: ${Formatter.formatShortFileSize(context, storage.totalBytes)}", style = MaterialTheme.typography.bodyMedium)
                            Text("Verfügbar: ${Formatter.formatShortFileSize(context, storage.freeBytes)}", style = MaterialTheme.typography.bodyMedium, color = CyberMint)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Getrennte physische TF-Speicher: BOX-TF · DISPLAY-TF · CAM-TF. Das 2-GB-Ziel bleibt Projektanforderung; reale TF-Grösse wird je Karte am Gerät ausgelesen.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "CYBER BACKUP CENTER",
                                subtitle = "Sichert Geräte, Favoriten, Senderlisten & Ausnahmen ohne Passwörter oder SIM-PINs",
                                icon = Icons.Rounded.Backup
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { backupLauncher.launch("cyber_tv_box_v9_backup.json") },
                                modifier = Modifier.testTag("export_backup_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                            ) {
                                Text("Sicheres JSON-Backup exportieren")
                            }
                        }
                    }
                }
            }

            3 -> {
                // Cam, Audio & Video Call Privacy (TEIL 21, 22, 23)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard(highlightBorder = true) {
                            SectionHeader(
                                title = "CYBER CAM · OV5640 & SICHTBARE PRIVACY",
                                subtitle = "Unabhängige lokale TF-Aufnahme; Fernzugriff standardmässig deaktiviert",
                                icon = Icons.Rounded.Cameraswitch
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Kamera-Sperre (Privacy Lock)")
                                Switch(checked = settings.camPrivacyLock, onCheckedChange = onSetCamLock)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Mikrofon Stummschaltung")
                                Switch(checked = settings.micPrivacyMute, onCheckedChange = onSetMicMute)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "TF-Ordner: /CYBER_CAM/PHOTOS/ · /VIDEOS/ · /AUDIO/ · /EXPORT/ · /LOGS/",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan
                            )
                        }
                    }

                    item {
                        CyberHoloCard {
                            Text("CYBER AUDIO CENTER · Hardware-Trennung", style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Spacer(Modifier.height(6.dp))
                            Text("• CAM-Aufnahme: Dual-Mikrofon-Array + ES7210", style = MaterialTheme.typography.bodySmall)
                            Text("• CAM-Wiedergabe: ES8311 + NS4150B Verstärker (max. 3 W @ 4 Ω gem. Datenblatt, KEINE 5-W-Behauptung!)", style = MaterialTheme.typography.bodySmall)
                            Text("• A7670E Modem-Audio ist getrennt vom ESP32-I2S-Audio.", style = MaterialTheme.typography.bodySmall, color = CyberAmber)
                            Text("• TV-Client-Audio wird auf dem jeweiligen TV 1–6 gesteuert.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            4 -> {
                // Kids Mode, Accessibility & Privacy Wipe (TEIL 26 & TEIL 28)
                var pinInput by remember { mutableStateOf("") }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "CYBER KIDS MODE (PIN-Schutz)",
                                subtitle = "Blendet kostenpflichtige/DRM-Abos im Streaming-Center aus",
                                icon = Icons.Rounded.ChildCare,
                                trailing = {
                                    StatusBadge(
                                        label = if (settings.kidsModeEnabled) "GESCHÜTZT" else "AUS",
                                        statusType = if (settings.kidsModeEnabled) "STANDBY" else "ONLINE"
                                    )
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = { if (it.length <= 6) pinInput = it },
                                label = { Text("PIN (4–6 Ziffern, SHA-256 gehasht)") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!settings.kidsModeEnabled) {
                                    Button(
                                        onClick = {
                                            if (pinInput.length >= 4) {
                                                onEnableKidsMode(pinInput)
                                                pinInput = ""
                                            } else {
                                                onShowMessage("Bitte mindestens 4 Zeichen als PIN eingeben.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                                    ) {
                                        Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Kids Mode aktivieren")
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (onUnlockKidsMode(pinInput)) pinInput = ""
                                        }
                                    ) {
                                        Text("Mit PIN entsperren")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "DESIGN & BARRIEREFREIHEIT",
                                subtitle = "Dark/Light Design, hoher Kontrast, grosse Schrift & reduzierte Animationen",
                                icon = Icons.Rounded.AccessibilityNew
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dark Cyber Holo Design (Aus = Light)")
                                Switch(
                                    checked = settings.darkThemeEnabled,
                                    onCheckedChange = onSetDarkTheme,
                                    modifier = Modifier.testTag("dark_theme_switch")
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hoher Kontrast")
                                Switch(checked = settings.highContrastEnabled, onCheckedChange = onSetHighContrast)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Grosse Schrift")
                                Switch(checked = settings.largeTextEnabled, onCheckedChange = onSetLargeText)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Reduzierte Animationen")
                                Switch(checked = settings.reducedMotionEnabled, onCheckedChange = onSetReducedMotion)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Lokale Personalisierung")
                                Switch(checked = settings.personalizationEnabled, onCheckedChange = onSetPersonalization)
                            }
                        }
                    }

                    item {
                        CyberHoloCard {
                            Text("CYBER PRIVACY CENTER · Datenlöschung", style = MaterialTheme.typography.titleSmall, color = CyberCoral)
                            Spacer(Modifier.height(6.dp))
                            OutlinedButton(onClick = onWipeLocalPreferences) {
                                Text("Lokale Einstellungen zurücksetzen")
                            }
                        }
                    }
                }
            }

            5 -> {
                // Hardware Compatibility Matrix (TEIL 3/48) & Competitor Matrix (TEIL 45)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SectionHeader(
                            title = "Hardware-Kompatibilitätsmatrix (TEIL 3 & 48)",
                            subtitle = "Verbindliche Herstellerdaten ohne erfundene GPIO/Leistungswerte",
                            icon = Icons.Rounded.Hardware
                        )
                    }
                    items(CyberSeedData.hardwareCompatibilityMatrix, key = { it.component }) { row ->
                        CyberHoloCard {
                            Text(row.component, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Text(row.boardName, style = MaterialTheme.typography.bodyMedium)
                            Text(row.mcuAndMemory, style = MaterialTheme.typography.labelSmall, color = CyberMint)
                            Text("Schnittstellen: ${row.interfaces}", style = MaterialTheme.typography.bodySmall)
                            Text("Grenzen: ${row.mandatoryBoundaries}", style = MaterialTheme.typography.bodySmall, color = CyberAmber)
                        }
                    }

                    item {
                        SectionHeader(
                            title = "Konkurrenzvergleich (TEIL 45)",
                            subtitle = "Ehrlicher Status vs. Swisscom blue TV, Sunrise/Salt, Zattoo/Teleboy, Apple/Google/Fire TV",
                            icon = Icons.Rounded.CompareArrows
                        )
                    }
                    items(CyberSeedData.competitorMatrix, key = { it.feature }) { c ->
                        CyberHoloCard {
                            Text(c.feature, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Text("Telcos (blue TV/Sunrise/Salt): ${c.swisscomSunriseSalt}", style = MaterialTheme.typography.bodySmall)
                            Text("OTT (Zattoo/Teleboy): ${c.zattooTeleboy} · Boxen (Apple/Google/Fire): ${c.appleGoogleFire}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            Text("CYBER: ${c.cyberStatus}", style = MaterialTheme.typography.labelMedium, color = CyberMint)
                            Text("Grenze: ${c.cyberLimit}", style = MaterialTheme.typography.labelSmall, color = CyberAmber)
                        }
                    }
                }
            }
        }
    }
}
