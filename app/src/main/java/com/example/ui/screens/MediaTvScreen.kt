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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.CyberSettingsState
import com.example.data.LiveTvChannelEntity
import com.example.data.ManagedDeviceEntity
import com.example.data.StreamingPlatformEntity
import com.example.hardware.NetworkTelemetry
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TvClientSelectorStrip
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMint
import com.example.ui.theme.ObsidianVoid

@Composable
fun MediaTvScreen(
    settings: CyberSettingsState,
    platforms: List<StreamingPlatformEntity>,
    channels: List<LiveTvChannelEntity>,
    devices: List<ManagedDeviceEntity>,
    network: NetworkTelemetry,
    onSelectTv: (String) -> Unit,
    onTogglePlatformFav: (StreamingPlatformEntity) -> Unit,
    onTogglePlatformWatchlist: (StreamingPlatformEntity) -> Unit,
    onVerifyPlatform: (StreamingPlatformEntity) -> Unit,
    onToggleChannelFav: (LiveTvChannelEntity) -> Unit,
    onMarkChannelWatched: (LiveTvChannelEntity) -> Unit,
    onVerifyChannel: (LiveTvChannelEntity) -> Unit,
    onCreateWatchTogether: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("Alle") }
    var selectedLanguage by remember { mutableStateOf("Alle") }
    var favoritesOnly by remember { mutableStateOf(false) }

    val effectiveOnline = network.isOnline && !settings.offlineModeForced
    val tvClients = remember(devices) { devices.filter { it.deviceRole == "TV_CLIENT" } }
    val activeTv = remember(tvClients, settings.activeTvId) {
        tvClients.firstOrNull { it.id == settings.activeTvId }
    }

    val tabs = listOf("Streaming", "Live-TV (${channels.size})", "Universal Search", "EPG & Replay")

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
                    modifier = Modifier.testTag("media_tab_$idx"),
                    text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        // Target TV Client Strip
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            TvClientSelectorStrip(tvClients = tvClients, activeTvId = settings.activeTvId, onSelectTv = onSelectTv)
        }

        when (selectedTab) {
            0 -> {
                // Streaming Center
                val visiblePlatforms = remember(platforms, settings.kidsModeEnabled) {
                    if (settings.kidsModeEnabled) platforms.filter { it.isFree && it.id != "netflix" } else platforms
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "CYBER INTERNET STREAMING CENTER",
                                subtitle = "Öffnungslogik: DISPLAY → BOX prüft Netz & Ziel-TV (${activeTv?.name ?: "TV 1"}) → TV-Client öffnet offizielle URL",
                                icon = Icons.Rounded.PlayCircle
                            )
                            if (!effectiveOnline) {
                                Spacer(Modifier.height(6.dp))
                                StatusBadge(label = "INTERNET OFFLINE", statusType = "OFFLINE")
                            }
                        }
                    }

                    items(visiblePlatforms, key = { it.id }) { p ->
                        val isCyberPortal = p.id == "cyber_stream_free"
                        CyberHoloCard(highlightBorder = isCyberPortal) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = p.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isCyberPortal) CyberCyan else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${p.category} · ${p.country} · ${p.language}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onTogglePlatformWatchlist(p) }) {
                                    Icon(
                                        imageVector = if (p.inWatchlist) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                        contentDescription = "Merkliste umschalten",
                                        tint = if (p.inWatchlist) CyberAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onTogglePlatformFav(p) }) {
                                    Icon(
                                        imageVector = if (p.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = "Favorit umschalten",
                                        tint = if (p.isFavorite) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(p.officialUrl, style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${if (p.isFree) "Kostenlos" else "Kostenpflichtig / Store"} · ${p.loginNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Status: ${p.lastCheckStatus} · Clients: ${p.supportedDevices}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberMint
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (!effectiveOnline) {
                                            onShowMessage("Offline-Modus: ${p.name} benötigt Internet.")
                                        } else {
                                            onShowMessage("An ${activeTv?.name ?: "Browser"} übermittelt: ${p.name}")
                                            openExternalUrl(context, p.officialUrl, onShowMessage)
                                        }
                                    },
                                    modifier = Modifier.testTag("open_platform_${p.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberCyan,
                                        contentColor = ObsidianVoid
                                    )
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Öffnen")
                                }
                                OutlinedButton(onClick = { onVerifyPlatform(p) }) {
                                    Icon(Icons.Rounded.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Prüfen")
                                }
                                OutlinedButton(onClick = { onCreateWatchTogether(p.name) }) {
                                    Icon(Icons.Rounded.Groups, contentDescription = "Watch Together", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Legal Live-TV Directory
                val countries = listOf("Alle", "Schweiz", "Deutschland", "Europa", "Österreich", "Frankreich", "International")
                val languages = listOf("Alle", "Deutsch", "Französisch", "Italienisch", "Rätoromanisch")
                val filtered = remember(channels, searchQuery, selectedCountry, selectedLanguage, favoritesOnly) {
                    channels.filter { ch ->
                        val matchesQuery = searchQuery.isBlank() ||
                            ch.name.contains(searchQuery, true) ||
                            ch.provider.contains(searchQuery, true) ||
                            ch.category.contains(searchQuery, true)
                        val matchesCountry = selectedCountry == "Alle" || ch.country == selectedCountry
                        val matchesLang = selectedLanguage == "Alle" || ch.language == selectedLanguage
                        val matchesFav = !favoritesOnly || ch.isFavorite
                        matchesQuery && matchesCountry && matchesLang && matchesFav
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Sender, Anbieter oder Kategorie suchen") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("livetv_search_input")
                        )
                        Spacer(Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                FilterChip(
                                    selected = favoritesOnly,
                                    onClick = { favoritesOnly = !favoritesOnly },
                                    label = { Text("Favoriten") }
                                )
                            }
                            items(countries) { c ->
                                FilterChip(
                                    selected = selectedCountry == c,
                                    onClick = { selectedCountry = c },
                                    label = { Text(c) }
                                )
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(languages) { l ->
                                FilterChip(
                                    selected = selectedLanguage == l,
                                    onClick = { selectedLanguage = l },
                                    label = { Text(l) }
                                )
                            }
                        }
                    }

                    items(filtered, key = { it.id }) { ch ->
                        CyberHoloCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ch.name, style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                                    Text(
                                        text = "${ch.country} · ${ch.language} · ${ch.category} · ${ch.provider}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onToggleChannelFav(ch) }) {
                                    Icon(
                                        imageVector = if (ch.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = "Sender-Favorit",
                                        tint = if (ch.isFavorite) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Geoblocking: ${ch.geoblockingNote}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberAmber
                            )
                            Text(
                                text = "Prüfstatus: ${ch.lastCheckStatus}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberMint
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (!effectiveOnline) {
                                            onShowMessage("Offline: Sender ${ch.name} benötigt Internet.")
                                        } else {
                                            onMarkChannelWatched(ch)
                                            openExternalUrl(context, ch.officialUrl, onShowMessage)
                                        }
                                    },
                                    modifier = Modifier.testTag("open_channel_${ch.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                                ) {
                                    Icon(Icons.Rounded.LiveTv, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Offizieller Live-Link")
                                }
                                OutlinedButton(onClick = { onVerifyChannel(ch) }) {
                                    Text("Link prüfen")
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Universal Search & Watchlist
                var uniQuery by remember { mutableStateOf("") }
                val matchedPlatforms = remember(platforms, uniQuery) {
                    if (uniQuery.isBlank()) platforms.filter { it.inWatchlist }
                    else platforms.filter {
                        it.name.contains(uniQuery, true) || it.category.contains(uniQuery, true)
                    }
                }
                val matchedChannels = remember(channels, uniQuery) {
                    if (uniQuery.isBlank()) channels.filter { it.isFavorite }
                    else channels.filter {
                        it.name.contains(uniQuery, true) ||
                            it.currentProgramTitle.contains(uniQuery, true) ||
                            it.category.contains(uniQuery, true)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = uniQuery,
                            onValueChange = { uniQuery = it },
                            label = { Text("CYBER UNIVERSAL SEARCH (Plattformen, Sender, Sendungen)") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("universal_search_input")
                        )
                    }
                    item {
                        SectionHeader(
                            title = if (uniQuery.isBlank()) "Merkliste (${matchedPlatforms.size})" else "Plattform-Treffer (${matchedPlatforms.size})",
                            icon = Icons.Rounded.Bookmark
                        )
                    }
                    items(matchedPlatforms, key = { "uni_p_${it.id}" }) { p ->
                        CyberHoloCard(onClick = {
                            if (effectiveOnline) openExternalUrl(context, p.officialUrl, onShowMessage)
                        }) {
                            Text(p.name, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Text("${p.category} · ${if (p.isFree) "Kostenlos" else "Kostenpflichtig"} · ${p.officialUrl}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    item {
                        SectionHeader(
                            title = if (uniQuery.isBlank()) "Sender-Favoriten (${matchedChannels.size})" else "Live-TV & EPG-Treffer (${matchedChannels.size})",
                            icon = Icons.Rounded.LiveTv
                        )
                    }
                    items(matchedChannels, key = { "uni_c_${it.id}" }) { ch ->
                        CyberHoloCard(onClick = {
                            if (effectiveOnline) openExternalUrl(context, ch.officialUrl, onShowMessage)
                        }) {
                            Text("${ch.name} (${ch.country})", style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Text("Aktuell: ${ch.currentProgramTitle} · ${ch.provider}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            3 -> {
                // EPG & Replay (TEIL 9 & TEIL 10)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        CyberHoloCard {
                            SectionHeader(
                                title = "CYBER EPG & REPLAY",
                                subtitle = "Fehlende EPG-Daten werden transparent als nicht verfügbar markiert; Replay nur bei offiziellem Anbietersupport.",
                                icon = Icons.Rounded.CheckCircle
                            )
                        }
                    }
                    items(channels, key = { "epg_${it.id}" }) { ch ->
                        CyberHoloCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(ch.name, style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                                StatusBadge(
                                    label = if (ch.epgAvailable) "EPG AKTIV" else "KEIN LIVE-EPG",
                                    statusType = if (ch.epgAvailable) "ONLINE" else "STANDBY"
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Jetzt: ${ch.currentProgramTitle}", style = MaterialTheme.typography.bodySmall)
                            Text("Danach: ${ch.nextProgramTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = {
                                        if (ch.replaySupported && effectiveOnline) {
                                            openExternalUrl(context, ch.officialUrl, onShowMessage)
                                        } else {
                                            onShowMessage("Replay für ${ch.name} nicht unterstützt oder offline.")
                                        }
                                    },
                                    label = { Text(if (ch.replaySupported) "Offizielles Replay öffnen" else "Replay nicht verfügbar") }
                                )
                                AssistChip(
                                    onClick = { onCreateWatchTogether("${ch.name}: ${ch.currentProgramTitle}") },
                                    label = { Text("Watch Together") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
