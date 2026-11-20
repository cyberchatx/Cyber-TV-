package com.example.ui.screens

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
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GppMaybe
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SystemSecurityUpdateWarning
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.AppSelfSecurityReport
import com.example.data.ManagedDeviceEntity
import com.example.data.SecurityAuditEntity
import com.example.data.SecurityEventEntity
import com.example.data.UpdatePackageEntity
import com.example.data.VulnerabilityEntity
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityCenterScreen(
    devices: List<ManagedDeviceEntity>,
    packages: List<UpdatePackageEntity>,
    vulnerabilities: List<VulnerabilityEntity>,
    audits: List<SecurityAuditEntity>,
    events: List<SecurityEventEntity>,
    appSelfReport: AppSelfSecurityReport,
    lastScanEpochMs: Long,
    onRunFullSecurityAudit: () -> Unit,
    onAuthorizeDevice: (ManagedDeviceEntity) -> Unit,
    onBlockDevice: (ManagedDeviceEntity) -> Unit,
    onResetDevicePairing: (ManagedDeviceEntity) -> Unit,
    onTriggerEmergencyResponse: (ManagedDeviceEntity) -> Unit,
    onJumpToUpdateCenter: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingEmergencyDevice by remember { mutableStateOf<ManagedDeviceEntity?>(null) }

    val tabs = listOf("Dashboard & Prüfung", "Schwachstellen", "Gerätezugriff", "Protokoll & Notfall")

    Column(modifier = Modifier.fillMaxSize()) {
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
                    modifier = Modifier.testTag("security_tab_$idx"),
                    text = { Text(title, maxLines = 1) }
                )
            }
        }

        when (selectedTab) {
            0 -> SecurityDashboardTab(
                devices = devices,
                packages = packages,
                audits = audits,
                appSelfReport = appSelfReport,
                lastScanEpochMs = lastScanEpochMs,
                onRunFullSecurityAudit = onRunFullSecurityAudit,
                onJumpToUpdateCenter = onJumpToUpdateCenter
            )

            1 -> VulnerabilitiesTab(
                vulnerabilities = vulnerabilities,
                packages = packages,
                onJumpToUpdateCenter = onJumpToUpdateCenter
            )

            2 -> DeviceAccessTab(
                devices = devices,
                onAuthorizeDevice = onAuthorizeDevice,
                onBlockDevice = onBlockDevice,
                onResetDevicePairing = onResetDevicePairing
            )

            3 -> SecurityLogAndEmergencyTab(
                devices = devices,
                events = events,
                onRequestEmergency = { pendingEmergencyDevice = it }
            )
        }
    }

    pendingEmergencyDevice?.let { dev ->
        AlertDialog(
            onDismissRequest = { pendingEmergencyDevice = null },
            title = { Text("Notfallmassnahme für ${dev.name}?") },
            text = {
                Text("Gerät wird im Security Center als KRITISCH markiert, nicht-autorisierte Zugriffe werden gesperrt und ein Sicherheitsprotokoll gespeichert. Lokale CAM-Aufnahmen (/CYBER_CAM/*) und andere CYBER-Geräte bleiben geschützt.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTriggerEmergencyResponse(dev)
                        pendingEmergencyDevice = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_emergency_button")
                ) { Text("Notfallmassnahme ausführen") }
            },
            dismissButton = {
                TextButton(onClick = { pendingEmergencyDevice = null }) { Text("Abbrechen") }
            }
        )
    }
}

@Composable
private fun SecurityDashboardTab(
    devices: List<ManagedDeviceEntity>,
    packages: List<UpdatePackageEntity>,
    audits: List<SecurityAuditEntity>,
    appSelfReport: AppSelfSecurityReport,
    lastScanEpochMs: Long,
    onRunFullSecurityAudit: () -> Unit,
    onJumpToUpdateCenter: () -> Unit
) {
    val criticalDevices = devices.count { it.securityStatus == "KRITISCH" || it.securityStatus == "SICHERHEITSRISIKO" }
    val uncheckedDevices = devices.count { it.securityStatus == "NICHT_UEBERPRUEFT" || it.securityStatus == "NICHT_UEBERPRUEFBAR" }
    val pendingSecurityUpdates = packages.count { !it.isInstalled && it.isCriticalSecurity }

    val overallStatus = when {
        criticalDevices > 0 || pendingSecurityUpdates > 0 -> "KRITISCH"
        audits.isEmpty() -> "NICHT_UEBERPRUEFT"
        audits.any { it.resultStatus == "PRUEFUNG_ERFORDERLICH" || it.resultStatus == "UPDATE_VERFUEGBAR" } -> "PRUEFUNG_ERFORDERLICH"
        else -> "GESCHUETZT"
    }

    val lastScanLabel = remember(lastScanEpochMs) {
        if (lastScanEpochMs == 0L) "Noch keine vollständige Prüfung ausgeführt"
        else SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN).format(Date(lastScanEpochMs))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CyberHoloCard(highlightBorder = true) {
                SectionHeader(
                    title = "CYBER SECURITY UPDATE CENTER",
                    subtitle = lastScanLabel,
                    icon = Icons.Rounded.Security,
                    trailing = { StatusBadge(label = overallStatus, statusType = overallStatus) }
                )
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(label = "$pendingSecurityUpdates Krit. Patches", statusType = if (pendingSecurityUpdates > 0) "KRITISCH" else "GESCHUETZT")
                    StatusBadge(label = "$uncheckedDevices Ungeprüft", statusType = if (uncheckedDevices > 0) "NICHT_UEBERPRUEFT" else "GESCHUETZT")
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Ehrliche Prüfung: Ungeprüfte oder nicht verbundene Geräte werden niemals automatisch als GESCHÜTZT markiert.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onRunFullSecurityAudit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("full_security_audit_button")
                ) {
                    Icon(Icons.Rounded.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("VOLLSTÄNDIGE SICHERHEITSPRÜFUNG")
                }
                if (pendingSecurityUpdates > 0) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onJumpToUpdateCenter,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.SystemSecurityUpdateWarning, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Kritische Sicherheitsupdates im Update Center öffnen")
                    }
                }
            }
        }

        // App Self-Security (Section 14)
        item {
            CyberHoloCard {
                SectionHeader(
                    title = "App-Selbstsicherheit (CYBER FLASH APP)",
                    subtitle = "${appSelfReport.packageName} · v${appSelfReport.versionName} (${appSelfReport.versionCode})",
                    icon = Icons.Rounded.Shield,
                    trailing = { StatusBadge(label = appSelfReport.overallStatus, statusType = appSelfReport.overallStatus) }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "APK-Signatur (SHA-256): ${appSelfReport.signingCertSha256}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Breite Speicherrechte: ${if (appSelfReport.hasDangerousBroadStoragePerm) "JA" else "NEIN (SAF genutzt)"} · APK-Side-Install: ${if (appSelfReport.hasInstallPackagesPerm) "JA" else "NEIN"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Secrets/PIN-Schutz: SHA-256 gehasht, keine Klartext-Passwörter in DB/Logs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            SectionHeader(
                title = "Aktuelle Audit-Ergebnisse (${audits.size})",
                subtitle = "Nach Gerät und Kategorie geordnet"
            )
        }

        if (audits.isEmpty()) {
            item {
                CyberHoloCard {
                    Text(
                        text = "Tippe auf 'VOLLSTÄNDIGE SICHERHEITSPRÜFUNG', um alle Geräte, Signaturen, Berechtigungen und bekannte Schwachstellen zu prüfen.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            items(audits, key = { it.id }) { audit ->
                CyberHoloCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${audit.deviceName} · ${audit.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(audit.checkName, style = MaterialTheme.typography.titleSmall)
                        }
                        StatusBadge(label = audit.resultStatus, statusType = audit.resultStatus)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(audit.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun VulnerabilitiesTab(
    vulnerabilities: List<VulnerabilityEntity>,
    packages: List<UpdatePackageEntity>,
    onJumpToUpdateCenter: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Schwachstellen- & CVE-Manager",
                subtitle = "Verifizierte NVD-Hinweise & interne CYBER-Security-Advisories (keine erfundenen CVEs)",
                icon = Icons.Rounded.BugReport
            )
        }

        items(vulnerabilities, key = { it.id }) { vuln ->
            val fixPkg = packages.firstOrNull { it.id == vuln.fixPackageId }
            CyberHoloCard(highlightBorder = vuln.status == "BESTAETIGT" && vuln.severity == "KRITISCH") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(vuln.cveId, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${vuln.affectedComponent} (${vuln.affectedVersion})", style = MaterialTheme.typography.bodySmall)
                    }
                    StatusBadge(label = vuln.status, statusType = vuln.status)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(label = "Risiko: ${vuln.severity}", statusType = vuln.severity)
                    StatusBadge(label = "CVSS: ${vuln.cvssScore}", statusType = "CYBER")
                }
                Spacer(Modifier.height(6.dp))
                Text(vuln.description, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text("Empfehlung: ${vuln.recommendedAction}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (fixPkg != null && vuln.status != "BEHOBEN") {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onJumpToUpdateCenter,
                        modifier = Modifier.testTag("fix_vuln_${vuln.id}")
                    ) {
                        Text("Patch ${fixPkg.firmwareVersion} im Update Center installieren")
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceAccessTab(
    devices: List<ManagedDeviceEntity>,
    onAuthorizeDevice: (ManagedDeviceEntity) -> Unit,
    onBlockDevice: (ManagedDeviceEntity) -> Unit,
    onResetDevicePairing: (ManagedDeviceEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Geräte- & Netzwerkzugriff (TEIL 15)",
                subtitle = "Autorisierte Verbindungen verwalten, unbekannte/riskante Geräte sperren oder Kopplung zurücksetzen",
                icon = Icons.Rounded.PrivacyTip
            )
        }

        items(devices, key = { it.id }) { dev ->
            CyberHoloCard(highlightBorder = dev.authStatus == "GESPERRT") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dev.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${dev.protocol} · ${dev.ipOrMac}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StatusBadge(label = dev.authStatus, statusType = dev.authStatus)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onAuthorizeDevice(dev) },
                        modifier = Modifier.testTag("auth_btn_${dev.id}")
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Autorisieren")
                    }
                    OutlinedButton(
                        onClick = { onBlockDevice(dev) },
                        modifier = Modifier.testTag("block_btn_${dev.id}")
                    ) {
                        Icon(Icons.Rounded.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Sperren")
                    }
                    OutlinedButton(
                        onClick = { onResetDevicePairing(dev) },
                        modifier = Modifier.testTag("reset_pair_btn_${dev.id}")
                    ) {
                        Icon(Icons.Rounded.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityLogAndEmergencyTab(
    devices: List<ManagedDeviceEntity>,
    events: List<SecurityEventEntity>,
    onRequestEmergency: (ManagedDeviceEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CyberHoloCard(highlightBorder = true) {
                SectionHeader(
                    title = "Notfallmassnahmen bei Sicherheitsproblem (TEIL 16)",
                    subtitle = "Isoliert betroffene Geräte ohne Zerstörung von CAM-Aufnahmen (/CYBER_CAM/*) oder Abschaltung gesunder Geräte",
                    icon = Icons.Rounded.GppMaybe
                )
                Spacer(Modifier.height(8.dp))
                devices.filter { it.deviceRole in listOf("BOX_MASTER", "DISPLAY", "CAM", "WEBHOST") }.forEach { dev ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dev.name, style = MaterialTheme.typography.labelLarge)
                            Text("Status: ${dev.securityStatus}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { onRequestEmergency(dev) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("emergency_btn_${dev.id}")
                        ) {
                            Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Isolieren")
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Sicherheitsprotokoll (${events.size})",
                subtitle = "Ohne Passwörter, Token oder SIM-PINs protokolliert"
            )
        }

        if (events.isEmpty()) {
            item {
                CyberHoloCard {
                    Text(
                        text = "Noch keine Sicherheitsereignisse aufgezeichnet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(events, key = { it.id }) { ev ->
                val dateStr = remember(ev.timestampEpochMs) {
                    SimpleDateFormat("dd.MM. HH:mm:ss", Locale.GERMAN).format(Date(ev.timestampEpochMs))
                }
                CyberHoloCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(ev.title, style = MaterialTheme.typography.titleSmall)
                        StatusBadge(label = ev.severity, statusType = ev.severity)
                    }
                    Text(ev.detail, style = MaterialTheme.typography.bodySmall)
                    Text("${ev.actionTaken} · $dateStr", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
