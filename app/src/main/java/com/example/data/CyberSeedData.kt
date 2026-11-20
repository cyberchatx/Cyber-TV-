package com.example.data

object CyberSeedData {

    val streamingPlatforms: List<StreamingPlatformEntity> = listOf(
        StreamingPlatformEntity(
            id = "cyber_stream_free",
            name = "CYBER STREAM FREE",
            officialUrl = "https://cyber-stream-free.base44.app/",
            category = "CYBER Portal",
            country = "CH / Global",
            language = "DE / EN",
            isFree = true,
            loginNote = "Kein Pflicht-Login für freie Inhalte",
            supportedDevices = "TV Client Browser, Android, Web",
            isFavorite = true,
            inWatchlist = true
        ),
        StreamingPlatformEntity(
            id = "play_srf",
            name = "Play SRF",
            officialUrl = "https://www.srf.ch/play/tv",
            category = "Mediathek & Live",
            country = "Schweiz",
            language = "Deutsch",
            isFree = true,
            loginNote = "Freier Zugang in der Schweiz; optionales SRF-Konto für Merkliste",
            supportedDevices = "TV Client, Smart TV App, Browser",
            isFavorite = true,
            inWatchlist = true
        ),
        StreamingPlatformEntity(
            id = "play_rts",
            name = "Play RTS",
            officialUrl = "https://www.rts.ch/play/tv",
            category = "Mediathek & Live",
            country = "Schweiz",
            language = "Französisch",
            isFree = true,
            loginNote = "Offizielles SRG SSR Portal (Geoblocking bei Sport/Lizenzinhalten möglich)",
            supportedDevices = "TV Client, Smart TV App, Browser"
        ),
        StreamingPlatformEntity(
            id = "play_rsi",
            name = "Play RSI",
            officialUrl = "https://www.rsi.ch/play/tv",
            category = "Mediathek & Live",
            country = "Schweiz",
            language = "Italienisch",
            isFree = true,
            loginNote = "Offizielles SRG SSR Portal",
            supportedDevices = "TV Client, Smart TV App, Browser"
        ),
        StreamingPlatformEntity(
            id = "play_rtr",
            name = "Play RTR",
            officialUrl = "https://www.rtr.ch/play/tv",
            category = "Mediathek & Live",
            country = "Schweiz",
            language = "Rätoromanisch",
            isFree = true,
            loginNote = "Offizielles SRG SSR Portal",
            supportedDevices = "TV Client, Browser"
        ),
        StreamingPlatformEntity(
            id = "play_suisse",
            name = "Play Suisse",
            officialUrl = "https://www.playsuisse.ch/",
            category = "Schweizer Filme & Serien",
            country = "Schweiz",
            language = "DE / FR / IT / RM",
            isFree = true,
            loginNote = "Kostenloses SRG-SSR-Konto erforderlich (Schweizer Wohnsitz)",
            supportedDevices = "TV Client, Smart TV, Browser",
            isFavorite = true
        ),
        StreamingPlatformEntity(
            id = "arte",
            name = "ARTE.tv",
            officialUrl = "https://www.arte.tv/de/",
            category = "Kultur & Doku",
            country = "Europa (DE/FR)",
            language = "DE / FR",
            isFree = true,
            loginNote = "Kostenlos ohne Konto nutzbar",
            supportedDevices = "TV Client, Smart TV, Browser",
            isFavorite = true
        ),
        StreamingPlatformEntity(
            id = "ard_mediathek",
            name = "ARD Mediathek",
            officialUrl = "https://www.ardmediathek.de/",
            category = "Mediathek & Live",
            country = "Deutschland",
            language = "Deutsch",
            isFree = true,
            loginNote = "Freier Zugang (einzelne Inhalte in CH ggf. geogeblockt)",
            supportedDevices = "TV Client, Smart TV, Browser"
        ),
        StreamingPlatformEntity(
            id = "zdf",
            name = "ZDF Mediathek",
            officialUrl = "https://www.zdf.de/",
            category = "Mediathek & Live",
            country = "Deutschland",
            language = "Deutsch",
            isFree = true,
            loginNote = "Freier Zugang (einzelne Inhalte in CH ggf. geogeblockt)",
            supportedDevices = "TV Client, Smart TV, Browser"
        ),
        StreamingPlatformEntity(
            id = "youtube",
            name = "YouTube",
            officialUrl = "https://www.youtube.com/",
            category = "Video & Channels",
            country = "International",
            language = "Mehrsprachig",
            isFree = true,
            loginNote = "Läuft auf kompatiblem TV Client / Browser (nicht nativ auf ESP32-S3)",
            supportedDevices = "TV Client, Smart TV, Android",
            isFavorite = true
        ),
        StreamingPlatformEntity(
            id = "pluto_tv",
            name = "Pluto TV",
            officialUrl = "https://pluto.tv/",
            category = "FAST Channels & VOD",
            country = "DACH / International",
            language = "DE / EN",
            isFree = true,
            loginNote = "Werbefinanziert, kein Pflicht-Konto",
            supportedDevices = "TV Client, Smart TV, Browser"
        ),
        StreamingPlatformEntity(
            id = "rakuten_tv",
            name = "Rakuten TV",
            officialUrl = "https://www.rakuten.tv/",
            category = "FAST & Store",
            country = "Europa",
            language = "DE / EN / FR / IT",
            isFree = false,
            loginNote = "Freie FAST-Kanäle + kostenpflichtige Leih-/Kauffilme",
            supportedDevices = "TV Client, Smart TV, Browser"
        ),
        StreamingPlatformEntity(
            id = "joyn",
            name = "Joyn",
            officialUrl = "https://www.joyn.ch/",
            category = "Live-TV & Mediathek",
            country = "DACH",
            language = "Deutsch",
            isFree = false,
            loginNote = "Basisbereich kostenlos mit Konto; Plus+ kostenpflichtig",
            supportedDevices = "TV Client, Smart TV, Browser"
        ),
        StreamingPlatformEntity(
            id = "netflix",
            name = "Netflix",
            officialUrl = "https://www.netflix.com/",
            category = "Abo-Streaming (DRM)",
            country = "International",
            language = "Mehrsprachig",
            isFree = false,
            loginNote = "Kostenpflichtiges Abo & Widevine-DRM-Client erforderlich (keine ESP32-Wiedergabe)",
            supportedDevices = "Zertifizierter TV Client / Smart TV / Browser"
        )
    )

    val liveTvChannels: List<LiveTvChannelEntity> = listOf(
        // SRG SSR National
        LiveTvChannelEntity(
            id = "srf_1", name = "SRF 1", country = "Schweiz", language = "Deutsch",
            category = "Unterhaltung", provider = "SRG SSR / SRF",
            officialUrl = "https://www.srf.ch/play/tv/live/srf-1",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Schweiz (Geoblocking im Ausland)",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play SRF",
            currentProgramTitle = "Tagesschau / Magazin", nextProgramTitle = "Schweiz aktuell",
            epgAvailable = true, replaySupported = true, isFavorite = true
        ),
        LiveTvChannelEntity(
            id = "srf_zwei", name = "SRF zwei", country = "Schweiz", language = "Deutsch",
            category = "Sport", provider = "SRG SSR / SRF",
            officialUrl = "https://www.srf.ch/play/tv/live/srf-zwei",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Schweiz (Sportrechte geogeblockt)",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play SRF",
            currentProgramTitle = "Sport Live / Serie", nextProgramTitle = "Spielfilm / Sportflash",
            epgAvailable = true, replaySupported = true, isFavorite = true
        ),
        LiveTvChannelEntity(
            id = "srf_info", name = "SRF info", country = "Schweiz", language = "Deutsch",
            category = "Nachrichten", provider = "SRG SSR / SRF",
            officialUrl = "https://www.srf.ch/play/tv/live/srf-info",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Teils weltweit bei Eigenproduktionen",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play SRF",
            currentProgramTitle = "SRF News Loop / Dokus", nextProgramTitle = "10 vor 10 Wiederholung",
            epgAvailable = true, replaySupported = true, isFavorite = true
        ),
        LiveTvChannelEntity(
            id = "rts_1", name = "RTS 1", country = "Schweiz", language = "Französisch",
            category = "Unterhaltung", provider = "SRG SSR / RTS",
            officialUrl = "https://www.rts.ch/play/tv/direct",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play RTS",
            currentProgramTitle = "Le 19h30 / Magazine", nextProgramTitle = "Cinéma / Série",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "rts_2", name = "RTS 2", country = "Schweiz", language = "Französisch",
            category = "Sport", provider = "SRG SSR / RTS",
            officialUrl = "https://www.rts.ch/play/tv/direct",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play RTS",
            currentProgramTitle = "RTS Sport / Culture", nextProgramTitle = "Documentaire",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "rsi_la_1", name = "RSI LA 1", country = "Schweiz", language = "Italienisch",
            category = "Unterhaltung", provider = "SRG SSR / RSI",
            officialUrl = "https://www.rsi.ch/play/tv/live",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play RSI",
            currentProgramTitle = "Telegiornale / Intrattenimento", nextProgramTitle = "Il Quotidiano",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "rsi_la_2", name = "RSI LA 2", country = "Schweiz", language = "Italienisch",
            category = "Sport", provider = "SRG SSR / RSI",
            officialUrl = "https://www.rsi.ch/play/tv/live",
            availabilityNote = "Offizieller Webplayer verfügbar", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play RSI",
            currentProgramTitle = "RSI Sport / Cultura", nextProgramTitle = "Film / Serie",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "rtr", name = "RTR Telesguard", country = "Schweiz", language = "Rätoromanisch",
            category = "Kultur", provider = "SRG SSR / RTR",
            officialUrl = "https://www.rtr.ch/play/tv",
            availabilityNote = "Live-Fenster auf SRF 1/info & Play RTR", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, Play RTR",
            currentProgramTitle = "Telesguard / Cuntrasts", nextProgramTitle = "Minisguard",
            epgAvailable = false, replaySupported = true
        ),
        // Swiss Regional TV
        LiveTvChannelEntity(
            id = "telebaern", name = "TeleBärn", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "CH Media",
            officialUrl = "https://www.telebaern.ch/live",
            availabilityNote = "Offizieller Livestream auf Senderwebsite", geoblockingNote = "Keine oder CH-regional",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "News / Region Bern", nextProgramTitle = "TalkTäglich",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "telebasel", name = "Telebasel", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "Stiftung Telebasel",
            officialUrl = "https://telebasel.ch/live/",
            availabilityNote = "Offizieller Livestream auf Senderwebsite", geoblockingNote = "Kein strenges Geoblocking",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Telebasel News", nextProgramTitle = "Glam / Talk",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "tele_m1", name = "Tele M1", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "CH Media",
            officialUrl = "https://www.telem1.ch/live",
            availabilityNote = "Offizieller Livestream auf Senderwebsite", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Aktuell Aargau/Solothurn", nextProgramTitle = "Tierisch",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "tele_1", name = "Tele 1", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "CH Media",
            officialUrl = "https://www.tele1.ch/live",
            availabilityNote = "Offizieller Livestream auf Senderwebsite", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Nachrichten Zentralschweiz", nextProgramTitle = "Unterwegs",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "tele_top", name = "TELE TOP", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "TOP-Medien",
            officialUrl = "https://www.toponline.ch/tele-top/",
            availabilityNote = "Offizielles Portal & Livestream prüfen", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "TOP NEWS", nextProgramTitle = "TOP TALK",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "tvo", name = "TVO", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "CH Media",
            officialUrl = "https://www.tvo-online.ch/live",
            availabilityNote = "Offizieller Livestream auf Senderwebsite", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "TVO Aktuell Ostschweiz", nextProgramTitle = "60 Minuten",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "tele_suedostschweiz", name = "Tele Südostschweiz", country = "Schweiz", language = "Deutsch",
            category = "Regional-TV", provider = "Somedia",
            officialUrl = "https://www.suedostschweiz.ch/tso",
            availabilityNote = "Offizielles Regionalportal", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Südostschweiz News", nextProgramTitle = "Regionalmagazin",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "teleticino", name = "TeleTicino", country = "Schweiz", language = "Italienisch",
            category = "Regional-TV", provider = "Gruppo Corriere del Ticino",
            officialUrl = "https://www.teleticino.ch/diretta",
            availabilityNote = "Offizieller Webstream prüfen", geoblockingNote = "Schweiz / Tessin",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Ticinonews", nextProgramTitle = "Fuorigioco",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "leman_bleu", name = "Léman Bleu", country = "Schweiz", language = "Französisch",
            category = "Regional-TV", provider = "Léman Bleu SA",
            officialUrl = "https://www.lemanbleu.ch/fr/Direct.html",
            availabilityNote = "Offizieller Genfer Livestream", geoblockingNote = "Weltweit meist frei",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Le Journal de Genève", nextProgramTitle = "Genève à Chaud",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "la_tele", name = "La Télé", country = "Schweiz", language = "Französisch",
            category = "Regional-TV", provider = "La Télé Vaud-Fribourg",
            officialUrl = "https://latele.ch/live",
            availabilityNote = "Offizieller Livestream auf Senderwebsite", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "L'Info VD/FR", nextProgramTitle = "Le Bistrot",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "canal_alpha", name = "Canal Alpha", country = "Schweiz", language = "Französisch",
            category = "Regional-TV", provider = "Canal Alpha",
            officialUrl = "https://www.canalalpha.ch/direct",
            availabilityNote = "Offizieller Arc-Jurassien-Stream", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Le Journal", nextProgramTitle = "Le Canal Sportif",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "canal_9", name = "Canal 9", country = "Schweiz", language = "Französisch",
            category = "Regional-TV", provider = "Canal9 / Kanal9",
            officialUrl = "https://canal9.ch/live/",
            availabilityNote = "Offizieller Walliser Livestream", geoblockingNote = "Schweiz",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Le Journal du Valais", nextProgramTitle = "Complément d'info",
            epgAvailable = false, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "canal_b", name = "Canal B", country = "Schweiz", language = "Französisch",
            category = "Regional-TV", provider = "Canal B / Broye",
            officialUrl = "https://www.canalb.ch/",
            availabilityNote = "Offizielle Website (Live-Status im Web prüfen)", geoblockingNote = "Unbekannt / Webportal",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Programm gemäss Webportal", nextProgramTitle = "EPG nicht verfügbar",
            epgAvailable = false, replaySupported = false
        ),
        // DACH & Europa Legal Public Streams
        LiveTvChannelEntity(
            id = "ard_das_erste", name = "Das Erste (ARD)", country = "Deutschland", language = "Deutsch",
            category = "Nachrichten", provider = "ARD",
            officialUrl = "https://www.ardmediathek.de/live",
            availabilityNote = "Offizieller ARD Live-Player", geoblockingNote = "Deutschland (EU/CH teils eingeschränkt)",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, ARD App",
            currentProgramTitle = "ARD Programm / Tagesschau", nextProgramTitle = "ARD Brennpunkt / Doku",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "zdf_live", name = "ZDF Live", country = "Deutschland", language = "Deutsch",
            category = "Dokumentation", provider = "ZDF",
            officialUrl = "https://www.zdf.de/live-tv",
            availabilityNote = "Offizieller ZDF Live-Player", geoblockingNote = "Deutschland (CH bei Eigenproduktionen frei)",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, ZDF App",
            currentProgramTitle = "ZDF heute / Terra X", nextProgramTitle = "ZDF Magazin",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "arte_live", name = "ARTE Live", country = "Europa", language = "Deutsch",
            category = "Kultur", provider = "ARTE GEIE",
            officialUrl = "https://www.arte.tv/de/live/",
            availabilityNote = "Offizieller ARTE Live-Player", geoblockingNote = "Europa (CH/DE/FR meist frei)",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, ARTE App",
            currentProgramTitle = "ARTE Dokumentation", nextProgramTitle = "ARTE Journal",
            epgAvailable = true, replaySupported = true, isFavorite = true
        ),
        LiveTvChannelEntity(
            id = "3sat_live", name = "3sat", country = "Europa", language = "Deutsch",
            category = "Bildung", provider = "ZDF / ORF / SRG / ARD",
            officialUrl = "https://www.3sat.de/programm/3sat-livestream-100.html",
            availabilityNote = "Offizieller 3sat Livestream", geoblockingNote = "DACH",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "nano / Kulturzeit", nextProgramTitle = "3sat Wissen Doku",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "orf_1", name = "ORF 1", country = "Österreich", language = "Deutsch",
            category = "Unterhaltung", provider = "ORF",
            officialUrl = "https://on.orf.at/",
            availabilityNote = "Offizielles ORF ON Portal", geoblockingNote = "Österreich (in CH stark geogeblockt)",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser, ORF ON",
            currentProgramTitle = "ORF ON Livefenster", nextProgramTitle = "ZIB Flash",
            epgAvailable = true, replaySupported = true
        ),
        LiveTvChannelEntity(
            id = "france_24", name = "France 24", country = "Frankreich", language = "Französisch",
            category = "Nachrichten", provider = "France Médias Monde",
            officialUrl = "https://www.france24.com/fr/direct",
            availabilityNote = "Offizieller internationaler Nachrichtenstream", geoblockingNote = "Weltweit frei",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Le Journal International", nextProgramTitle = "Le Débat",
            epgAvailable = false, replaySupported = false
        ),
        LiveTvChannelEntity(
            id = "euronews_de", name = "Euronews (DE)", country = "International", language = "Deutsch",
            category = "Wirtschaft", provider = "Euronews SA",
            officialUrl = "https://de.euronews.com/live",
            availabilityNote = "Offizieller Live-Nachrichtenstream", geoblockingNote = "Weltweit frei",
            isFree = true, accountRequired = false, supportedClients = "TV Client Browser",
            currentProgramTitle = "Euronews Live / Business", nextProgramTitle = "Global Conversation",
            epgAvailable = false, replaySupported = false
        )
    )

    val initialDevices: List<ManagedDeviceEntity> = listOf(
        ManagedDeviceEntity(
            id = "box_master",
            name = "CYBER BOX MASTER",
            deviceRole = "BOX_MASTER",
            hardwareModel = "Waveshare ESP32-S3-A7670E-4G (HW V2.0 Variante A)",
            roomName = "Technik / Rack",
            ipOrMac = "192.168.4.1 (CYBER-AP)",
            protocol = "WLAN + BLE 5.0 + 4G",
            firmwareVersion = "v9.0.0-blueprint",
            status = "STANDBY",
            hardwareVersion = "V2.0 Variante A",
            storageSummary = "16 MB Flash · 8 MB PSRAM · TF nicht geprüft",
            securityStatus = "NICHT_UEBERPRUEFT",
            authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "A7670E-Modem getrennt von ESP32-FW verwaltet (kein SIM7670G)"
        ),
        ManagedDeviceEntity(
            id = "cyber_display",
            name = "CYBER DISPLAY 7\"",
            deviceRole = "DISPLAY",
            hardwareModel = "Waveshare ESP32-S3-Touch-LCD-7B Rev B (1024×600, 5-Touch)",
            roomName = "Wohnzimmer",
            ipOrMac = "BLE: CYBER-DISP-7B",
            protocol = "BLE 5.0 + WLAN",
            firmwareVersion = "v9.0.0-lvgl",
            status = "STANDBY",
            hardwareVersion = "Rev B",
            storageSummary = "16 MB Flash · 8 MB OPI PSRAM",
            securityStatus = "NICHT_UEBERPRUEFT",
            authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "Touch-Treiber & LVGL-Modus bereit für Hardwareprüfung"
        ),
        ManagedDeviceEntity(
            id = "cyber_cam",
            name = "CYBER CAM",
            deviceRole = "CAM",
            hardwareModel = "Waveshare ESP32-S3-CAM-OV5640 (ES7210/ES8311/NS4150B)",
            roomName = "Wohnzimmer",
            ipOrMac = "Lokal / TF-Aufnahme",
            protocol = "WLAN + BLE",
            firmwareVersion = "v9.0.0-cam",
            status = "OFFLINE",
            hardwareVersion = "OV5640 Rev 1",
            storageSummary = "/CYBER_CAM/* TF-Schutz aktiv",
            securityStatus = "NICHT_UEBERPRUEFT",
            authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "Aufnahmeschutz aktiv; physische TF-Prüfung ausstehend"
        ),
        ManagedDeviceEntity(
            id = "cyber_os",
            name = "CYBER OS",
            deviceRole = "CYBER_OS",
            hardwareModel = "CYBER Modular Ecosystem Core",
            roomName = "Systemkern",
            ipOrMac = "CYBER-LINK v1",
            protocol = "WLAN + BLE",
            firmwareVersion = "v9.0.0",
            status = "ONLINE",
            hardwareVersion = "V9.0",
            storageSummary = "Systemmodule aktiv",
            securityStatus = "PRUEFUNG_ERFORDERLICH",
            authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "Sicherheitsprüfung verfügbar"
        ),
        ManagedDeviceEntity(
            id = "cyber_webhost",
            name = "CYBER WEBHOST",
            deviceRole = "WEBHOST",
            hardwareModel = "~/CYBER_TV_WEBHOST (Lokale Webverwaltung)",
            roomName = "Lokales LAN",
            ipOrMac = "http://192.168.4.1/",
            protocol = "WLAN",
            firmwareVersion = "v9.0.0-web",
            status = "STANDBY",
            hardwareVersion = "Multi-File HTML/JS",
            storageSummary = "Modulare Webdateien",
            securityStatus = "NICHT_UEBERPRUEFT",
            authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "Statische Ressourcen getrennt von Hardware-Backend"
        ),
        ManagedDeviceEntity(
            id = "cyber_flash_app",
            name = "CYBER FLASH APP",
            deviceRole = "APP_SELF",
            hardwareModel = "Android Steuerzentrale (API 24–36)",
            roomName = "Smartphone",
            ipOrMac = "Lokal (Android)",
            protocol = "ANDROID",
            firmwareVersion = "v1.0.0",
            status = "ONLINE",
            hardwareVersion = "Android APK",
            storageSummary = "Interner App-Speicher",
            securityStatus = "PRUEFUNG_ERFORDERLICH",
            authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "Selbstprüfung über Security Center verfügbar"
        ),
        ManagedDeviceEntity(
            id = "tv_1", name = "TV 1 · Wohnzimmer", deviceRole = "TV_CLIENT",
            hardwareModel = "Kompatibler Smart-TV / Streaming Client", roomName = "Wohnzimmer",
            ipOrMac = "192.168.1.101", protocol = "WLAN", firmwareVersion = "Client-Ready", status = "ONLINE",
            hardwareVersion = "Externer Client", storageSummary = "Herstellerverwaltet",
            securityStatus = "NICHT_UEBERPRUEFT", authStatus = "AUTORISIERT",
            lastDiagnosticsSummary = "Updates erfolgen ausschliesslich über Hersteller-OS"
        ),
        ManagedDeviceEntity(
            id = "tv_2", name = "TV 2 · Schlafzimmer", deviceRole = "TV_CLIENT",
            hardwareModel = "Streaming Client / Browser TV", roomName = "Schlafzimmer",
            ipOrMac = "192.168.1.102", protocol = "WLAN", firmwareVersion = "Client-Ready", status = "STANDBY",
            hardwareVersion = "Externer Client", storageSummary = "Herstellerverwaltet",
            securityStatus = "NICHT_UEBERPRUEFT", authStatus = "AUTORISIERT"
        ),
        ManagedDeviceEntity(
            id = "tv_3", name = "TV 3 · Gästezimmer", deviceRole = "TV_CLIENT",
            hardwareModel = "Streaming Client", roomName = "Gästezimmer",
            ipOrMac = "192.168.1.103", protocol = "WLAN", firmwareVersion = "Client-Ready", status = "OFFLINE",
            hardwareVersion = "Externer Client", storageSummary = "Herstellerverwaltet",
            securityStatus = "NICHT_UEBERPRUEFT", authStatus = "NICHT_AUTORISIERT"
        ),
        ManagedDeviceEntity(
            id = "tv_4", name = "TV 4 · Büro", deviceRole = "TV_CLIENT",
            hardwareModel = "Desktop / Browser TV Client", roomName = "Büro",
            ipOrMac = "192.168.1.104", protocol = "WLAN", firmwareVersion = "Client-Ready", status = "STANDBY",
            hardwareVersion = "Externer Client", storageSummary = "Herstellerverwaltet",
            securityStatus = "NICHT_UEBERPRUEFT", authStatus = "AUTORISIERT"
        ),
        ManagedDeviceEntity(
            id = "tv_5", name = "TV 5 · Studio", deviceRole = "TV_CLIENT",
            hardwareModel = "HDMI Streaming Client", roomName = "Studio",
            ipOrMac = "192.168.1.105", protocol = "WLAN", firmwareVersion = "Client-Ready", status = "OFFLINE",
            hardwareVersion = "Externer Client", storageSummary = "Herstellerverwaltet",
            securityStatus = "NICHT_UEBERPRUEFT", authStatus = "NICHT_AUTORISIERT"
        ),
        ManagedDeviceEntity(
            id = "tv_6", name = "TV 6 · Mobil", deviceRole = "TV_CLIENT",
            hardwareModel = "Tablet / Portabler TV Client", roomName = "Mobil",
            ipOrMac = "192.168.1.106", protocol = "WLAN", firmwareVersion = "Client-Ready", status = "OFFLINE",
            hardwareVersion = "Externer Client", storageSummary = "Herstellerverwaltet",
            securityStatus = "NICHT_UEBERPRUEFT", authStatus = "NICHT_AUTORISIERT"
        ),
        ManagedDeviceEntity(
            id = "switch_ctrl_1",
            name = "Switch 1 Controller",
            deviceRole = "CONTROLLER",
            hardwareModel = "Nintendo Switch 1 Controller (Pro / Joy-Con BLE HID)",
            roomName = "Wohnzimmer",
            ipOrMac = "BLE HID Pairing ausstehend",
            protocol = "BLE",
            firmwareVersion = "HID-Check erforderlich",
            status = "OFFLINE",
            hardwareVersion = "BLE HID",
            storageSummary = "N/A",
            securityStatus = "NICHT_UEBERPRUEFT",
            authStatus = "NICHT_AUTORISIERT"
        ),
        ManagedDeviceEntity(
            id = "smarthome_hub",
            name = "Lokaler Smart-Home Hub",
            deviceRole = "SMART_HOME",
            hardwareModel = "Home Assistant / MQTT Broker (LAN)",
            roomName = "Technik / Rack",
            ipOrMac = "mqtt://homeassistant.local:1883",
            protocol = "MQTT / REST",
            firmwareVersion = "Externer Hub",
            status = "STANDBY",
            hardwareVersion = "Externer Hub",
            storageSummary = "Lokal",
            securityStatus = "NICHT_UEBERPRUEFT",
            authStatus = "AUTORISIERT"
        )
    )

    val initialTestMatrix: List<TestMatrixItemEntity> = listOf(
        // BOX MASTER
        TestMatrixItemEntity("tm_box_boot", "BOX MASTER", "Boot & ESP32-S3R8 PSRAM Init (8 MB)", "IMPLEMENTIERT", "Blueprint-Code vorhanden; Hardwaretest auf Zielplatine erforderlich"),
        TestMatrixItemEntity("tm_box_a7670e", "BOX MASTER", "A7670E-4G Modem (V2.0 Variante A, kein SIM7670G)", "IMPLEMENTIERT", "AT-Befehls-Treiber im Blueprint; SIM- & Netztest offen"),
        TestMatrixItemEntity("tm_box_wlan", "BOX MASTER", "WLAN 2,4 GHz + SoftAP + CYBER LINK", "IMPLEMENTIERT", "CYBER LINK Frame-Validator in Android & ESP32-Vorlage"),
        TestMatrixItemEntity("tm_box_ble", "BOX MASTER", "BLE 5.0 Pairing & Steuerbefehle", "IMPLEMENTIERT", "Android BLE Scanner integriert; Hardware-Gegenstelle nötig"),
        TestMatrixItemEntity("tm_box_tf", "BOX MASTER", "TF-Kartenschnittstelle & Speichercheck", "NICHT_IMPLEMENTIERT", "Hardware-Pins anhand Waveshare V2.0 Schaltplan zu verifizieren"),
        // DISPLAY
        TestMatrixItemEntity("tm_disp_lcd", "DISPLAY", "7\" 1024×600 RGB-LCD + 5-Punkt Kapazitiv-Touch", "IMPLEMENTIERT", "LVGL-Touch-Blueprint inkl. TV/Remote-Umschaltung"),
        TestMatrixItemEntity("tm_disp_standalone", "DISPLAY", "Eigenständiger Start ohne BOX", "IMPLEMENTIERT", "Lokale Offline-Oberfläche definiert"),
        // CAM
        TestMatrixItemEntity("tm_cam_ov5640", "CAM", "OV5640 Foto/Video + unabhängige TF-Aufnahme", "IMPLEMENTIERT", "Verzeichnisstruktur /CYBER_CAM/* + Treiber-Blueprint"),
        TestMatrixItemEntity("tm_cam_audio", "CAM", "ES7210 Dual-Mic / ES8311 / NS4150B (max 3W @ 4Ω)", "IMPLEMENTIERT", "Modem-Audio strikt getrennt; HW-Pegeltest offen"),
        // STREAMING & LIVE TV
        TestMatrixItemEntity("tm_stream_portal", "STREAMING", "CYBER STREAM FREE & Offizielle Plattformen", "IMPLEMENTIERT", "14 Plattformen inkl. HTTP-Prüfung & Intent-Start"),
        TestMatrixItemEntity("tm_livetv_db", "LIVE TV", "Schweizer & EU Senderdatenbank (28 legale Sender)", "IMPLEMENTIERT", "Ohne illegale IPTV/M3U; mit Länder-/Sprachfiltern"),
        // AD BLOCK
        TestMatrixItemEntity("tm_adblock_dns", "AD BLOCK", "DNS/Tracking-Filter & Ausnahmenverwaltung", "IMPLEMENTIERT", "Room-Ausnahmeliste + transparente Grenzen"),
        // VIDEO CALL
        TestMatrixItemEntity("tm_vcall_local", "VIDEO CALL", "Lokaler Anruf-Status, Stummschaltung & CAM-Lock", "IMPLEMENTIERT", "Privacy-Indikatoren aktiv; Echtzeit-WebRTC-Test offen"),
        // MULTI-TV
        TestMatrixItemEntity("tm_multitv", "MULTI-TV", "TV 1 bis TV 6 Einzelverwaltung & IP-Zuweisung", "IMPLEMENTIERT", "Bis zu 6 Clients verwaltbar; Lasttest offen"),
        // CONTROLLER
        TestMatrixItemEntity("tm_ctrl_switch", "CONTROLLER", "Nintendo Switch 1 BLE-HID Erkennung & RGB-Check", "NICHT_IMPLEMENTIERT", "Exaktes Modell & RGB-Protokoll am Gerät zu prüfen"),
        // SMART HOME
        TestMatrixItemEntity("tm_sh_auto", "SMART HOME", "MQTT/HA-Bridge & Wenn-Dann-Automationen", "IMPLEMENTIERT", "Bestätigungspflicht für kritische Aktionen aktiv")
    )

    val initialAutomations: List<AutomationRuleEntity> = listOf(
        AutomationRuleEntity("auto_1", "TV-Start → CYBER HOME", "Wenn TV 1 startet", "CYBER HOME auf DISPLAY öffnen", isEnabled = true, requiresConfirmation = false),
        AutomationRuleEntity("auto_2", "CAM Aufnahme-Indikator", "Wenn CYBER CAM aufnimmt", "Roten Aufnahmehinweis auf DISPLAY & App zeigen", isEnabled = true, requiresConfirmation = false),
        AutomationRuleEntity("auto_3", "Internetausfall → Offline Mode", "Wenn Internetverbindung ausfällt", "Automatisch auf lokalen CYBER OFFLINE MODE wechseln", isEnabled = true, requiresConfirmation = false),
        AutomationRuleEntity("auto_4", "Sendungserinnerung", "5 Min vor Favoriten-Sendung", "Priorisierte Benachrichtigung anzeigen", isEnabled = false, requiresConfirmation = false),
        AutomationRuleEntity("auto_5", "Ruhemodus Standby", "Wenn Nacht-/Ruhemodus aktiviert", "Verbundene TV 1–6 in Standby setzen", isEnabled = false, requiresConfirmation = true)
    )

    val swissApnProfiles: List<SwissApnProfile> = listOf(
        SwissApnProfile("Swisscom", "gprs.swisscom.ch", "Keine / PAP", "Standard-APN für Swisscom 4G Daten-SIMs (PIN/PUK im A7670E-Modem verwalten)"),
        SwissApnProfile("Sunrise", "internet", "Keine", "Standard-APN für Sunrise / Yallo Mobilfunk"),
        SwissApnProfile("Salt", "internet", "Keine", "Standard-APN für Salt Mobile CH"),
        SwissApnProfile("Lidl Connect CH", "internet", "Keine", "Nutzt Salt-Netz; ggf. Roaming/APN gemäss SIM-Brief prüfen"),
        SwissApnProfile("Aldi Suisse Mobile", "internet", "Keine", "Nutzt Sunrise-Netz; APN vor Produktivbetrieb am A7670E prüfen"),
        SwissApnProfile("Digital Republic", "dr.m2m.ch / internet", "Keine", "IoT/Daten-SIM-Tarife: exakten Tarif-APN im Kundenportal prüfen")
    )

    val hardwareCompatibilityMatrix: List<HardwareSpecRow> = listOf(
        HardwareSpecRow(
            component = "CYBER BOX MASTER",
            boardName = "Waveshare ESP32-S3-A7670E-4G (HW V2.0 Variante A)",
            mcuAndMemory = "ESP32-S3R8 · 16 MB Flash · 8 MB PSRAM",
            interfaces = "A7670E-4G LTE · WLAN 2,4 GHz · BLE 5.0 · TF-Slot · USB-C",
            mandatoryBoundaries = "NICHT SIM7670G! Kein 4K-Transcoder, keine 5W-Audioausgabe, keine 6 garantierten HD-Streams."
        ),
        HardwareSpecRow(
            component = "CYBER DISPLAY",
            boardName = "Waveshare ESP32-S3-Touch-LCD-7B",
            mcuAndMemory = "ESP32-S3 · 16 MB Flash · 8 MB OPI PSRAM",
            interfaces = "7\" 1024×600 LCD · 5-Punkt Kapazitiv-Touch · WLAN 2,4 GHz · BLE 5.0 · TF",
            mandatoryBoundaries = "Echtes Touch-Bedien- & Fernbedienungsgerät. Kein passiver Monitor, kein Android-TV-Ersatz für DRM-Apps."
        ),
        HardwareSpecRow(
            component = "CYBER CAM",
            boardName = "Waveshare ESP32-S3-CAM-OV5640",
            mcuAndMemory = "ESP32-S3R8 · 16 MB Flash · 8 MB PSRAM",
            interfaces = "OV5640 Sensor · Dual-Mic · ES7210 · ES8311 · NS4150B (max. 3W @ 4Ω) · TF",
            mandatoryBoundaries = "Unabhängige lokale TF-Aufnahme (/CYBER_CAM/*) ohne BOX/WLAN. Kein TV-Tuner, keine heimliche Aktivierung."
        ),
        HardwareSpecRow(
            component = "TV CLIENTS (TV 1–6)",
            boardName = "Kompatible Smart-TVs, Mini-PCs, Android/Browser-Clients",
            mcuAndMemory = "Client-abhängig (4K / Widevine DRM)",
            interfaces = "WLAN / LAN / HDMI",
            mandatoryBoundaries = "Übernehmen Netflix, YouTube, DRM-Streaming & HD/4K-Live-TV-Wiedergabe."
        ),
        HardwareSpecRow(
            component = "CONTROLLER",
            boardName = "Nintendo Switch 1 Controller (Pro / Joy-Con)",
            mcuAndMemory = "Bluetooth HID",
            interfaces = "Bluetooth → CYBER DISPLAY → WLAN → Spiel-Client",
            mandatoryBoundaries = "RGB-Steuerung erst nach verifiziertem Modell- & Protokolltest freischalten."
        )
    )

    val competitorMatrix: List<CompetitorRow> = listOf(
        CompetitorRow("Offizielle Schweizer Live-TV Sender", "Ja (Abo)", "Ja (Basis frei/Abo)", "Über Apps", "IMPLEMENTIERT (28 Sender)", "Offizielle Web-Streams / Geoblocking je Sender"),
        CompetitorRow("Legales Streaming Center", "Eingeschränkt", "Nein", "Ja (Apps)", "IMPLEMENTIERT (14 Portale)", "Öffnung auf TV-Client (kein natives DRM auf ESP32)"),
        CompetitorRow("Modulare ESP32-S3 Touch-Fernbedienung", "Nein", "Nein", "Nur Handy-App", "IMPLEMENTIERT (Blueprint + App)", "Benötigt Waveshare Touch-LCD-7B"),
        CompetitorRow("Unabhängige lokale Kamera (TF)", "Nein", "Nein", "Nein", "IMPLEMENTIERT (Spec + Privacy)", "Benötigt ESP32-S3-CAM-OV5640"),
        CompetitorRow("DNS-Werbe- & Tracking-Filter", "Nein", "Nein", "Nein", "IMPLEMENTIERT (Ausnahmen)", "Kein Entfernen von In-Stream-Werbung oder DRM"),
        CompetitorRow("KI Bedienassistent (Cloud)", "Eingeschränkt", "Nein", "Siri / Google / Alexa", "IMPLEMENTIERT (Firebase AI)", "Benötigt Internet; ESP32 ist kein LLM-Server"),
        CompetitorRow("Offline-Funktionen", "Kaum", "Nein", "Stark limitiert", "IMPLEMENTIERT", "Internet-Streams im Offline-Modus deaktiviert"),
        CompetitorRow("Multi-TV Verwaltung (TV 1–6)", "Zusatzboxen nötig", "Multi-Stream Abo", "Multiroom begrenzt", "IMPLEMENTIERT", "6 verwaltete Clients ≠ 6 garantierte HD-Streams")
    )

    val blueprints: List<BlueprintArtifact> = listOf(
        BlueprintArtifact(
            id = "bp_cyber_link",
            title = "CYBER LINK Protokoll v1 (ESP32 + Android)",
            targetPath = "firmware/common/cyber_link_protocol.h",
            language = "C / Header",
            summary = "Einheitliches Nachrichtenformat über BLE (Steuerung) & WLAN (Streams/Dateien) inkl. Sequenznummer, Deduplizierung & Fehlercodes.",
            codeContent = """
#pragma once
#include <stdint.h>
#include <stdbool.h>

#define CYBER_LINK_VERSION 1
#define CYBER_MAX_PAYLOAD  512

typedef enum {
    CYBER_ROLE_BOX_MASTER = 1,
    CYBER_ROLE_DISPLAY    = 2,
    CYBER_ROLE_CAM        = 3,
    CYBER_ROLE_FLASH_APP  = 4,
    CYBER_ROLE_TV_CLIENT  = 5
} cyber_role_t;

typedef enum {
    CYBER_MSG_HEARTBEAT   = 0x01,
    CYBER_MSG_PAIR_REQ    = 0x02,
    CYBER_MSG_PAIR_ACK    = 0x03,
    CYBER_MSG_CMD_TV      = 0x10,
    CYBER_MSG_CMD_CAM     = 0x20,
    CYBER_MSG_TELEMETRY   = 0x30,
    CYBER_MSG_ERROR       = 0xFF
} cyber_msg_type_t;

typedef struct {
    uint8_t  protocol_version;
    char     device_id[24];
    uint8_t  sender_role;
    uint8_t  receiver_role;
    uint8_t  module_id;
    uint8_t  msg_type;
    uint32_t seq_num;
    uint16_t error_code;
    uint16_t payload_len;
    char     payload_json[CYBER_MAX_PAYLOAD];
} cyber_link_packet_t;
            """.trimIndent()
        ),
        BlueprintArtifact(
            id = "bp_box_master",
            title = "CYBER BOX MASTER Firmware (A7670E-4G V2.0 Var A)",
            targetPath = "firmware/box_master/src/main.cpp",
            language = "C++ (ESP-IDF / Arduino)",
            summary = "Zentrales Netzwerk-, 4G-AT-Modem-, Watchdog- & CYBER-LINK-Routing-Gerüst für Waveshare ESP32-S3-A7670E-4G V2.0 Variante A.",
            codeContent = """
// CYBER BOX MASTER - Waveshare ESP32-S3-A7670E-4G (HW V2.0 Variante A)
// HINWEIS: GPIO-Belegung vor dem Flashen zwingend mit dem V2.0-Schaltplan abgleichen.
// NICHT mit SIM7670G verwechseln! Kein Video-Transcoder, keine 5W-Audio-Ausgabe.
#include <Arduino.h>
#include <WiFi.h>
#include <esp_task_wdt.h>
#include "cyber_link_protocol.h"

static uint32_t g_last_seq = 0;

void initModemA7670E() {
    // UART-Pins gemäss Waveshare ESP32-S3-A7670E-4G V2.0 Variante A Datenblatt setzen
    Serial1.begin(115200);
    Serial1.println("AT");
    // AT+CGDCONT=1,"IP","<CARRIER_APN>" -> APN aus verifiziertem Profil verwenden
}

void setup() {
    Serial.begin(115200);
    esp_task_wdt_init(15, true);
    esp_task_wdt_add(NULL);

    WiFi.mode(WIFI_AP_STA);
    WiFi.softAP("CYBER-BOX-LINK");
    initModemA7670E();
    Serial.println("[CYBER BOX] Ready - A7670E V2.0 Var A");
}

void loop() {
    esp_task_wdt_reset();
    // CYBER LINK Packet-Check & TV 1-6 Heartbeat-Monitoring
    delay(250);
}
            """.trimIndent()
        ),
        BlueprintArtifact(
            id = "bp_display_7b",
            title = "CYBER DISPLAY Firmware (Touch-LCD-7B 1024×600)",
            targetPath = "firmware/display_7b/src/main.cpp",
            language = "C++ / LVGL",
            summary = "Eigenständige 7-Zoll-Touchscreen-Oberfläche (TV MODE & REMOTE MODE ohne Neustart) mit Offline-Fähigkeit.",
            codeContent = """
// CYBER DISPLAY - Waveshare ESP32-S3-Touch-LCD-7B (1024x600, 5-Punkt Kapazitiv)
// Echtes Fingerbedienungsgerät - startet eigenständig auch ohne BOX MASTER.
#include <Arduino.h>

enum CyberDisplayMode { MODE_TV = 0, MODE_REMOTE = 1 };
static CyberDisplayMode g_mode = MODE_TV;

void toggleDisplayMode() {
    g_mode = (g_mode == MODE_TV) ? MODE_REMOTE : MODE_TV;
    // LVGL Screen-Wechsel ohne Reboot ausführen
}

void setup() {
    Serial.begin(115200);
    // 1. RGB-Panel 1024x600 + OPI PSRAM (8 MB) initialisieren
    // 2. GT911 Kapazitiv-Touch (bis zu 5 Touchpunkte) kalibrieren
    // 3. LVGL Cyber Holo UI (Obsidian #050912 / Cyan #00E5FF) laden
    Serial.println("[CYBER DISPLAY] LVGL 1024x600 Touch Ready");
}

void loop() {
    // lv_timer_handler();
    delay(5);
}
            """.trimIndent()
        ),
        BlueprintArtifact(
            id = "bp_cam_ov5640",
            title = "CYBER CAM Firmware (OV5640 + ES7210/ES8311/NS4150B)",
            targetPath = "firmware/cam_ov5640/src/main.cpp",
            language = "C++",
            summary = "Unabhängige TF-Aufnahme (/CYBER_CAM/PHOTOS|VIDEOS|AUDIO|EXPORT|LOGS), sichtbarer Privacy-Indikator und max. 3W @ 4Ω Audio.",
            codeContent = """
// CYBER CAM - Waveshare ESP32-S3-CAM-OV5640
// Arbeitet unabhängig von der BOX und nimmt ohne WLAN lokal auf TF-Karte auf.
// Audio: Dual-Mic -> ES7210 (Record), ES8311 -> NS4150B (max. 3W @ 4 Ohm gem. Datenblatt).
#include <Arduino.h>

static const char* CAM_DIRS[] = {
    "/CYBER_CAM/PHOTOS",
    "/CYBER_CAM/VIDEOS",
    "/CYBER_CAM/AUDIO",
    "/CYBER_CAM/EXPORT",
    "/CYBER_CAM/LOGS"
};

void setup() {
    Serial.begin(115200);
    // 1. TF-Karte mounten & Dateisystemgrösse dynamisch auslesen (keine erfundene Grösse)
    // 2. Verzeichnisse sicherstellen
    // 3. Sichtbare Status-LED für Kamera & Mikrofon aktivieren (kein verdecktes Recording)
    Serial.println("[CYBER CAM] Standalone Ready");
}

void loop() {
    delay(100);
}
            """.trimIndent()
        ),
        BlueprintArtifact(
            id = "bp_webhost",
            title = "CYBER WEBHOST Startseite (~/CYBER_TV_WEBHOST/index.html)",
            targetPath = "~/CYBER_TV_WEBHOST/index.html",
            language = "HTML / CSS / JS",
            summary = "Modulare lokale Weboberfläche (kein einzelnes GZIP-Archiv) für Geräteverwaltung, Live-TV-Links & Systemstatus.",
            codeContent = """
<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>CYBER WEBHOST · V9.0</title>
  <link rel="stylesheet" href="css/cyber-holo.css" />
</head>
<body class="obsidian-bg">
  <header class="holo-bar">
    <h1>CYBER TV BOX · WEBHOST</h1>
    <nav>
      <a href="index.html">Home</a>
      <a href="pages/devices.html">Geräte (TV 1–6)</a>
      <a href="pages/livetv.html">Live-TV</a>
      <a href="pages/flash.html">System &amp; Backup</a>
    </nav>
  </header>
  <main class="grid">
    <section class="holo-card">
      <h2>CYBER STREAM FREE</h2>
      <a class="btn-cyan" href="https://cyber-stream-free.base44.app/" target="_blank" rel="noopener">Portal öffnen</a>
    </section>
    <section class="holo-card">
      <h2>Lokaler Gerätestatus</h2>
      <div id="cyber-status">Verbunden über lokales LAN</div>
    </section>
  </main>
  <script src="js/cyber-link-ws.js"></script>
</body>
</html>
            """.trimIndent()
        )
    )

    val initialUpdateSources: List<UpdateSourceEntity> = listOf(
        UpdateSourceEntity(
            id = "src_cyber_repo",
            name = "Offizielles CYBER-Repository",
            sourceType = "OFFICIAL_REPO",
            endpointUrl = "https://cyberchatx.github.io/CYBER-TV/",
            isEnabled = true,
            isTrustedByDefault = true
        ),
        UpdateSourceEntity(
            id = "src_github",
            name = "GitHub Releases (CYBER-TV)",
            sourceType = "GITHUB_RELEASES",
            endpointUrl = "https://github.com/cyberchatx/CYBER-TV/releases",
            isEnabled = true,
            isTrustedByDefault = true
        ),
        UpdateSourceEntity(
            id = "src_webhost",
            name = "Lokaler CYBER WEBHOST (LAN)",
            sourceType = "WEBHOST",
            endpointUrl = "http://192.168.4.1/updates/manifest.json",
            isEnabled = true,
            isTrustedByDefault = false
        ),
        UpdateSourceEntity(
            id = "src_local_file",
            name = "Lokale Firmwaredateien (.bin / SAF)",
            sourceType = "LOCAL_STORAGE",
            endpointUrl = "content://android.storage/local",
            isEnabled = true,
            isTrustedByDefault = false
        ),
        UpdateSourceEntity(
            id = "src_sd_card",
            name = "SD-/TF-Karte (über SAF)",
            sourceType = "SD_CARD",
            endpointUrl = "content://android.storage/sdcard",
            isEnabled = true,
            isTrustedByDefault = false
        )
    )

    val initialUpdatePackages: List<UpdatePackageEntity> = listOf(
        UpdatePackageEntity(
            id = "pkg_box_esp32_901",
            productId = "CYBER-BOX-A7670E-ESP32",
            deviceId = "box_master",
            hardwareVersion = "V2.0 Variante A",
            firmwareVersion = "v9.0.1-sec1",
            minRequiredVersion = "v9.0.0-blueprint",
            channel = "STABLE",
            sourceName = "Offizielles CYBER-Repository",
            sourceUrl = "https://cyberchatx.github.io/CYBER-TV/",
            packageSizeBytes = 1_842_176L,
            sha256Checksum = "a8c4e91f0b3d72c5e61490a2b7c3e4f50192837465abcdef1234567890fedcba",
            signatureDigest = "ECDSA-P256-CYBER-PUB-01:verified-manifest",
            publisherKeyId = "CYBER-ROOT-2026-PUB",
            releaseDate = "2026-11-18",
            changelog = "• CYBER LINK Sequenznummer-Replay-Schutz verstärkt\n• Watchdog-Timeout-Recovery für ESP32-S3R8 optimiert\n• Getrennte A7670E AT-Modem-Statusabfrage",
            dependenciesText = "ESP32-S3R8 (16 MB Flash / 8 MB PSRAM) · Bootloader >= v2.0",
            installMethod = "USB_UART",
            isCriticalSecurity = true,
            isInstalled = false
        ),
        UpdatePackageEntity(
            id = "pkg_box_modem_doc",
            productId = "CYBER-BOX-A7670E-MODEM",
            deviceId = "box_master",
            hardwareVersion = "V2.0 Variante A",
            firmwareVersion = "A7670E-R2-DOC",
            minRequiredVersion = "A7670E-R1",
            channel = "STABLE",
            sourceName = "Offizielles CYBER-Repository",
            sourceUrl = "https://cyberchatx.github.io/CYBER-TV/",
            packageSizeBytes = 0L,
            sha256Checksum = "0000000000000000000000000000000000000000000000000000000000000000",
            signatureDigest = "DOC-ONLY-SEPARATE-MODEM",
            publisherKeyId = "CYBER-ROOT-2026-PUB",
            releaseDate = "2026-11-18",
            changelog = "Modemfirmware ist getrennt von ESP32-Firmware. Update nur über dokumentiertes SIMCom/Waveshare A7670E-Verfahren (nicht SIM7670G!).",
            dependenciesText = "Waveshare A7670E-4G V2.0 Variante A USB-Modemport",
            installMethod = "MODEM_AT_DOC",
            isCriticalSecurity = false,
            isInstalled = false
        ),
        UpdatePackageEntity(
            id = "pkg_display_901",
            productId = "CYBER-DISPLAY-7B",
            deviceId = "cyber_display",
            hardwareVersion = "Rev B",
            firmwareVersion = "v9.0.1-lvgl",
            minRequiredVersion = "v9.0.0-lvgl",
            channel = "STABLE",
            sourceName = "Offizielles CYBER-Repository",
            sourceUrl = "https://cyberchatx.github.io/CYBER-TV/",
            packageSizeBytes = 2_310_400L,
            sha256Checksum = "b4d2f81e9a7c6301f5e28d9c4b1a7f3e609182736455aabbccddeeff00112233",
            signatureDigest = "ECDSA-P256-CYBER-PUB-01:verified-manifest",
            publisherKeyId = "CYBER-ROOT-2026-PUB",
            releaseDate = "2026-11-18",
            changelog = "• GT911 5-Punkt Touch-Kalibrierung verbessert\n• Nahtloser Wechsel TV MODE / REMOTE MODE\n• BLE-Pairing-Auth gehärtet",
            dependenciesText = "Waveshare ESP32-S3-Touch-LCD-7B Rev B (8 MB OPI PSRAM)",
            installMethod = "WLAN_OTA",
            isCriticalSecurity = false,
            isInstalled = false
        ),
        UpdatePackageEntity(
            id = "pkg_cam_901",
            productId = "CYBER-CAM-OV5640",
            deviceId = "cyber_cam",
            hardwareVersion = "OV5640 Rev 1",
            firmwareVersion = "v9.0.1-cam",
            minRequiredVersion = "v9.0.0-cam",
            channel = "STABLE",
            sourceName = "Offizielles CYBER-Repository",
            sourceUrl = "https://cyberchatx.github.io/CYBER-TV/",
            packageSizeBytes = 1_620_992L,
            sha256Checksum = "c9f1a32b4d5e67890123456789abcdef0123456789abcdef9876543210fedcba",
            signatureDigest = "ECDSA-P256-CYBER-PUB-01:verified-manifest",
            publisherKeyId = "CYBER-ROOT-2026-PUB",
            releaseDate = "2026-11-18",
            changelog = "• Schutz für /CYBER_CAM/PHOTOS & /VIDEOS bei Updates\n• Sichtbarer Kamera-/Mic-Status-Lock\n• ES7210/ES8311 Pegelstabilisierung (max 3W @ 4 Ohm)",
            dependenciesText = "ESP32-S3-CAM-OV5640 + TF-Karte unangetastet",
            installMethod = "USB_UART",
            isCriticalSecurity = true,
            isInstalled = false
        ),
        UpdatePackageEntity(
            id = "pkg_webhost_901",
            productId = "CYBER-WEBHOST",
            deviceId = "cyber_webhost",
            hardwareVersion = "Multi-File HTML/JS",
            firmwareVersion = "v9.0.1-web",
            minRequiredVersion = "v9.0.0-web",
            channel = "STABLE",
            sourceName = "GitHub Releases (CYBER-TV)",
            sourceUrl = "https://github.com/cyberchatx/CYBER-TV/releases",
            packageSizeBytes = 248_320L,
            sha256Checksum = "d1e2f3a4b5c60718293a4b5c6d7e8f90112233445566778899aabbccddeeff00",
            signatureDigest = "ECDSA-P256-CYBER-PUB-01:verified-manifest",
            publisherKeyId = "CYBER-ROOT-2026-PUB",
            releaseDate = "2026-11-19",
            changelog = "• CSP-Header-Vorlagen & XSS-Sanitizing im Webhost\n• Neue Statusansicht für Update Center",
            dependenciesText = "~/CYBER_TV_WEBHOST Verzeichnis (keine einzelne GZIP-Datei)",
            installMethod = "WLAN_OTA",
            isCriticalSecurity = false,
            isInstalled = false
        ),
        UpdatePackageEntity(
            id = "pkg_box_beta_910",
            productId = "CYBER-BOX-A7670E-ESP32",
            deviceId = "box_master",
            hardwareVersion = "V2.0 Variante A",
            firmwareVersion = "v9.1.0-beta1",
            minRequiredVersion = "v9.0.0-blueprint",
            channel = "BETA",
            sourceName = "GitHub Releases (CYBER-TV)",
            sourceUrl = "https://github.com/cyberchatx/CYBER-TV/releases",
            packageSizeBytes = 1_905_400L,
            sha256Checksum = "e7f8a9b0c1d2e3f405162738495a6b7c8d9e0f1a2b3c4d5e6f708192a3b4c5d6",
            signatureDigest = "ECDSA-P256-CYBER-PUB-01:verified-manifest",
            publisherKeyId = "CYBER-ROOT-2026-PUB",
            releaseDate = "2026-11-19",
            changelog = "[BETA] Erweiterte BLE-MTU-Aushandlung & Watchdog-Diagnose-Telemetrie",
            dependenciesText = "Nur für Testumgebung (Freigabe für BETA-Kanal erforderlich)",
            installMethod = "USB_UART",
            isCriticalSecurity = false,
            isInstalled = false
        ),
        UpdatePackageEntity(
            id = "pkg_flash_app_101",
            productId = "CYBER-FLASH-APP-ANDROID",
            deviceId = "cyber_flash_app",
            hardwareVersion = "Android APK",
            firmwareVersion = "v1.0.1",
            minRequiredVersion = "v1.0.0",
            channel = "STABLE",
            sourceName = "Offizielles CYBER-Repository",
            sourceUrl = "https://cyberchatx.github.io/CYBER-TV/",
            packageSizeBytes = 12_400_000L,
            sha256Checksum = "f1a2b3c4d5e6f708192a3b4c5d6e7f8091a2b3c4d5e6f708192a3b4c5d6e7f80",
            signatureDigest = "ANDROID-APK-SIGNING-V3-EXTERNAL",
            publisherKeyId = "CYBER-ANDROID-REL",
            releaseDate = "2026-11-19",
            changelog = "• Regulärer Android-Update-Check über Release-Seite (keine Umgehung von Android-Sicherheitsmechanismen)",
            dependenciesText = "Android 7.0+ (API 24–36)",
            installMethod = "ANDROID_APK_INTENT",
            isCriticalSecurity = false,
            isInstalled = false
        )
    )

    val initialVulnerabilities: List<VulnerabilityEntity> = listOf(
        VulnerabilityEntity(
            id = "vuln_esp_link_replay",
            cveId = "CYBER-ADV-2026-001",
            affectedComponent = "CYBER BOX MASTER · CYBER LINK v1",
            affectedVersion = "< v9.0.1-sec1",
            description = "Fehlende strikte Sequenznummer- und Zeitstempelprüfung bei BLE/WLAN-Steuerpaketen in v9.0.0-blueprint.",
            severity = "KRITISCH",
            cvssScore = "7.4 (High - intern bewertet)",
            source = "CYBER Security Audit",
            publishedDate = "2026-11-18",
            fixPackageId = "pkg_box_esp32_901",
            recommendedAction = "Auf BOX MASTER Firmware v9.0.1-sec1 aktualisieren und Pairing prüfen.",
            status = "BESTAETIGT"
        ),
        VulnerabilityEntity(
            id = "vuln_cam_privacy_lock",
            cveId = "CYBER-ADV-2026-002",
            affectedComponent = "CYBER CAM (ESP32-S3-CAM-OV5640)",
            affectedVersion = "< v9.0.1-cam",
            description = "Ohne signierten Live-Stream-Token ist ein lokaler Kamera-Zugriff im Heimnetzwerk genauer abzusichern.",
            severity = "HOCH",
            cvssScore = "6.8 (Medium - intern bewertet)",
            source = "CYBER Security Audit",
            publishedDate = "2026-11-18",
            fixPackageId = "pkg_cam_901",
            recommendedAction = "CAM-Firmware v9.0.1-cam einspielen; Fernzugriff standardmässig deaktiviert lassen.",
            status = "BESTAETIGT"
        ),
        VulnerabilityEntity(
            id = "vuln_esp_wifi_wpa",
            cveId = "CVE-2023-52160 (Prüfhinweis)",
            affectedComponent = "WLAN-Clients / ESP32 Wi-Fi Supplicant",
            affectedVersion = "Hardware-/IDF-abhängig",
            description = "Bekannte Wi-Fi-Konfigurationsschwäche bei ungeprüfter CA-Zertifikatsprüfung in Enterprise-WLANs.",
            severity = "MITTEL",
            cvssScore = "6.5 (NVD/CVSS v3)",
            source = "NVD / Wi-Fi Alliance Hinweis",
            publishedDate = "2024-02-20",
            fixPackageId = "",
            recommendedAction = "Im Heim-WLAN WPA2/WPA3-PSK mit starkem Passwort nutzen; bei Enterprise-WLAN CA-Pinning prüfen.",
            status = "NICHT_UEBERPRUEFT"
        ),
        VulnerabilityEntity(
            id = "vuln_webhost_xss",
            cveId = "CYBER-ADV-2026-003",
            affectedComponent = "CYBER WEBHOST (~/CYBER_TV_WEBHOST)",
            affectedVersion = "< v9.0.1-web",
            description = "Empfehlung für strikte Content-Security-Policy (CSP) in lokalen HTML-Verwaltungsseiten.",
            severity = "NIEDRIG",
            cvssScore = "3.7 (Low - intern bewertet)",
            source = "CYBER Security Audit",
            publishedDate = "2026-11-19",
            fixPackageId = "pkg_webhost_901",
            recommendedAction = "CYBER WEBHOST v9.0.1-web einspielen.",
            status = "MOEGLICH"
        )
    )

    val initialRecoveryPoints: List<RecoveryPointEntity> = listOf(
        RecoveryPointEntity(
            id = 1,
            deviceId = "box_master",
            deviceName = "CYBER BOX MASTER",
            hardwareVersion = "V2.0 Variante A",
            firmwareVersion = "v9.0.0-blueprint",
            configSnapshotJson = "{\"role\":\"BOX_MASTER\",\"apnCheck\":\"manual\",\"cyberLink\":\"v1\"}",
            backupSha256 = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
            isRollbackSupported = true,
            rollbackNote = "Wiederherstellung per lokalem .bin-Image über USB-UART-Bootloader möglich."
        ),
        RecoveryPointEntity(
            id = 2,
            deviceId = "cyber_cam",
            deviceName = "CYBER CAM",
            hardwareVersion = "OV5640 Rev 1",
            firmwareVersion = "v9.0.0-cam",
            configSnapshotJson = "{\"role\":\"CAM\",\"tfDirsProtected\":[\"/CYBER_CAM/PHOTOS\",\"/CYBER_CAM/VIDEOS\"]}",
            backupSha256 = "4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce",
            isRollbackSupported = true,
            rollbackNote = "TF-Aufnahmen bleiben bei Recovery über USB-Bootloader vollständig erhalten."
        )
    )
}
