package com.example

import com.example.core.AppSelfSecurityReport
import com.example.core.SecurityScannerEngine
import com.example.core.UpdateManifestVerifier
import com.example.data.CyberSeedData
import com.example.data.CyberSettingsState
import com.example.data.TestStatusOption
import com.example.hardware.NetworkTelemetry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun seedPlatforms_preserveCyberStreamFreeAndLegalDirectory() {
        val platforms = CyberSeedData.streamingPlatforms
        assertTrue(platforms.any { it.officialUrl == "https://cyber-stream-free.base44.app/" })
        assertTrue(platforms.size >= 14)
        assertTrue(platforms.any { it.name == "Play SRF" })
        assertTrue(platforms.any { it.name == "Play Suisse" })
    }

    @Test
    fun seedChannels_includeAllSwissBroadcastersWithoutIllegalM3uLinks() {
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
        assertFalse(channels.any { it.officialUrl.contains(".m3u", ignoreCase = true) })
        assertTrue(channels.all { it.officialUrl.startsWith("https://") })
    }

    @Test
    fun hardwareMatrix_specifiesA7670EVariantA_andSeparatesAudioLimits() {
        val box = CyberSeedData.hardwareCompatibilityMatrix.first { it.component == "CYBER BOX MASTER" }
        assertTrue(box.boardName.contains("A7670E-4G"))
        assertTrue(box.boardName.contains("Variante A"))
        assertTrue(box.mandatoryBoundaries.contains("NICHT SIM7670G"))

        val cam = CyberSeedData.hardwareCompatibilityMatrix.first { it.component == "CYBER CAM" }
        assertTrue(cam.interfaces.contains("3W @ 4Ω"))
        assertFalse(cam.interfaces.contains("5W"))
    }

    @Test
    fun swissApns_includeAllSixCarriers() {
        val carriers = CyberSeedData.swissApnProfiles.map { it.carrier }
        assertEquals(6, carriers.size)
        assertTrue(carriers.contains("Swisscom"))
        assertTrue(carriers.contains("Sunrise"))
        assertTrue(carriers.contains("Salt"))
        assertTrue(carriers.contains("Digital Republic"))
    }

    @Test
    fun testMatrix_andBlueprints_arePopulated() {
        assertTrue(CyberSeedData.initialTestMatrix.isNotEmpty())
        assertTrue(
            CyberSeedData.initialTestMatrix.none {
                it.status == TestStatusOption.HARDWARETEST_BESTANDEN.code
            }
        )
        assertTrue(CyberSeedData.blueprints.size >= 5)
    }

    @Test
    fun sha256PinDigest_isDeterministicAndNonPlaintext() {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("4321".toByteArray())
            .joinToString("") { "%02x".format(it) }
        assertEquals(64, digest.length)
        assertFalse(digest.contains("4321"))
    }

    // --- Universal Update & Security Center Tests (Section 23) ---

    @Test
    fun manifestVerifier_acceptsValidSignedBoxPackage() {
        val box = CyberSeedData.initialDevices.first { it.id == "box_master" }
        val pkg = CyberSeedData.initialUpdatePackages.first { it.id == "pkg_box_esp32_901" }
        val res = UpdateManifestVerifier.verifyForDevice(
            pkg = pkg,
            targetDevice = box,
            settings = CyberSettingsState(),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN", isMetered = false)
        )
        assertTrue(res.isValid)
        assertEquals("VERIFIZIERT", res.statusCode)
    }

    @Test
    fun manifestVerifier_rejectsWrongHardwareAndSim7670gAndExternalTvClients() {
        val box = CyberSeedData.initialDevices.first { it.id == "box_master" }
        val pkg = CyberSeedData.initialUpdatePackages.first { it.id == "pkg_box_esp32_901" }

        val wrongHw = UpdateManifestVerifier.verifyForDevice(
            pkg = pkg.copy(hardwareVersion = "SIM7670G"),
            targetDevice = box,
            settings = CyberSettingsState(),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN")
        )
        assertFalse(wrongHw.isValid)
        assertEquals("INKOMPATIBEL", wrongHw.statusCode)

        val tvClient = CyberSeedData.initialDevices.first { it.id == "tv_1" }
        val tvAttempt = UpdateManifestVerifier.verifyForDevice(
            pkg = pkg.copy(deviceId = tvClient.id, hardwareVersion = tvClient.hardwareVersion),
            targetDevice = tvClient,
            settings = CyberSettingsState(),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN")
        )
        assertFalse(tvAttempt.isValid)
        assertEquals("INKOMPATIBEL", tvAttempt.statusCode)
    }

    @Test
    fun manifestVerifier_rejectsCorruptedChecksumOrUntrustedKey() {
        val box = CyberSeedData.initialDevices.first { it.id == "box_master" }
        val pkg = CyberSeedData.initialUpdatePackages.first { it.id == "pkg_box_esp32_901" }

        val badSha = UpdateManifestVerifier.verifyForDevice(
            pkg = pkg.copy(sha256Checksum = "invalid_short_checksum"),
            targetDevice = box,
            settings = CyberSettingsState(),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN")
        )
        assertFalse(badSha.isValid)
        assertEquals("SIGNATUR_FEHLER", badSha.statusCode)

        val badKey = UpdateManifestVerifier.verifyForDevice(
            pkg = pkg.copy(publisherKeyId = "ROGUE-KEY"),
            targetDevice = box,
            settings = CyberSettingsState(),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN")
        )
        assertFalse(badKey.isValid)
        assertEquals("SIGNATUR_FEHLER", badKey.statusCode)
    }

    @Test
    fun manifestVerifier_enforcesBetaApprovalAndWifiOnlyRule() {
        val box = CyberSeedData.initialDevices.first { it.id == "box_master" }
        val betaPkg = CyberSeedData.initialUpdatePackages.first { it.id == "pkg_box_beta_910" }

        val unapproved = UpdateManifestVerifier.verifyForDevice(
            pkg = betaPkg,
            targetDevice = box,
            settings = CyberSettingsState(betaChannelApproved = false),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN")
        )
        assertFalse(unapproved.isValid)

        val cellularBlocked = UpdateManifestVerifier.verifyForDevice(
            pkg = betaPkg,
            targetDevice = box,
            settings = CyberSettingsState(betaChannelApproved = true, wifiOnlyDownloads = true, allowCellularDownloads = false),
            network = NetworkTelemetry(isOnline = true, transportLabel = "4G / Mobilfunk", isMetered = true)
        )
        assertFalse(cellularBlocked.isValid)

        val approvedWifi = UpdateManifestVerifier.verifyForDevice(
            pkg = betaPkg,
            targetDevice = box,
            settings = CyberSettingsState(betaChannelApproved = true),
            network = NetworkTelemetry(isOnline = true, transportLabel = "WLAN", isMetered = false)
        )
        assertTrue(approvedWifi.isValid)
    }

    @Test
    fun securityScanner_neverMarksUncheckedOrOfflineDevicesAsProtected() {
        val devices = CyberSeedData.initialDevices
        assertTrue(devices.none { it.securityStatus == "GESCHUETZT" })

        val dummyAppReport = AppSelfSecurityReport(
            packageName = "com.aistudio.cyberflash.xqvrtm",
            versionName = "1.0",
            versionCode = 1L,
            signingCertSha256 = "AA:BB",
            hasDangerousBroadStoragePerm = false,
            hasInstallPackagesPerm = false,
            kidsPinHashed = true,
            overallStatus = "GESCHUETZT"
        )
        val (audits, devResults) = SecurityScannerEngine.runFullAudit(
            devices = devices,
            packages = CyberSeedData.initialUpdatePackages,
            vulnerabilities = CyberSeedData.initialVulnerabilities,
            settings = CyberSettingsState(),
            usbBridges = emptyList(),
            appReport = dummyAppReport
        )

        val modemAudit = audits.first { it.id == "audit_box_master_modem" }
        assertEquals("NICHT_UEBERPRUEFBAR", modemAudit.resultStatus)
        assertEquals("KRITISCH", devResults["box_master"]?.first)
        assertNotEquals("GESCHUETZT", devResults["cyber_cam"]?.first)

        val blockedDev = devices.first { it.id == "tv_3" }.copy(authStatus = "GESPERRT")
        val (_, blockedResult) = SecurityScannerEngine.runFullAudit(
            devices = listOf(blockedDev),
            packages = emptyList(),
            vulnerabilities = emptyList(),
            settings = CyberSettingsState(),
            usbBridges = emptyList(),
            appReport = dummyAppReport
        )
        assertEquals("SICHERHEITSRISIKO", blockedResult["tv_3"]?.first)
    }

    @Test
    fun modemPackageIsStrictlySeparatedFromEsp32Package() {
        val espPkg = CyberSeedData.initialUpdatePackages.first { it.id == "pkg_box_esp32_901" }
        val modemPkg = CyberSeedData.initialUpdatePackages.first { it.id == "pkg_box_modem_doc" }
        assertEquals("USB_UART", espPkg.installMethod)
        assertEquals("MODEM_AT_DOC", modemPkg.installMethod)
        assertNotEquals(espPkg.productId, modemPkg.productId)
    }
}
