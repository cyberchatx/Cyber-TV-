package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "CyberAiService"

sealed interface AiAssistantUiState {
    data object Idle : AiAssistantUiState
    data object Loading : AiAssistantUiState
    data class Streaming(val text: String) : AiAssistantUiState
    data class Success(val text: String) : AiAssistantUiState
    data class Error(val message: String) : AiAssistantUiState
}

data class CyberChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestampMs: Long = System.currentTimeMillis()
)

class CyberAiAssistantService {

    private val systemPrompt = content {
        text(
            """
            Du bist der CYBER AI PERSONAL ASSISTANT für das modulare Ökosystem CYBER TV BOX V9.0 und die CYBER FLASH APP.
            Antworte präzise, technisch ehrlich, hilfsbereit und standardmässig auf Deutsch (ausser der Benutzer schreibt Englisch).
            
            VERBINDLICHE SYSTEMFAKTEN:
            1. CYBER BOX MASTER: Waveshare ESP32-S3-A7670E-4G (HW-Version 2.0, Variante A, ESP32-S3R8, 16 MB Flash, 8 MB PSRAM). NICHT mit SIM7670G verwechseln! Keine 5W-Audioausgabe, kein 4K-Videotranscoder.
            2. CYBER DISPLAY: Waveshare ESP32-S3-Touch-LCD-7B (7 Zoll, 1024x600, 5-Punkt Kapazitiv-Touch, 16 MB Flash, 8 MB OPI PSRAM). Eigenständiger Start ohne BOX, TV MODE & REMOTE MODE ohne Neustart.
            3. CYBER CAM: Waveshare ESP32-S3-CAM-OV5640 (Dual-Mic, ES7210 Record, ES8311 Playback, NS4150B max. 3W @ 4 Ohm, lokale TF-Aufnahme in /CYBER_CAM/* unabhängig von BOX/WLAN).
            4. TV CLIENTS (TV 1 bis TV 6): Übernehmen anspruchsvolle Wiedergabe (Netflix, YouTube, DRM, 4K, Live-TV-Webplayer).
            5. CYBER STREAM FREE: https://cyber-stream-free.base44.app/ sowie offizielle Schweizer/EU Plattformen (Play SRF, Play RTS, Play RSI, Play RTR, Play Suisse, ARTE, ARD, ZDF, Pluto TV, Rakuten TV, Joyn, Netflix, YouTube).
            6. CYBER LIVE TV: 28 legale Schweizer & DACH/EU Sender (SRF 1/zwei/info, RTS 1/2, RSI LA 1/2, RTR, TeleBärn, Telebasel, Tele M1, Tele 1, TELE TOP, TVO, Tele Südostschweiz, TeleTicino, Léman Bleu, La Télé, Canal Alpha, Canal 9, Canal B, ARD, ZDF, ARTE, 3sat, ORF 1, France 24, Euronews). Keine illegalen IPTV-Listen.
            7. GRENZEN: Keine DRM-Umgehung, keine erfundenen M3U-Links, ESP32-S3 ist kein lokaler LLM-Server, keine garantierten 6 parallelen HD-Streams, niemals Passwörter oder SIM-PINs ausgeben.
            """.trimIndent()
        )
    }

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = systemPrompt
        )
    }

    fun streamAssistantReply(
        userPrompt: String,
        systemContextSummary: String
    ): Flow<AiAssistantUiState> = flow {
        emit(AiAssistantUiState.Loading)
        val fullPrompt = buildString {
            appendLine("Aktueller Systemkontext der CYBER FLASH APP:")
            appendLine(systemContextSummary)
            appendLine()
            appendLine("Benutzeranfrage:")
            append(userPrompt)
        }
        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                model.generateContentStream(fullPrompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(AiAssistantUiState.Streaming(accumulated.toString()))
                    }
                }
            }
            if (accumulated.isNotBlank()) {
                emit(AiAssistantUiState.Success(accumulated.toString()))
            } else {
                emit(AiAssistantUiState.Error("Das KI-Modell hat eine leere Antwort zurückgegeben."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Cyber AI generation timed out after 60s", e)
            emit(AiAssistantUiState.Error("Zeitüberschreitung nach 60 Sekunden. Bitte erneut versuchen."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI Logic inference failed", e)
            emit(
                AiAssistantUiState.Error(
                    e.localizedMessage ?: "Firebase AI Logic Anfrage fehlgeschlagen."
                )
            )
        }
    }
}
