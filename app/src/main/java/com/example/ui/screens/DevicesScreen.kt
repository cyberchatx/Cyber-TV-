package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.BluetoothSearching
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.SettingsRemote
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AutomationRuleEntity
import com.example.data.CyberSettingsState
import com.example.data.ManagedDeviceEntity
import com.example.data.UpdatePackageEntity
import com.example.hardware.BleDiscoveredNode
import com.example.hardware.UsbSerialCandidate
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TouchRemotePad
import com.example.ui.components.TvClientSelectorStrip
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMint
import com.example.ui.theme.ObsidianVoid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DevicesScreen(
    settings: CyberSettingsState,
    devices: List<ManagedDeviceEntity>,
    updatePackages: List<UpdatePackageEntity>,
    usbBridges: List<UsbSerialCandidate>,
    bleNodes: List<BleDiscoveredNode>,
    isBleScanning: Boolean,
    bleStatusMessage: String,
    automations: List<AutomationRuleEntity>,
    onStartBleScan: () -> Unit,
    onStopBleScan: () -> Unit,
    onPairBleNode: (BleDiscoveredNode) -> Unit,
    onRefreshUsb: () -> Unit,
    onSelectTv: (String) -> Unit,
    onSendRemoteAction: (String) -> Unit,
    onUpdateDevice: (ManagedDeviceEntity, String, String, String) -> Unit,
    onAddModularDevice: (String, String, String, String, String, String) -> Unit,
    onAdjustVolume: (ManagedDeviceEntity, Int, Boolean) -> Unit,
    onToggleAutomation: (String, Boolean) -> Unit,
    onNavigateToUpdates: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var editingDevice by remember { mutableStateOf<ManagedDeviceEntity?>(null) }
    var showAddModularDialog by remember { mutableStateOf(false) }
    var pendingConfirmRule by remember { mutableStateOf<AutomationRuleEntity?>(null) }

    val blePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            onStartBleScan()
        } else {
            onShowMessage("Bluetooth-Berechtigungen benötigt für CYBER LINK BLE-Scan.")
        }
    }

    val tabs = listOf("Geräte-Manager", "Multi-TV 1–6", "Gaming & Controller", "Smart Home")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    modifier = Modifier.testTag("devices_tab_$idx"),
                    text = { Text(title, maxLines = 1) }
                )
            }
        }

        when (selectedTab) {
            0 -> HardwareAndBleTab(
                devices = devices.filter {
                    it.deviceRole in listOf("BOX_MASTER", "DISPLAY", "CAM", "CYBER_OS", "WEBHOST", "APP_SELF", "FUTURE_CYBER")
                },
                updatePackages = updatePackages,
                usbBridges = usbBridges,
                bleNodes = bleNodes,
                isBleScanning = isBleScanning,
                bleStatusMessage = bleStatusMessage,
                onScanBle = {
                    if (isBleScanning) {
                        onStopBleScan()
                    } else {
                        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
                        } else {
                            arrayOf(Manifest.permission.BLUETOOTH)
                        }
                        blePermissionLauncher.launch(perms)
                    }
                },
                onPairBleNode = onPairBleNode,
                onRefreshUsb = onRefreshUsb,
                onEditDevice = { editingDevice = it },
                onAddModularClick = { showAddModularDialog = true },
                onNavigateToUpdates = onNavigateToUpdates
            )

            1 -> MultiTvAndRoomTab(
                tvClients = devices.filter { it.deviceRole == "TV_CLIENT" },
                activeTvId = settings.activeTvId,
                onSelectTv = onSelectTv,
                onSendRemoteAction = onSendRemoteAction,
                onEditDevice = { editingDevice = it },
                onAdjustVolume = onAdjustVolume
            )

            2 -> GamingAndControllerTab(
                controllers = devices.filter { it.deviceRole == "CONTROLLER" },
                onEditDevice = { editingDevice = it }
            )

            3 -> SmartHomeAndAutomationTab(
                smartHomeDevices = devices.filter { it.deviceRole == "SMART_HOME" },
                automations = automations,
                onEditDevice = { editingDevice = it },
                onToggleAutomation = { rule, enabled ->
                    if (enabled && rule.requiresConfirmation) {
                        pendingConfirmRule = rule
                    } else {
                        onToggleAutomation(rule.id, enabled)
                    }
                }
            )
        }
    }

    editingDevice?.let { dev ->
        DeviceEditDialog(
            device = dev,
            onDismiss = { editingDevice = null },
            onSave = { endpoint, room, status ->
                onUpdateDevice(dev, endpoint, room, status)
                editingDevice = null
            }
        )
    }

    if (showAddModularDialog) {
        AddModularDeviceDialog(
            onDismiss = { showAddModularDialog = false },
            onCreate = { name, role, model, hwVer, endpoint, protocol ->
                onAddModularDevice(name, role, model, hwVer, endpoint, protocol)
                showAddModularDialog = false
            }
        )
    }

    pendingConfirmRule?.let { rule ->
        AlertDialog(
            onDismissRequest = { pendingConfirmRule = null },
            title = { Text("Sichere Aktion bestätigen") },
            text = { Text("Automation '${rule.name}' aktivieren? (${rule.triggerText} → ${rule.actionText})") },
            confirmButton = {
                Button(onClick = {
                    onToggleAutomation(rule.id, true)
                    pendingConfirmRule = null
                }) { Text("Aktivieren") }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmRule = null }) { Text("Abbrechen") }
            }
        )
    }
}

@Composable
private fun HardwareAndBleTab(
    devices: List<ManagedDeviceEntity>,
    updatePackages: List<UpdatePackageEntity>,
    usbBridges: List<UsbSerialCandidate>,
    bleNodes: List<BleDiscoveredNode>,
    isBleScanning: Boolean,
    bleStatusMessage: String,
    onScanBle: () -> Unit,
    onPairBleNode: (BleDiscoveredNode) -> Unit,
    onRefreshUsb: () -> Unit,
    onEditDevice: (ManagedDeviceEntity) -> Unit,
    onAddModularClick: () -> Unit,
    onNavigateToUpdates: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_esp32_hardware_deck),
                    contentDescription = stringResource(R.string.cd_hardware_deck),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, ObsidianVoid.copy(alpha = 0.88f))
                            )
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "UNIVERSAL DEVICE MANAGER · ALL CYBER DEVICES",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberCyan
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onAddModularClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_modular_device_button")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Zukünftiges / modulares CYBER-Gerät registrieren")
            }
        }

        items(devices, key = { it.id }) { dev ->
            val availableUpdates = updatePackages.count { it.deviceId == dev.id && !it.isInstalled }
            val lastUpdLabel = remember(dev.lastUpdateEpochMs) {
                if (dev.lastUpdateEpochMs == 0L) "Ausstehend"
                else SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMAN).format(Date(dev.lastUpdateEpochMs))
            }
            val batteryLabel = if (dev.batteryPercent >= 0) "${dev.batteryPercent}%" else "Netzbetrieb / N/A"

            CyberHoloCard(highlightBorder = availableUpdates > 0 || dev.securityStatus == "KRITISCH") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dev.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text("${dev.deviceRole} · ${dev.hardwareModel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                    StatusBadge(label = dev.status, statusType = dev.status)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(label = "Sec: ${dev.securityStatus}", statusType = dev.securityStatus)
                    StatusBadge(label = "Auth: ${dev.authStatus}", statusType = dev.authStatus)
                    if (availableUpdates > 0) {
                        StatusBadge(label = "$availableUpdates Update(s)", statusType = "UPDATE_VERFUEGBAR")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HW: ${dev.hardwareVersion} · FW: ${dev.firmwareVersion}\n" +
                            "Verbindung: ${dev.protocol} · ${dev.ipOrMac}\n" +
                            "Akku: $batteryLabel · Speicher: ${dev.storageSummary}\n" +
                            "Letztes Update: $lastUpdLabel\n" +
                            "Diagnose: ${dev.lastDiagnosticsSummary}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onEditDevice(dev) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "${dev.name} bearbeiten", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (availableUpdates > 0) {
                    Spacer(Modifier.height(6.dp))
                    TextButton(onClick = onNavigateToUpdates) {
                        Text("Updates für ${dev.name} anzeigen →")
                    }
                }
            }
        }

        item {
            CyberHoloCard(highlightBorder = isBleScanning) {
                SectionHeader(
                    title = "CYBER LINK · BLE 5.0 Scanner",
                    subtitle = bleStatusMessage,
                    icon = Icons.Rounded.BluetoothSearching,
                    trailing = {
                        Button(
                            onClick = onScanBle,
                            modifier = Modifier.testTag("ble_scan_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBleScanning) CyberAmber else CyberCyan,
                                contentColor = ObsidianVoid
                            )
                        ) {
                            Text(if (isBleScanning) "Stopp" else "BLE Scan")
                        }
                    }
                )
                if (bleNodes.isEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Keine aktiven BLE-Knoten im aktuellen Scan. BLE dient nur für Pairing, Status & kleine Steuerbefehle (keine Videoströme).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                    bleNodes.forEach { node ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(node.name, style = MaterialTheme.typography.titleSmall)
                                Text("${node.address} · ${node.rssi} dBm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            FilledTonalButton(onClick = { onPairBleNode(node) }) {
                                Text("Koppeln")
                            }
                        }
                    }
                }
            }
        }

        item {
            CyberHoloCard {
                SectionHeader(
                    title = "USB-OTG ESP32-S3 Erkennung",
                    subtitle = "Android UsbManager Prüfung für CP210x / CH340 / Espressif JTAG",
                    icon = Icons.Rounded.Usb,
                    trailing = {
                        OutlinedButton(onClick = onRefreshUsb, modifier = Modifier.testTag("usb_scan_button")) {
                            Text("Prüfen")
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                if (usbBridges.isEmpty()) {
                    Text(
                        text = "Kein ESP32-S3 per USB-OTG verbunden. Verbinde ein Board mit OTG-Kabel und tippe auf Prüfen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    usbBridges.forEach { usb ->
                        Text(
                            text = "• ${usb.productLabel} (${usb.chipFamilyHint}) · VID:PID ${usb.vendorId.toString(16)}:${usb.productId.toString(16)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberMint
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiTvAndRoomTab(
    tvClients: List<ManagedDeviceEntity>,
    activeTvId: String,
    onSelectTv: (String) -> Unit,
    onSendRemoteAction: (String) -> Unit,
    onEditDevice: (ManagedDeviceEntity) -> Unit,
    onAdjustVolume: (ManagedDeviceEntity, Int, Boolean) -> Unit
) {
    val selectedTv = tvClients.firstOrNull { it.id == activeTvId } ?: tvClients.firstOrNull()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CyberHoloCard {
                SectionHeader(
                    title = "CYBER MULTI-TV 6 & MULTIROOM",
                    subtitle = "Bis zu 6 verwaltete TV-Clients (keine 6 garantierten parallelen HD-Streams)",
                    icon = Icons.Rounded.Tv
                )
                Spacer(Modifier.height(8.dp))
                TvClientSelectorStrip(tvClients = tvClients, activeTvId = activeTvId, onSelectTv = onSelectTv)
            }
        }

        item {
            TouchRemotePad(targetDevice = selectedTv, onSendCommand = onSendRemoteAction)
        }

        items(tvClients, key = { it.id }) { tv ->
            CyberHoloCard(highlightBorder = tv.id == activeTvId) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tv.name, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                        Text("Raum: ${tv.roomName} · ${tv.ipOrMac}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StatusBadge(label = tv.status, statusType = tv.status)
                    IconButton(onClick = { onEditDevice(tv) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "TV bearbeiten", tint = CyberCyan)
                    }
                }
                Spacer(Modifier.height(6.dp))
                var sliderVal by remember(tv.volumeLevel) { mutableFloatStateOf(tv.volumeLevel.toFloat()) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lautstärke: ${sliderVal.toInt()}%", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(105.dp))
                    Slider(
                        value = sliderVal,
                        onValueChange = { sliderVal = it },
                        onValueChangeFinished = { onAdjustVolume(tv, sliderVal.toInt(), tv.isMuted) },
                        valueRange = 0f..100f,
                        enabled = tv.status != "OFFLINE",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GamingAndControllerTab(
    controllers: List<ManagedDeviceEntity>,
    onEditDevice: (ManagedDeviceEntity) -> Unit
) {
    // Interactive 2D Cyber Pulse Reflex Demo (TEIL 19: playable 2D demo)
    var score by remember { mutableIntStateOf(0) }
    var targetX by remember { mutableFloatStateOf(0.5f) }
    var targetY by remember { mutableFloatStateOf(0.5f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(controllers, key = { it.id }) { ctrl ->
            CyberHoloCard {
                SectionHeader(
                    title = ctrl.name,
                    subtitle = "Pfad: Switch-Controller → BLE → CYBER DISPLAY → WLAN → Spiel-Client",
                    icon = Icons.Rounded.Gamepad,
                    trailing = { StatusBadge(label = ctrl.status, statusType = ctrl.status) }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Prüf-Checkliste (TEIL 20): 1. Modell identifizieren · 2. BLE-HID-Protokoll · 3. Tasten & Joystick · 4. Latenz · 5. RGB nur freigeben, wenn Protokoll dies bestätigt.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { onEditDevice(ctrl) }) {
                    Text("Controller-Status / MAC anpassen")
                }
            }
        }

        item {
            CyberHoloCard(highlightBorder = true) {
                SectionHeader(
                    title = "CYBER GAME CENTER · 2D Touch Reflex Demo",
                    subtitle = "Rendering: Android Jetpack Compose Canvas (ESP32-BOX ist kein 3D-PC)",
                    icon = Icons.Rounded.SportsEsports,
                    trailing = { Text("Score: $score", style = MaterialTheme.typography.titleMedium, color = CyberCyan) }
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(ObsidianVoid)
                        .testTag("game_2d_canvas")
                        .clickable {
                            score += 10
                            targetX = ((score * 37 % 80) + 10) / 100f
                            targetY = ((score * 53 % 70) + 15) / 100f
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width * targetX
                        val cy = size.height * targetY
                        drawCircle(
                            color = CyberCyan.copy(alpha = 0.25f),
                            radius = 44.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = CyberCyan,
                            radius = 22.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }
                    Text(
                        text = "Tippe auf das Spielfeld zum Ziel-Wechsel (+10 Pkt)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SmartHomeAndAutomationTab(
    smartHomeDevices: List<ManagedDeviceEntity>,
    automations: List<AutomationRuleEntity>,
    onEditDevice: (ManagedDeviceEntity) -> Unit,
    onToggleAutomation: (AutomationRuleEntity, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(smartHomeDevices, key = { it.id }) { hub ->
            CyberHoloCard {
                SectionHeader(
                    title = hub.name,
                    subtitle = "${hub.hardwareModel} · ${hub.ipOrMac}",
                    icon = Icons.Rounded.Hub,
                    trailing = { StatusBadge(label = hub.status, statusType = hub.status) }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Hinweis (TEIL 24): Die ESP32-BOX ist kein vollständiger Matter-Hub; lokale Smart-Home-Steuerung erfolgt über kompatible MQTT-/Home-Assistant-Bridges.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { onEditDevice(hub) }) {
                    Text("Broker-Adresse & Status bearbeiten")
                }
            }
        }

        item {
            SectionHeader(
                title = "CYBER AUTOMATION CENTER",
                subtitle = "Wenn-Dann-Regeln (kritische Aktionen mit Bestätigung)",
                icon = Icons.Rounded.AutoMode
            )
        }

        items(automations, key = { it.id }) { rule ->
            CyberHoloCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rule.name, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                        Text("Wenn: ${rule.triggerText}", style = MaterialTheme.typography.bodySmall)
                        Text("Dann: ${rule.actionText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = rule.isEnabled,
                        onCheckedChange = { onToggleAutomation(rule, it) },
                        modifier = Modifier.testTag("automation_switch_${rule.id}")
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceEditDialog(
    device: ManagedDeviceEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var endpoint by remember { mutableStateOf(device.ipOrMac) }
    var room by remember { mutableStateOf(device.roomName) }
    var status by remember { mutableStateOf(device.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${device.name} konfigurieren") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text("IP-Adresse / Hostname / MAC") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Raum (z.B. Wohnzimmer)") },
                    singleLine = true
                )
                Text("Verbindungsstatus:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ONLINE", "STANDBY", "OFFLINE").forEach { st ->
                        OutlinedButton(
                            onClick = { status = st },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (status == st) CyberCyan.copy(alpha = 0.2f) else Color.Transparent
                            )
                        ) {
                            Text(st, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(endpoint, room, status) }) { Text("Speichern") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}

@Composable
private fun AddModularDeviceDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("FUTURE_CYBER") }
    var model by remember { mutableStateOf("ESP32-S3 Modul") }
    var hwVer by remember { mutableStateOf("V1.0") }
    var endpoint by remember { mutableStateOf("192.168.4.50") }
    var protocol by remember { mutableStateOf("WLAN + BLE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modulares CYBER-Gerät anlegen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Gerätename (z.B. CYBER SENSOR)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Hardwaremodell / Typ") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = hwVer,
                    onValueChange = { hwVer = it },
                    label = { Text("Hardwareversion") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text("IP / MAC / Endpunkt") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("FUTURE_CYBER", "CAM", "DISPLAY").forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, role, model, hwVer, endpoint, protocol)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_modular_device")
            ) { Text("Registrieren") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
