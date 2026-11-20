package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ManagedDeviceEntity
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCoral
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMint
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloCardBg
import com.example.ui.theme.HoloCardElevated

@Composable
fun isCyberDarkMode(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
fun CyberHoloCard(
    modifier: Modifier = Modifier,
    highlightBorder: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isCyberDarkMode()
    val borderColor = if (highlightBorder) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    } else if (isDark) {
        HoloBorder
    } else {
        MaterialTheme.colorScheme.outline
    }
    val bgBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                HoloCardElevated.copy(alpha = 0.92f),
                HoloCardBg.copy(alpha = 0.95f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            )
        )
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        color = Color.Transparent,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .background(bgBrush)
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val accent = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.14f))
                        .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun StatusBadge(
    label: String,
    statusType: String,
    modifier: Modifier = Modifier
) {
    val isDark = isCyberDarkMode()
    val greenColor = if (isDark) CyberMint else Color(0xFF007A43)
    val amberColor = if (isDark) CyberAmber else Color(0xFFA85D00)
    val redColor = if (isDark) CyberCoral else Color(0xFFC62828)
    val cyanColor = if (isDark) CyberCyan else MaterialTheme.colorScheme.primary

    val color = when (statusType.uppercase()) {
        "ONLINE", "HARDWARETEST_BESTANDEN", "VALIDIERT_BEREIT", "OK",
        "GESCHUETZT", "AUTORISIERT", "VERIFIZIERT", "VERBUNDEN",
        "BEHOBEN", "ERFOLGREICH", "ABSCHLUSS", "ABGESCHLOSSEN" -> greenColor

        "STANDBY", "IMPLEMENTIERT", "SIMULIERT_GETESTET", "BUILD_ERFOLGREICH",
        "USB_DETECTED", "FORMAT_HINWEIS", "UPDATE_VERFUEGBAR", "PRUEFUNG_ERFORDERLICH",
        "NICHT_UEBERPRUEFBAR", "NICHT_UEBERPRUEFT", "NICHT_GEPRUEFT",
        "NICHT_AUTORISIERT", "MOEGLICH", "MITTEL", "WARNUNG", "RECOVERY",
        "UPDATE_LAEUFT", "BETA", "DEVELOPMENT" -> amberColor

        "OFFLINE", "FEHLGESCHLAGEN", "ALERT", "SICHERHEITSPROBLEM",
        "SICHERHEITSRISIKO", "KRITISCH", "BESTAETIGT", "HOCH",
        "GESPERRT", "UNBEKANNT", "SIGNATUR_FEHLER", "INKOMPATIBEL", "FEHLER" -> redColor

        else -> cyanColor
    }
    val bg by animateColorAsState(color.copy(alpha = if (isDark) 0.15f else 0.12f), label = "badgeBg")
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, color.copy(alpha = 0.55f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TvClientSelectorStrip(
    tvClients: List<ManagedDeviceEntity>,
    activeTvId: String,
    onSelectTv: (String) -> Unit
) {
    val isDark = isCyberDarkMode()
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(tvClients, key = { it.id }) { tv ->
            val selected = tv.id == activeTvId
            val isOnline = tv.status != "OFFLINE"
            FilterChip(
                selected = selected,
                onClick = { onSelectTv(tv.id) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("tv_chip_${tv.id}"),
                label = {
                    Text(
                        text = tv.name.substringBefore(" ·"),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Tv,
                        contentDescription = null,
                        tint = when {
                            selected -> MaterialTheme.colorScheme.onPrimary
                            isOnline -> if (isDark) CyberMint else Color(0xFF007A43)
                            else -> if (isDark) CyberCoral else Color(0xFFC62828)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun TouchRemotePad(
    targetDevice: ManagedDeviceEntity?,
    onSendCommand: (String) -> Unit
) {
    val isAvailable = targetDevice != null && targetDevice.status != "OFFLINE"
    CyberHoloCard(highlightBorder = isAvailable) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CYBER TOUCH REMOTE",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = targetDevice?.let { "${it.name} · ${it.ipOrMac}" } ?: "Kein TV-Client gewählt",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(
                label = targetDevice?.status ?: "OFFLINE",
                statusType = targetDevice?.status ?: "OFFLINE"
            )
        }
        Spacer(Modifier.height(12.dp))
        if (!isAvailable) {
            Text(
                text = "Zielgerät ist OFFLINE – Fernbedienungsaktionen sind gesperrt (TEIL 5 Regel).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RemoteIconButton(
                icon = Icons.Rounded.KeyboardArrowUp,
                description = "Nach oben",
                enabled = isAvailable,
                testTag = "remote_up"
            ) { onSendCommand("NAV_UP") }
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RemoteIconButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    description = "Nach links",
                    enabled = isAvailable,
                    testTag = "remote_left"
                ) { onSendCommand("NAV_LEFT") }
                Button(
                    onClick = { onSendCommand("SELECT_OK") },
                    enabled = isAvailable,
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("remote_ok"),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
                RemoteIconButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    description = "Nach rechts",
                    enabled = isAvailable,
                    testTag = "remote_right"
                ) { onSendCommand("NAV_RIGHT") }
            }
            Spacer(Modifier.height(6.dp))
            RemoteIconButton(
                icon = Icons.Rounded.KeyboardArrowDown,
                description = "Nach unten",
                enabled = isAvailable,
                testTag = "remote_down"
            ) { onSendCommand("NAV_DOWN") }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RemoteIconButton(
                icon = Icons.Rounded.PlayArrow,
                description = "Wiedergabe",
                enabled = isAvailable,
                testTag = "remote_play"
            ) { onSendCommand("MEDIA_PLAY") }
            RemoteIconButton(
                icon = Icons.Rounded.Pause,
                description = "Pause",
                enabled = isAvailable,
                testTag = "remote_pause"
            ) { onSendCommand("MEDIA_PAUSE") }
            RemoteIconButton(
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                description = "Lauter",
                enabled = isAvailable,
                testTag = "remote_vol_up"
            ) { onSendCommand("VOLUME_UP") }
            RemoteIconButton(
                icon = Icons.AutoMirrored.Rounded.VolumeOff,
                description = "Stumm",
                enabled = isAvailable,
                testTag = "remote_mute"
            ) { onSendCommand("VOLUME_MUTE") }
            RemoteIconButton(
                icon = Icons.Rounded.PowerSettingsNew,
                description = "Standby / Ein-Aus",
                enabled = isAvailable,
                testTag = "remote_power"
            ) { onSendCommand("POWER_TOGGLE") }
        }
    }
}

@Composable
private fun RemoteIconButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .testTag(testTag)
            .clickable(enabled = enabled, onClick = onClick),
        color = if (enabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = CircleShape,
        border = BorderStroke(
            1.dp,
            if (enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
fun WatchTogetherDialog(
    sessionData: String,
    onDismiss: () -> Unit
) {
    val parts = sessionData.split("|", limit = 2)
    val code = parts.firstOrNull().orEmpty()
    val mediaTitle = parts.getOrNull(1).orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CYBER Watch Together", color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Schliessen")
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = mediaTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(164.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val cells = 11
                        val cellW = size.width / cells
                        val cellH = size.height / cells
                        val seed = code.hashCode()
                        for (r in 0 until cells) {
                            for (c in 0 until cells) {
                                val isFinder = (r < 3 && c < 3) || (r < 3 && c >= cells - 3) || (r >= cells - 3 && c < 3)
                                val bit = ((seed ushr ((r * cells + c) % 31)) xor (r * 7 + c * 13)) and 1 == 1
                                if (isFinder || bit) {
                                    drawRect(
                                        color = Color(0xFF050912),
                                        topLeft = Offset(c * cellW, r * cellH),
                                        size = Size(cellW * 0.92f, cellH * 0.92f)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Sitzungscode: $code",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Hinweis (TEIL 14): Jeder Teilnehmer benötigt einen eigenen legalen Zugang zur Plattform. Ohne API-Schnittstelle erfolgt keine vorgetäuschte Autosynchronisation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Sitzung beenden")
            }
        }
    )
}

fun openExternalUrl(context: Context, url: String, onError: (String) -> Unit) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        onError("Browser konnte URL nicht öffnen: ${e.localizedMessage}")
    }
}
