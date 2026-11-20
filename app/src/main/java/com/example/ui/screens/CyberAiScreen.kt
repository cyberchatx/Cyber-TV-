package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ai.AiAssistantUiState
import com.example.ai.CyberChatMessage
import com.example.data.CyberSettingsState
import com.example.ui.components.CyberHoloCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCoral
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianVoid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberAiScreen(
    settings: CyberSettingsState,
    aiState: AiAssistantUiState,
    messages: List<CyberChatMessage>,
    onAcceptConsent: (Boolean) -> Unit,
    onSendPrompt: (String) -> Unit,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = spoken
                onSendPrompt(spoken)
            }
        }
    }

    val quickPrompts = listOf(
        "Welche Schweizer Nachrichten-Sender sind verfügbar?",
        "Erkläre CYBER LINK BLE vs WLAN",
        "Diagnostiziere Waveshare A7670E-4G V2.0 Var A",
        "Wie öffne ich Play SRF auf TV 2?"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = CyberCyan)
                        Spacer(Modifier.width(8.dp))
                        Text("CYBER AI ASSISTANT")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("ai_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            if (!settings.aiConsentAccepted) {
                CyberHoloCard(highlightBorder = true) {
                    Text("Datenschutz & KI-Verarbeitung (TEIL 12)", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Der ESP32-S3 ist kein lokaler KI-Server. Anfragen werden über Firebase AI Logic (Gemini 3.8 Flash) verarbeitet. Es werden keine SIM-PINs oder Passwörter übertragen.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { onAcceptConsent(true) },
                        modifier = Modifier.testTag("ai_consent_accept_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                    ) {
                        Text("Zustimmen & Cyber AI aktivieren")
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPrompts) { qp ->
                    AssistChip(
                        onClick = {
                            if (settings.aiConsentAccepted) {
                                onSendPrompt(qp)
                            } else {
                                onShowMessage("Bitte zuerst der KI-Verarbeitung zustimmen.")
                            }
                        },
                        label = { Text(qp) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (messages.isEmpty() && aiState is AiAssistantUiState.Idle) {
                    item {
                        CyberHoloCard {
                            Text("Bereit für Befehle", style = MaterialTheme.typography.titleSmall, color = CyberCyan)
                            Text(
                                text = "Nutze Text oder Push-to-Talk (Mikrofon-Button), um Sender zu finden, Hardware zu diagnostizieren oder Einstellungen zu klären.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    CyberHoloCard(highlightBorder = !msg.isUser) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (msg.isUser) "DU" else "CYBER AI",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.isUser) CyberAmber else CyberCyan
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(msg.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                when (aiState) {
                    is AiAssistantUiState.Loading -> {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyberCyan)
                                Spacer(Modifier.width(10.dp))
                                Text("CYBER AI analysiert...", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    is AiAssistantUiState.Streaming -> {
                        item {
                            CyberHoloCard(highlightBorder = true) {
                                StatusBadge(label = "STREAMING", statusType = "CYBER")
                                Spacer(Modifier.height(6.dp))
                                Text(aiState.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    is AiAssistantUiState.Error -> {
                        item {
                            CyberHoloCard {
                                StatusBadge(label = "KI FEHLER", statusType = "ALERT")
                                Spacer(Modifier.height(6.dp))
                                Text(aiState.message, style = MaterialTheme.typography.bodySmall, color = CyberCoral)
                            }
                        }
                    }

                    else -> Unit
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-CH")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "CYBER Voice Control (Push-to-Talk)")
                            }
                            voiceLauncher.launch(intent)
                        } catch (_: Exception) {
                            onShowMessage("Spracherkennung auf diesem Gerät nicht verfügbar.")
                        }
                    },
                    modifier = Modifier.testTag("ai_voice_ptt_button")
                ) {
                    Icon(Icons.Rounded.Mic, contentDescription = "Push-to-Talk Spracheingabe", tint = CyberCyan)
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Befehl eingeben…") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_prompt_input")
                )

                Button(
                    onClick = {
                        val toSend = inputText
                        inputText = ""
                        onSendPrompt(toSend)
                    },
                    enabled = inputText.isNotBlank() && settings.aiConsentAccepted,
                    modifier = Modifier.testTag("ai_send_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianVoid)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Senden")
                }
            }
        }
    }
}
