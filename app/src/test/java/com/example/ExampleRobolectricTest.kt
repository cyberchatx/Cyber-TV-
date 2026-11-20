package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CyberPreferences
import com.example.data.CyberSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.MessageDigest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches Cyber Flash`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Cyber Flash", appName)
    }

    @Test
    fun `seed data preserves CYBER STREAM FREE and all Swiss TV families without M3U links`() {
        val platforms = CyberSeedData.streamingPlatforms
        assertTrue(platforms.any { it.officialUrl == "https://cyber-stream-free.base44.app/" })
        assertTrue(platforms.size >= 14)

        val channels = CyberSeedData.liveTvChannels
        val requiredSwiss = listOf(
            "SRF 1", "SRF zwei", "SRF info", "RTS 1", "RTS 2", "RSI LA 1", "RSI LA 2",
            "RTR Telesguard", "TeleBärn", "Telebasel", "Tele M1", "Tele 1", "TELE TOP",
            "TVO", "Tele Südostschweiz", "TeleTicino", "Léman Bleu", "La Télé",
            "Canal Alpha", "Canal 9", "Canal B"
        )
        requiredSwiss.forEach { name ->
            assertTrue("Missing Swiss broadcaster: $name", channels.any { it.name == name })
        }
        // Verify no illegal .m3u or .m3u8 links exist in seed data
        assertFalse(channels.any { it.officialUrl.contains(".m3u", ignoreCase = true) })
    }

    @Test
    fun `hardware matrix strictly specifies A7670E Variant A and never SIM7670G`() {
        val box = CyberSeedData.hardwareCompatibilityMatrix.first { it.component == "CYBER BOX MASTER" }
        assertTrue(box.boardName.contains("A7670E-4G"))
        assertTrue(box.boardName.contains("Variante A"))
        assertTrue(box.mandatoryBoundaries.contains("NICHT SIM7670G"))
    }

    @Test
    fun `kids pin verification uses SHA-256 hash`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = CyberPreferences(context)
        val expectedHash = MessageDigest.getInstance("SHA-256")
            .digest("4321".toByteArray())
            .joinToString("") { "%02x".format(it) }
        assertTrue(prefs.verifyPin("4321", expectedHash))
        assertFalse(prefs.verifyPin("0000", expectedHash))
    }
}
