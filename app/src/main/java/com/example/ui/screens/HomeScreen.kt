package com.example.ui.screens

import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SettingsRemote
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CyberSettingsState
import com.example.data.LiveTvChannelEntity
import com.example.data.ManagedDeviceEntity
import com.example.data.StreamingPlatformEntity
import com.example.data.UpdatePackageEntity
import com.example.data.VulnerabilityEntity
import com.example.hardware.NetworkTelemetry
import com.example.hardware.StorageTelemetry
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TouchRemotePad
import com.example.ui.components.TvClientSelectorStrip
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCoral
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMint
import com.example.ui.theme.ObsidianVoid
import com.example.viewmodel.CyberNotification
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val allWidgetDefinitions = listOf(
    "CLOCK" to "Uhrzeit & Datum",
    "UPDATE_SECURITY" to "Update & Security Center",
    "LIVE_TV" to "Live-TV Vorschau",
    "STREAMING" to "Streaming-Favoriten",
    "MULTI_TV" to "Verbundene TV 1–6",
    "CAM_PRIVACY" to "Kamera & Sicherheit",
    "NETWORK" to "Netzwerkstatus",
    "STORAGE" to "Speicherstatus",
    "DIAGNOSTICS" to "Systemdiagnose & Meldungen"
)

@Composable
fun HomeScreen(
    settings: CyberSettingsState,
    channels: List<LiveTvChannelEntity>,
    platforms: List<StreamingPlatformEntity>,
    devices: List<ManagedDeviceEntity>,
    updatePackages: List<UpdatePackageEntity>,
    vulnerabilities: List<VulnerabilityEntity>,
    network: NetworkTelemetry,
    storage: StorageTelemetry,
    notifications: List<CyberNotification>,
    usbCount: Int,
    onToggleDisplayMode: () -> Unit,
    onSelectTv: (String) -> Unit,
    onSendRemoteAction: (String) -> Unit,
    onToggleWidget: (String) -> Unit,
    onNavigateToMedia: () -> Unit,
    onNavigateToUpdates: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToFlash: () -> Unit,
    onNavigateToAi: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showWidgetConfig by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(30_000L)
        }
    }

    val timeStr = remember(nowMillis) {
        SimpleDateFormat("HH:mm", Locale.GERMAN).format(Date(nowMillis))
    }
    val dateStr = remember(nowMillis) {
        SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMAN).format(Date(nowMillis))
    }

    val visibleWidgets = remember(settings.visibleWidgetsCsv) {
        settings.visibleWidgetsCsv.split(",").filter { it.isNotBlank() }.toSet()
    }
    val tvClients = remember(devices) { devices.filter { it.deviceRole == "TV_CLIENT" } }
    val activeTv = remember(tvClients, settings.activeTvId) {
        tvClients.firstOrNull { it.id == settings.activeTvId } ?: tvClients.firstOrNull()
    }
    val effectiveOnline = network.isOnline && !settings.offlineModeForced
    val openUpdates = remember(updatePackages) { updatePackages.count { !it.isInstalled } }
    val criticalSecurity = remember(vulnerabilities) { vulnerabilities.count { it.status == "BESTAETIGT" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner with Cyber Holo Aesthetic
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cyber_holo_hero),
                    contentDescription = stringResource(R.string.cd_hero_banner),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ObsidianVoid.copy(alpha = 0.25f),
                                    ObsidianVoid.copy(alpha = 0.92f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(
                                label = if (effectiveOnline) "${network.transportLabel} AKTIV" else "OFFLINE MODE",
                                statusType = if (effectiveOnline) "ONLINE" else "STANDBY"
                            )
                            Spacer(Modifier.width(8.dp))
                            StatusBadge(
                                label = if (settings.displayMode == "TV_MODE") "TV MODE" else "REMOTE MODE",
                                statusType = "CYBER"
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "CYBER FLASH · V9.0",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.cyber_tagline),
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan
                        )
                    }
                }
            }
        }

        // Mode Switcher & Customize Widgets Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggleDisplayMode,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("toggle_display_mode_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (settings.displayMode == "TV_MODE") Icons.Rounded.SettingsRemote else Icons.Rounded.Tv,
                        contentDescription = stringResource(R.string.cd_toggle_mode),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (settings.displayMode == "TV_MODE") "Zu REMOTE MODE" else "Zu TV MODE",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                FilledTonalButton(
                    onClick = onNavigateToAi,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("home_open_ai_button")
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cyber AI")
                }
                OutlinedButton(
                    onClick = { showWidgetConfig = true },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("customize_widgets_button"),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Rounded.DashboardCustomize, contentDescription = "Widgets anpassen", modifier = Modifier.size(18.dp))
                }
            }
        }

        // Update Center & Security Center Quick Cards (always visible unless unchecked)
        if (visibleWidgets.contains("UPDATE_SECURITY") || !visibleWidgets.contains("HIDE_UPD_SEC")) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberHoloCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_update_center_card"),
                        highlightBorder = openUpdates > 0,
                        onClick = onNavigateToUpdates
                    ) {
                        Text("Update Center", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        StatusBadge(
                            label = "$openUpdates offen (${settings.updateChannel})",
                            statusType = if (openUpdates > 0) "UPDATE_VERFUEGBAR" else "GESCHUETZT"
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Updates suchen & prüfen →", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    CyberHoloCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_security_center_card"),
                        highlightBorder = criticalSecurity > 0,
                        onClick = onNavigateToSecurity
                    ) {
                        Text("Security Center", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        StatusBadge(
                            label = if (criticalSecurity > 0) "$criticalSecurity Befunde" else "Prüfen",
                            statusType = if (criticalSecurity > 0) "KRITISCH" else "PRUEFUNG_ERFORDERLICH"
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Sicherheitsprüfung öffnen →", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // If REMOTE_MODE is active, bring Touch Remote Pad directly to the top!
        if (settings.displayMode == "REMOTE_MODE") {
            item {
                TvClientSelectorStrip(
                    tvClients = tvClients,
                    activeTvId = settings.activeTvId,
                    onSelectTv = onSelectTv
                )
            }
            item {
                TouchRemotePad(
                    targetDevice = activeTv,
                    onSendCommand = onSendRemoteAction
                )
            }
        }

        // Widget: Clock & Quick Overview
        if (visibleWidgets.contains("CLOCK")) {
            item {
                CyberHoloCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = timeStr,
                                style = MaterialTheme.typography.displaySmall,
                                color = CyberCyan
                            )
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = activeTv?.name ?: "TV 1",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (settings.kidsModeEnabled) "Kids Mode: AKTIV" else "Standard-Profil",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (settings.kidsModeEnabled) CyberAmber else CyberMint
                            )
                        }
                    }
                }
            }
        }

        // Widget: Streaming Favorites (incl. CYBER STREAM FREE)
        if (visibleWidgets.contains("STREAMING")) {
            item {
                SectionHeader(
                    title = "Streaming-Favoriten",
                    subtitle = "Start über kompatiblen TV Client oder Browser",
                    icon = Icons.Rounded.PlayCircle,
                    trailing = {
                        TextButton(onClick = onNavigateToMedia) {
                            Text("Alle (${platforms.size})")
                        }
                    }
                )
                Spacer(Modifier.height(6.dp))
                val favPlatforms = platforms.filter { it.isFavorite }.ifEmpty { platforms.take(4) }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(favPlatforms, key = { it.id }) { p ->
                        CyberHoloCard(
                            modifier = Modifier
                                .width(205.dp)
                                .testTag("home_platform_${p.id}"),
                            highlightBorder = p.id == "cyber_stream_free",
                            onClick = {
                                if (effectiveOnline) {
                                    openExternalUrl(context, p.officialUrl, onShowMessage)
                                } else {
                                    onShowMessage("Offline-Modus: Internetplattform ${p.name} derzeit nicht erreichbar.")
                                }
                            }
                        ) {
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = CyberCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${p.category} · ${if (p.isFree) "Gratis" else "Abo/Store"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Offiziell öffnen →",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (effectiveOnline) CyberMint else CyberAmber
                            )
                        }
                    }
                }
            }
        }

        // Widget: Live-TV Vorschau
        if (visibleWidgets.contains("LIVE_TV")) {
            item {
                SectionHeader(
                    title = "Live-TV Vorschau (Legal CH/EU)",
                    subtitle = "Offizielle Senderportale ohne illegale IPTV-Listen",
                    icon = Icons.Rounded.LiveTv,
                    trailing = {
                        TextButton(onClick = onNavigateToMedia) {
                            Text("Alle (${channels.size})")
                        }
                    }
                )
                Spacer(Modifier.height(6.dp))
                val favChannels = channels.filter { it.isFavorite }.ifEmpty { channels.take(4) }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(favChannels, key = { it.id }) { ch ->
                        CyberHoloCard(
                            modifier = Modifier
                                .width(220.dp)
                                .testTag("home_channel_${ch.id}"),
                            onClick = {
                                if (effectiveOnline) {
                                    openExternalUrl(context, ch.officialUrl, onShowMessage)
                                } else {
                                    onShowMessage("Offline: Live-TV benötigt Internet.")
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(ch.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                                Text(ch.country, style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Jetzt: ${ch.currentProgramTitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = ch.geoblockingNote,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberAmber,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Widget: Multi-TV 1-6 Status
        if (visibleWidgets.contains("MULTI_TV")) {
            item {
                CyberHoloCard {
                    SectionHeader(
                        title = "Verbundene TV-Clients (TV 1–6)",
                        subtitle = "${tvClients.count { it.status != "OFFLINE" }} von ${tvClients.size} erreichbar",
                        icon = Icons.Rounded.Tv
                    )
                    Spacer(Modifier.height(8.dp))
                    TvClientSelectorStrip(
                        tvClients = tvClients,
                        activeTvId = settings.activeTvId,
                        onSelectTv = onSelectTv
                    )
                }
            }
        }

        // Widget: Network & Storage & Cam Privacy pulse
        if (visibleWidgets.contains("NETWORK") || visibleWidgets.contains("STORAGE") || visibleWidgets.contains("CAM_PRIVACY")) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (visibleWidgets.contains("NETWORK")) {
                        CyberHoloCard(modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.Wifi, contentDescription = null, tint = if (effectiveOnline) CyberMint else CyberAmber)
                            Spacer(Modifier.height(6.dp))
                            Text("Netzwerk", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = if (effectiveOnline) "${network.transportLabel}\n${network.localIpv4}" else "Offline Mode aktiv",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (visibleWidgets.contains("STORAGE")) {
                        CyberHoloCard(modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.SdStorage, contentDescription = null, tint = CyberCyan)
                            Spacer(Modifier.height(6.dp))
                            Text("Speicher", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Frei: ${Formatter.formatShortFileSize(context, storage.freeBytes)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (visibleWidgets.contains("CAM_PRIVACY")) {
            item {
                CyberHoloCard {
                    SectionHeader(
                        title = "CYBER CAM & Privatsphäre",
                        subtitle = "OV5640 TF-Aufnahme unabhängig von BOX & WLAN",
                        icon = Icons.Rounded.Cameraswitch
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusBadge(
                            label = if (settings.camPrivacyLock) "CAM GESPERRT" else "CAM FREIGEGEBEN",
                            statusType = if (settings.camPrivacyLock) "ONLINE" else "ALERT"
                        )
                        StatusBadge(
                            label = if (settings.micPrivacyMute) "MIC STUMM" else "MIC AKTIV",
                            statusType = if (settings.micPrivacyMute) "ONLINE" else "ALERT"
                        )
                        StatusBadge(
                            label = if (settings.adBlockEnabled) "ADBLOCK EIN" else "ADBLOCK AUS",
                            statusType = if (settings.adBlockEnabled) "ONLINE" else "STANDBY"
                        )
                    }
                }
            }
        }

        // Widget: Diagnostics & Notification Center
        if (visibleWidgets.contains("DIAGNOSTICS")) {
            item {
                CyberHoloCard {
                    SectionHeader(
                        title = "Systemdiagnose & Flash-Status",
                        subtitle = "USB-OTG Brücken erkannt: $usbCount · Meldungen: ${notifications.size}",
                        icon = Icons.Rounded.Speed,
                        trailing = {
                            AssistChip(
                                onClick = onNavigateToFlash,
                                label = { Text("Flash Center") },
                                leadingIcon = { Icon(Icons.Rounded.Memory, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    notifications.forEach { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Security,
                                contentDescription = null,
                                tint = when (note.severity) {
                                    "ALERT" -> CyberCoral
                                    "WARN" -> CyberAmber
                                    else -> CyberCyan
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(note.title, style = MaterialTheme.typography.labelLarge)
                                Text(
                                    note.detail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showWidgetConfig) {
        AlertDialog(
            onDismissRequest = { showWidgetConfig = false },
            title = { Text("CYBER HOME Widgets") },
            text = {
                Column {
                    allWidgetDefinitions.forEach { (key, label) ->
                        val checked = visibleWidgets.contains(key)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onToggleWidget(key) }
                            )
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWidgetConfig = false }) {
                    Text("Fertig")
                }
            }
        )
    }
}
