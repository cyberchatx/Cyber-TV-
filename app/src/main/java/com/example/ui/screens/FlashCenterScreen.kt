package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.provider.OpenableColumns
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import com.example.data.BlueprintArtifact
import com.example.data.CyberSeedData
import com.example.data.FlashLogEntity
import com.example.data.TestMatrixItemEntity
import com.example.data.TestStatusOption
import com.example.hardware.FirmwareInspectionResult
import com.example.hardware.UsbSerialCandidate
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMint
import com.example.ui.theme.ObsidianVoid

@Composable
fun FlashCenterScreen(
    usbBridges: List<UsbSerialCandidate>,
    inspectedFirmware: FirmwareInspectionResult?,
    flashLogs: List<FlashLogEntity>,
    testMatrix: List<TestMatrixItemEntity>,
    onRefreshUsb: () -> Unit,
    onInspectFirmwareUri: (android.net.Uri, String, String) -> Unit,
    onClearFlashLogs: () -> Unit,
    onUpdateTestMatrix: (TestMatrixItemEntity, String, String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var targetBoard by remember { mutableStateOf("BOX MASTER (ESP32-S3-A7670E-4G V2.0 Var A)") }
    var editingMatrixItem by remember { mutableStateOf<TestMatrixItemEntity?>(null) }
    var pendingExportBlueprint by remember { mutableStateOf<BlueprintArtifact?>(null) }

    val firmwarePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val name = context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst() && idx >= 0) c.getString(idx) else "firmware.bin"
            } ?: "firmware.bin"
            onInspectFirmwareUri(uri, name, targetBoard)
        }
    }

    val blueprintExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        val bp = pendingExportBlueprint
        if (uri != null && bp != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(bp.codeContent.toByteArray())
                }
                onShowMessage("${bp.title} exportiert.")
            } catch (e: Exception) {
                onShowMessage("Export fehlgeschlagen: ${e.localizedMessage}")
            }
        }
        pendingExportBlueprint = null
    }

    val tabs = listOf("Flash Center", "Testmatrix (TEIL 43)", "Firmware & Webhost")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CyberCyan
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    modifier = Modifier.testTag("flash_tab_$idx"),
                    text = { Text(title, maxLines = 1) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                val boards = listOf(
                    "BOX MASTER (ESP32-S3-A7670E-4G V2.0 Var A)",
                    "DISPLAY (ESP32-S3-Touch-LCD-7B)",
                    "CAM (ESP32-S3-CAM-OV5640)"
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard(highlightBorder = true) {
                            SectionHeader(
                                title = "CYBER FLASH CENTER",
                                subtitle = "Echte .bin-Header- & SHA-256-Prüfung + USB-UART-Erkennung (TEIL 33)",
                                icon = Icons.Rounded.Memory
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Hinweis: esptool / ESP-IDF / Arduino CLI sind Host-Werkzeuge und werden nicht als eingebetteter Compiler in dieser APK vorgetäuscht.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberAmber
                            )
                            Spacer(Modifier.height(10.dp))
                            Text("Ziel-Platine auswählen:", style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(4.dp))
                            boards.forEach { b ->
                                FilterChip(
                                    selected = targetBoard == b,
                                    onClick = { targetBoard = b },
                                    label = { Text(b) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { firmwarePickerLauncher.launch(arrayOf("*/*")) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("select_firmware_bin_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                                ) {
                                    Icon(Icons.Rounded.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(".bin Firmware prüfen")
                                }
                                OutlinedButton(onClick = onRefreshUsb) {
                                    Icon(Icons.Rounded.Usb, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("USB-OTG")
                                }
                            }
                        }
                    }

                    item {
                        CyberHoloCard {
                            Text("Verbundene USB-UART Brücken (${usbBridges.size})", style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Spacer(Modifier.height(4.dp))
                            if (usbBridges.isEmpty()) {
                                Text(
                                    text = "Kein ESP32-S3 USB-Gerät erkannt. Für direktes USB-Flashing Board per OTG-Adapter verbinden oder esptool am Host nutzen.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                usbBridges.forEach { usb ->
                                    Text("• ${usb.productLabel} · ${usb.chipFamilyHint}", style = MaterialTheme.typography.bodySmall, color = CyberMint)
                                }
                            }
                        }
                    }

                    if (inspectedFirmware != null) {
                        item {
                            CyberHoloCard(highlightBorder = inspectedFirmware.hasEspMagicByte) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(inspectedFirmware.fileName, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                                    StatusBadge(
                                        label = if (inspectedFirmware.hasEspMagicByte) "0xE9 VALID" else "HINWEIS",
                                        statusType = if (inspectedFirmware.hasEspMagicByte) "VALIDIERT_BEREIT" else "FORMAT_HINWEIS"
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text("Grösse: ${Formatter.formatShortFileSize(context, inspectedFirmware.sizeBytes)}", style = MaterialTheme.typography.bodySmall)
                                Text("Chip-Analyse: ${inspectedFirmware.detectedChipHint}", style = MaterialTheme.typography.bodySmall)
                                Text("SHA-256: ${inspectedFirmware.sha256}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    item {
                        SectionHeader(
                            title = "Flash- & Prüfprotokoll (${flashLogs.size})",
                            icon = Icons.Rounded.Checklist,
                            trailing = {
                                if (flashLogs.isNotEmpty()) {
                                    IconButton(onClick = onClearFlashLogs) {
                                        Icon(Icons.Rounded.DeleteSweep, contentDescription = "Protokoll leeren")
                                    }
                                }
                            }
                        )
                    }

                    items(flashLogs, key = { it.id }) { log ->
                        CyberHoloCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.firmwareFileName, style = MaterialTheme.typography.titleSmall)
                                StatusBadge(label = log.resultStatus, statusType = log.resultStatus)
                            }
                            Text("Ziel: ${log.targetHardware}", style = MaterialTheme.typography.bodySmall, color = CyberCyan)
                            Text("Transport: ${log.transport}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("SHA-256: ${log.sha256Hash.take(24)}…", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(log.detailMessage, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            1 -> {
                // Pro-Board Testbericht & Testmatrix (TEIL 43)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "PRO-BOARD-TESTBERICHT & MATRIX",
                                subtitle = "Ehrliche Statusführung ohne erfundene Hardware-Testergebnisse (Antippen zum Aktualisieren)",
                                icon = Icons.Rounded.Checklist
                            )
                        }
                    }
                    items(testMatrix, key = { it.id }) { item ->
                        CyberHoloCard(
                            modifier = Modifier.testTag("matrix_item_${item.id}"),
                            onClick = { editingMatrixItem = item }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.component, style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                                    Text(item.featureName, style = MaterialTheme.typography.titleSmall)
                                }
                                StatusBadge(
                                    label = TestStatusOption.fromCode(item.status).label,
                                    statusType = item.status
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(item.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            2 -> {
                // Firmware & Webhost Blueprints (TEIL 34 & TEIL 48)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "ESP32-S3 FIRMWARE & CYBER WEBHOST",
                                subtitle = "Modulare Referenz-Quelltexte für BOX MASTER, DISPLAY, CAM, CYBER LINK & ~/CYBER_TV_WEBHOST",
                                icon = Icons.Rounded.Code
                            )
                        }
                    }
                    items(CyberSeedData.blueprints, key = { it.id }) { bp ->
                        CyberHoloCard {
                            Text(bp.title, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Text("${bp.targetPath} · ${bp.language}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(bp.summary, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = bp.codeContent,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = CyberMint,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText(bp.title, bp.codeContent))
                                    onShowMessage("Code in Zwischenablage kopiert.")
                                }) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Kopieren")
                                }
                                Button(
                                    onClick = {
                                        pendingExportBlueprint = bp
                                        blueprintExportLauncher.launch(bp.targetPath.substringAfterLast("/"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                                ) {
                                    Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Exportieren")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editingMatrixItem?.let { item ->
        var selectedStatus by remember { mutableStateOf(item.status) }
        var notes by remember { mutableStateOf(item.notes) }
        AlertDialog(
            onDismissRequest = { editingMatrixItem = null },
            title = { Text("${item.component}: ${item.featureName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TestStatusOption.entries.forEach { opt ->
                        FilterChip(
                            selected = selectedStatus == opt.code,
                            onClick = { selectedStatus = opt.code },
                            label = { Text(opt.label) }
                        )
                    }
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Prüfnotiz") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateTestMatrix(item, selectedStatus, notes)
                    editingMatrixItem = null
                }) { Text("Aktualisieren") }
            },
            dismissButton = {
                TextButton(onClick = { editingMatrixItem = null }) { Text("Abbrechen") }
            }
        )
    }
}
