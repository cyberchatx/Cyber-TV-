package com.example.ui.screens

import android.text.format.Formatter
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.SecurityUpdateGood
import androidx.compose.material.icons.rounded.SettingsApplications
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CyberSettingsState
import com.example.data.FlashLogEntity
import com.example.data.ManagedDeviceEntity
import com.example.data.RecoveryPointEntity
import com.example.data.TestMatrixItemEntity
import com.example.data.UpdateHistoryEntity
import com.example.data.UpdatePackageEntity
import com.example.data.UpdateSourceEntity
import com.example.hardware.FirmwareInspectionResult
import com.example.hardware.NetworkTelemetry
import com.example.hardware.UsbSerialCandidate
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.openExternalUrl
import com.example.viewmodel.UpdateInstallProgress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UpdateCenterScreen(
    settings: CyberSettingsState,
    devices: List<ManagedDeviceEntity>,
    packages: List<UpdatePackageEntity>,
    history: List<UpdateHistoryEntity>,
    sources: List<UpdateSourceEntity>,
    recoveryPoints: List<RecoveryPointEntity>,
    network: NetworkTelemetry,
    usbBridges: List<UsbSerialCandidate>,
    inspectedFirmware: FirmwareInspectionResult?,
    flashLogs: List<FlashLogEntity>,
    testMatrix: List<TestMatrixItemEntity>,
    isSearchingUpdates: Boolean,
    lastUpdateCheckEpochMs: Long,
    installProgress: UpdateInstallProgress?,
    startInFlashCenter: Boolean = false,
    onCheckForUpdates: () -> Unit,
    onInstallPackage: (UpdatePackageEntity, (String) -> Unit) -> Unit,
    onDismissInstallProgress: () -> Unit,
    onSetSourceEnabled: (String, Boolean) -> Unit,
    onSetChannel: (String, Boolean?, Boolean?) -> Unit,
    onSetAutoCheck: (Boolean) -> Unit,
    onSetNotifyUpdates: (Boolean) -> Unit,
    onSetWifiOnly: (Boolean) -> Unit,
    onSetAllowCellular: (Boolean) -> Unit,
    onSetAskBeforeInstall: (Boolean) -> Unit,
    onSetUpdateWindow: (String) -> Unit,
    onCreateRecoveryPoint: (ManagedDeviceEntity) -> Unit,
    onExecuteRollback: (RecoveryPointEntity) -> Unit,
    onRefreshUsb: () -> Unit,
    onInspectFirmwareUri: (android.net.Uri, String, String) -> Unit,
    onClearFlashLogs: () -> Unit,
    onUpdateTestMatrix: (TestMatrixItemEntity, String, String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var showFlashCenter by remember(startInFlashCenter) { mutableStateOf(startInFlashCenter) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("ALLE") }
    var pendingConfirmPackage by remember { mutableStateOf<UpdatePackageEntity?>(null) }
    var pendingChannelApproval by remember { mutableStateOf<String?>(null) }
    var pendingRollbackPoint by remember { mutableStateOf<RecoveryPointEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Mode Switcher: Update Center vs Flash Center & Testbericht
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SegmentedButton(
                selected = !showFlashCenter,
                onClick = { showFlashCenter = false },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                modifier = Modifier.testTag("mode_update_center")
            ) {
                Icon(Icons.Rounded.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Update Center")
            }
            SegmentedButton(
                selected = showFlashCenter,
                onClick = { showFlashCenter = true },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                modifier = Modifier.testTag("mode_flash_center")
            ) {
                Icon(Icons.Rounded.Memory, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Flash & Pro-Board")
            }
        }

        if (showFlashCenter) {
            FlashCenterScreen(
                usbBridges = usbBridges,
                inspectedFirmware = inspectedFirmware,
                flashLogs = flashLogs,
                testMatrix = testMatrix,
                onRefreshUsb = onRefreshUsb,
                onInspectFirmwareUri = onInspectFirmwareUri,
                onClearFlashLogs = onClearFlashLogs,
                onUpdateTestMatrix = onUpdateTestMatrix,
                onShowMessage = onShowMessage
            )
            return@Column
        }

        val tabs = listOf("Übersicht", "Pakete & Install", "Quellen & Regeln", "Verlauf & Recovery")
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    modifier = Modifier.testTag("update_tab_$idx"),
                    text = { Text(title, maxLines = 1) }
                )
            }
        }

        when (selectedTab) {
            0 -> UpdateOverviewTab(
                settings = settings,
                devices = devices,
                packages = packages,
                network = network,
                usbBridges = usbBridges,
                isSearching = isSearchingUpdates,
                lastCheckMs = lastUpdateCheckEpochMs,
                onCheckNow = onCheckForUpdates,
                onSelectChannel = { ch ->
                    if (ch == "STABLE") {
                        onSetChannel("STABLE", null, null)
                    } else if ((ch == "BETA" && !settings.betaChannelApproved) ||
                        (ch == "DEVELOPMENT" && !settings.devChannelApproved)
                    ) {
                        pendingChannelApproval = ch
                    } else {
                        onSetChannel(ch, null, null)
                    }
                },
                onJumpToPackages = { selectedTab = 1 }
            )

            1 -> UpdatePackagesTab(
                settings = settings,
                devices = devices,
                packages = packages,
                selectedFilter = selectedFilter,
                onSelectFilter = { selectedFilter = it },
                onRequestInstall = { pkg ->
                    if (settings.askBeforeInstall) {
                        pendingConfirmPackage = pkg
                    } else {
                        onInstallPackage(pkg) { url -> openExternalUrl(context, url, onShowMessage) }
                    }
                },
                onOpenFlashCenter = { showFlashCenter = true }
            )

            2 -> UpdateSourcesAndSettingsTab(
                settings = settings,
                sources = sources,
                onSetSourceEnabled = onSetSourceEnabled,
                onSetAutoCheck = onSetAutoCheck,
                onSetNotifyUpdates = onSetNotifyUpdates,
                onSetWifiOnly = onSetWifiOnly,
                onSetAllowCellular = onSetAllowCellular,
                onSetAskBeforeInstall = onSetAskBeforeInstall,
                onSetUpdateWindow = onSetUpdateWindow
            )

            3 -> UpdateHistoryAndRecoveryTab(
                devices = devices,
                history = history,
                recoveryPoints = recoveryPoints,
                onCreateRecoveryPoint = onCreateRecoveryPoint,
                onRequestRollback = { pendingRollbackPoint = it }
            )
        }
    }

    pendingChannelApproval?.let { ch ->
        AlertDialog(
            onDismissRequest = { pendingChannelApproval = null },
            title = { Text("$ch-Kanal ausdrücklich freigeben?") },
            text = {
                Text("Warnung: $ch-Pakete enthalten ungetestete Vorabänderungen. Standardmässig ist nur STABLE freigegeben. Möchtest du $ch explizit erlauben?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ch == "BETA") onSetChannel("BETA", true, null)
                        else onSetChannel("DEVELOPMENT", null, true)
                        pendingChannelApproval = null
                    },
                    modifier = Modifier.testTag("confirm_channel_button")
                ) { Text("$ch freigeben") }
            },
            dismissButton = {
                TextButton(onClick = { pendingChannelApproval = null }) { Text("Abbrechen") }
            }
        )
    }

    pendingConfirmPackage?.let { pkg ->
        val target = devices.firstOrNull { it.id == pkg.deviceId }
        AlertDialog(
            onDismissRequest = { pendingConfirmPackage = null },
            title = { Text("Update-Installation bestätigen") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Zielgerät: ${target?.name ?: pkg.deviceId}", fontWeight = FontWeight.Bold)
                    Text("Hardwareversion: ${pkg.hardwareVersion}")
                    Text("Version: ${target?.firmwareVersion ?: "?"} → ${pkg.firmwareVersion}")
                    Text("Grösse: ${if (pkg.packageSizeBytes > 0) Formatter.formatShortFileSize(context, pkg.packageSizeBytes) else "Dokumentiertes Tool"}")
                    Text("Quelle: ${pkg.sourceName}")
                    Text("SHA-256: ${pkg.sha256Checksum.take(24)}…", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                    Text("Vorab-Backup wird erstellt; CAM-Medien bleiben geschützt.", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingConfirmPackage = null
                        onInstallPackage(pkg) { url -> openExternalUrl(context, url, onShowMessage) }
                    },
                    modifier = Modifier.testTag("confirm_install_button")
                ) { Text("Jetzt installieren") }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmPackage = null }) { Text("Abbrechen") }
            }
        )
    }

    pendingRollbackPoint?.let { point ->
        AlertDialog(
            onDismissRequest = { pendingRollbackPoint = null },
            title = { Text("Recovery auf ${point.firmwareVersion}?") },
            text = {
                Text("Wiederherstellungspunkt für ${point.deviceName} (${point.hardwareVersion}) anwenden? ${point.rollbackNote}")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onExecuteRollback(point)
                        pendingRollbackPoint = null
                    },
                    modifier = Modifier.testTag("confirm_rollback_button")
                ) { Text("Wiederherstellen") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRollbackPoint = null }) { Text("Abbrechen") }
            }
        )
    }

    installProgress?.let { prog ->
        AlertDialog(
            onDismissRequest = { if (prog.isFinished) onDismissInstallProgress() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${prog.deviceName} · ${prog.version}", modifier = Modifier.weight(1f))
                    StatusBadge(label = prog.stage, statusType = prog.stage)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!prog.isFinished) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    Text(prog.detail, style = MaterialTheme.typography.bodyMedium)
                    prog.errorCode?.let {
                        Text("Fehlercode: $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }
                    prog.solutionHint?.let {
                        Text("Lösungshinweis: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                if (prog.isFinished) {
                    Button(onClick = onDismissInstallProgress, modifier = Modifier.testTag("close_install_progress")) {
                        Text(if (prog.isSuccess) "Fertig" else "Schliessen")
                    }
                }
            }
        )
    }
}

@Composable
private fun UpdateOverviewTab(
    settings: CyberSettingsState,
    devices: List<ManagedDeviceEntity>,
    packages: List<UpdatePackageEntity>,
    network: NetworkTelemetry,
    usbBridges: List<UsbSerialCandidate>,
    isSearching: Boolean,
    lastCheckMs: Long,
    onCheckNow: () -> Unit,
    onSelectChannel: (String) -> Unit,
    onJumpToPackages: () -> Unit
) {
    val openStable = packages.count { !it.isInstalled && it.channel == "STABLE" }
    val criticalOpen = packages.count { !it.isInstalled && it.isCriticalSecurity }
    val lastCheckText = remember(lastCheckMs) {
        if (lastCheckMs == 0L) "Noch nicht geprüft"
        else SimpleDateFormat("dd.MM. HH:mm:ss", Locale.GERMAN).format(Date(lastCheckMs))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CyberHoloCard(highlightBorder = true) {
                SectionHeader(
                    title = "CYBER UPDATE CENTER",
                    subtitle = "Zentrale Update-Steuerung · Letzte Prüfung: $lastCheckText",
                    icon = Icons.Rounded.SecurityUpdateGood
                )
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(label = "${devices.size} Geräte", statusType = "CYBER")
                    StatusBadge(label = "$openStable STABLE offen", statusType = if (openStable > 0) "UPDATE_VERFUEGBAR" else "GESCHUETZT")
                    if (criticalOpen > 0) {
                        StatusBadge(label = "$criticalOpen Kritisch", statusType = "KRITISCH")
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Netzwerk: ${if (network.isOnline && !settings.offlineModeForced) network.transportLabel else "Offline"} · USB-OTG Brücken: ${usbBridges.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onCheckNow,
                    enabled = !isSearching,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("check_updates_now_button")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (isSearching) "PRÜFE MANIFESTE..." else "JETZT NACH UPDATES SUCHEN")
                }
            }
        }

        item {
            CyberHoloCard {
                Text("Update-Kanal (Standard: STABLE)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("STABLE", "BETA", "DEVELOPMENT").forEach { ch ->
                        FilterChip(
                            selected = settings.updateChannel == ch,
                            onClick = { onSelectChannel(ch) },
                            modifier = Modifier.testTag("channel_chip_$ch"),
                            label = { Text(ch) }
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "BETA (${if (settings.betaChannelApproved) "freigegeben" else "gesperrt"}) · DEV (${if (settings.devChannelApproved) "freigegeben" else "gesperrt"})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            SectionHeader(
                title = "Geräte-Updatestatus",
                subtitle = "A7670E-Modem & ESP32 getrennt verwaltet · Externe TV-Clients geschützt",
                trailing = {
                    TextButton(onClick = onJumpToPackages) { Text("Zu Paketen") }
                }
            )
        }

        val coreDevices = devices.filter { it.deviceRole != "TV_CLIENT" && it.deviceRole != "CONTROLLER" }
        items(coreDevices, key = { it.id }) { dev ->
            val pending = packages.filter { it.deviceId == dev.id && !it.isInstalled }
            CyberHoloCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dev.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${dev.hardwareVersion} · Aktuell: ${dev.firmwareVersion}", style = MaterialTheme.typography.bodySmall)
                    }
                    StatusBadge(
                        label = if (pending.isNotEmpty()) "${pending.size} Update(s)" else "Aktuell",
                        statusType = if (pending.any { it.isCriticalSecurity }) "KRITISCH" else if (pending.isNotEmpty()) "UPDATE_VERFUEGBAR" else "GESCHUETZT"
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdatePackagesTab(
    settings: CyberSettingsState,
    devices: List<ManagedDeviceEntity>,
    packages: List<UpdatePackageEntity>,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    onRequestInstall: (UpdatePackageEntity) -> Unit,
    onOpenFlashCenter: () -> Unit
) {
    val context = LocalContext.current
    val filters = listOf("ALLE", "box_master", "cyber_display", "cyber_cam", "cyber_webhost", "cyber_flash_app")
    val filtered = remember(packages, selectedFilter) {
        if (selectedFilter == "ALLE") packages else packages.filter { it.deviceId == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    val label = when (f) {
                        "ALLE" -> "Alle (${packages.size})"
                        "box_master" -> "BOX MASTER"
                        "cyber_display" -> "DISPLAY"
                        "cyber_cam" -> "CAM"
                        "cyber_webhost" -> "WEBHOST"
                        else -> "Android-App"
                    }
                    FilterChip(
                        selected = selectedFilter == f,
                        onClick = { onSelectFilter(f) },
                        label = { Text(label) }
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { pkg ->
            val target = devices.firstOrNull { it.id == pkg.deviceId }
            val channelLocked = (pkg.channel == "BETA" && !settings.betaChannelApproved) ||
                (pkg.channel == "DEVELOPMENT" && !settings.devChannelApproved)
            CyberHoloCard(highlightBorder = pkg.isCriticalSecurity && !pkg.isInstalled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pkg.productId, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${target?.name ?: pkg.deviceId} · ${pkg.hardwareVersion}", style = MaterialTheme.typography.bodySmall)
                    }
                    StatusBadge(
                        label = if (pkg.isInstalled) "INSTALLIERT" else pkg.channel,
                        statusType = if (pkg.isInstalled) "GESCHUETZT" else if (pkg.isCriticalSecurity) "KRITISCH" else pkg.channel
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Version: ${target?.firmwareVersion ?: "-"} → ${pkg.firmwareVersion} (Min: ${pkg.minRequiredVersion})",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Quelle: ${pkg.sourceName} · Methode: ${pkg.installMethod} · Grösse: ${if (pkg.packageSizeBytes > 0) Formatter.formatShortFileSize(context, pkg.packageSizeBytes) else "Doku"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "SHA-256: ${pkg.sha256Checksum.take(28)}… · Signatur: ${pkg.publisherKeyId}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(pkg.changelog, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(label = pkg.verificationStatus, statusType = pkg.verificationStatus)
                    if (!pkg.isInstalled) {
                        Button(
                            onClick = { onRequestInstall(pkg) },
                            enabled = !channelLocked,
                            modifier = Modifier.testTag("install_pkg_${pkg.id}")
                        ) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (channelLocked) "Kanal gesperrt" else "UPDATE INSTALLIEREN")
                        }
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onOpenFlashCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jump_to_flash_from_packages")
            ) {
                Icon(Icons.Rounded.Memory, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lokale .bin Datei im Flash Center prüfen")
            }
        }
    }
}

@Composable
private fun UpdateSourcesAndSettingsTab(
    settings: CyberSettingsState,
    sources: List<UpdateSourceEntity>,
    onSetSourceEnabled: (String, Boolean) -> Unit,
    onSetAutoCheck: (Boolean) -> Unit,
    onSetNotifyUpdates: (Boolean) -> Unit,
    onSetWifiOnly: (Boolean) -> Unit,
    onSetAllowCellular: (Boolean) -> Unit,
    onSetAskBeforeInstall: (Boolean) -> Unit,
    onSetUpdateWindow: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Update-Quellen",
                subtitle = "Jede Quelle erfordert Signatur- & SHA-256-Prüfung",
                icon = Icons.Rounded.Source
            )
        }
        items(sources, key = { it.id }) { src ->
            CyberHoloCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(src.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text(src.endpointUrl, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = src.isEnabled,
                        onCheckedChange = { onSetSourceEnabled(src.id, it) },
                        modifier = Modifier.testTag("source_switch_${src.id}")
                    )
                }
            }
        }

        item {
            CyberHoloCard {
                SectionHeader(
                    title = "Update-Regeln & Sicherheitsschalter",
                    subtitle = "Keine erzwungene Installation ohne Benutzerzustimmung",
                    icon = Icons.Rounded.SettingsApplications
                )
                Spacer(Modifier.height(8.dp))
                SettingSwitchRow("Automatisch nach Updates suchen", settings.autoUpdateCheck, onSetAutoCheck)
                SettingSwitchRow("Bei neuen Updates benachrichtigen", settings.notifyOnUpdates, onSetNotifyUpdates)
                SettingSwitchRow("Downloads nur über WLAN", settings.wifiOnlyDownloads, onSetWifiOnly)
                SettingSwitchRow("Downloads über Mobilfunk (4G/A7670E) erlauben", settings.allowCellularDownloads, onSetAllowCellular)
                SettingSwitchRow("Vor jeder Installation explizit nachfragen", settings.askBeforeInstall, onSetAskBeforeInstall)

                Spacer(Modifier.height(8.dp))
                Text("Bevorzugtes Zeitfenster:", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MANUELL", "NACHTS 02-05", "SOFORT_WLAN").forEach { win ->
                        FilterChip(
                            selected = settings.updateWindowLabel == win,
                            onClick = { onSetUpdateWindow(win) },
                            label = { Text(win) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateHistoryAndRecoveryTab(
    devices: List<ManagedDeviceEntity>,
    history: List<UpdateHistoryEntity>,
    recoveryPoints: List<RecoveryPointEntity>,
    onCreateRecoveryPoint: (ManagedDeviceEntity) -> Unit,
    onRequestRollback: (RecoveryPointEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CyberHoloCard(highlightBorder = true) {
                SectionHeader(
                    title = "Backup & Recovery Center",
                    subtitle = "Sicherung vor Updates; CAM-TF-Aufnahmen bleiben geschützt",
                    icon = Icons.Rounded.Backup
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val snapshotDevices = devices.filter { it.deviceRole in listOf("BOX_MASTER", "DISPLAY", "CAM", "WEBHOST") }
                    items(snapshotDevices, key = { it.id }) { dev ->
                        OutlinedButton(onClick = { onCreateRecoveryPoint(dev) }) {
                            Text("+ Snapshot ${dev.name.substringAfter("CYBER ")}")
                        }
                    }
                }
            }
        }

        items(recoveryPoints, key = { it.id }) { point ->
            CyberHoloCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${point.deviceName} · ${point.firmwareVersion}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${point.hardwareVersion} · SHA: ${point.backupSha256.take(16)}…", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                    }
                    StatusBadge(
                        label = if (point.isRollbackSupported) "ROLLBACK BEREIT" else "NUR DOKU",
                        statusType = if (point.isRollbackSupported) "GESCHUETZT" else "STANDBY"
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(point.rollbackNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (point.isRollbackSupported) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onRequestRollback(point) },
                        modifier = Modifier.testTag("rollback_btn_${point.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Rounded.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Wiederherstellen")
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Update-Verlauf (${history.size})",
                subtitle = "Unverfälschtes Installations- und Fehlerprotokoll",
                icon = Icons.Rounded.History
            )
        }

        if (history.isEmpty()) {
            item {
                CyberHoloCard {
                    Text(
                        text = "Noch keine Update- oder Recovery-Vorgänge durchgeführt.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(history, key = { it.id }) { entry ->
                val dateStr = remember(entry.timestampEpochMs) {
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN).format(Date(entry.timestampEpochMs))
                }
                CyberHoloCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${entry.deviceName} (${entry.channel})", style = MaterialTheme.typography.titleSmall)
                        StatusBadge(label = entry.resultStatus, statusType = entry.resultStatus)
                    }
                    Text("${entry.fromVersion} → ${entry.toVersion} · $dateStr", style = MaterialTheme.typography.bodySmall)
                    if (entry.errorDetail.isNotBlank()) {
                        Text(entry.errorDetail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
