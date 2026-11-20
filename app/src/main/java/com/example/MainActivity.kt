package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.security.configureAppCheck
import com.example.ui.components.StatusBadge
import com.example.ui.components.WatchTogetherDialog
import com.example.ui.screens.CyberAiScreen
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MediaTvScreen
import com.example.ui.screens.SecurityCenterScreen
import com.example.ui.screens.SystemScreen
import com.example.ui.screens.UpdateCenterScreen
import com.example.ui.theme.CyberTvBoxTheme
import com.example.viewmodel.CyberViewModel

const val ROUTE_HOME = "home"
const val ROUTE_DEVICES = "devices"
const val ROUTE_UPDATES = "updates"
const val ROUTE_SECURITY = "security"
const val ROUTE_SYSTEM = "system"
const val ROUTE_FLASH = "flash_center"
const val ROUTE_MEDIA = "media_tv"
const val ROUTE_AI = "cyber_ai"

private data class CyberNavItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val primaryNavItems = listOf(
    CyberNavItem(ROUTE_HOME, R.string.nav_home, Icons.Rounded.Home, Icons.Outlined.Home, "nav_home"),
    CyberNavItem(ROUTE_DEVICES, R.string.nav_devices, Icons.Rounded.Devices, Icons.Outlined.Devices, "nav_devices"),
    CyberNavItem(ROUTE_UPDATES, R.string.nav_updates, Icons.Rounded.SystemUpdate, Icons.Outlined.SystemUpdate, "nav_updates"),
    CyberNavItem(ROUTE_SECURITY, R.string.nav_security, Icons.Rounded.Security, Icons.Outlined.Security, "nav_security"),
    CyberNavItem(ROUTE_SYSTEM, R.string.nav_system, Icons.Rounded.Tune, Icons.Outlined.Tune, "nav_system")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before UI or ViewModels.
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            val viewModel: CyberViewModel = viewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()

            CyberTvBoxTheme(
                darkTheme = settings.darkThemeEnabled,
                highContrast = settings.highContrastEnabled,
                largeText = settings.largeTextEnabled
            ) {
                CyberFlashAppRoot(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberFlashAppRoot(viewModel: CyberViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_HOME

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val platforms by viewModel.platforms.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val testMatrix by viewModel.testMatrix.collectAsStateWithLifecycle()
    val adBlockExceptions by viewModel.adBlockExceptions.collectAsStateWithLifecycle()
    val flashLogs by viewModel.flashLogs.collectAsStateWithLifecycle()
    val automations by viewModel.automations.collectAsStateWithLifecycle()
    val updatePackages by viewModel.updatePackages.collectAsStateWithLifecycle()
    val updateHistory by viewModel.updateHistory.collectAsStateWithLifecycle()
    val updateSources by viewModel.updateSources.collectAsStateWithLifecycle()
    val vulnerabilities by viewModel.vulnerabilities.collectAsStateWithLifecycle()
    val securityAudits by viewModel.securityAudits.collectAsStateWithLifecycle()
    val securityEvents by viewModel.securityEvents.collectAsStateWithLifecycle()
    val recoveryPoints by viewModel.recoveryPoints.collectAsStateWithLifecycle()
    val appSelfReport by viewModel.appSelfSecurityReport.collectAsStateWithLifecycle()
    val installProgress by viewModel.installProgress.collectAsStateWithLifecycle()
    val isSearchingUpdates by viewModel.isSearchingUpdates.collectAsStateWithLifecycle()
    val lastUpdateCheckEpochMs by viewModel.lastUpdateCheckEpochMs.collectAsStateWithLifecycle()
    val lastSecurityScanEpochMs by viewModel.lastSecurityScanEpochMs.collectAsStateWithLifecycle()

    val network by viewModel.networkTelemetry.collectAsStateWithLifecycle()
    val storage by viewModel.storageTelemetry.collectAsStateWithLifecycle()
    val usbBridges by viewModel.usbBridges.collectAsStateWithLifecycle()
    val bleNodes by viewModel.bleNodes.collectAsStateWithLifecycle()
    val isBleScanning by viewModel.isBleScanning.collectAsStateWithLifecycle()
    val bleStatusMessage by viewModel.bleStatusMessage.collectAsStateWithLifecycle()
    val inspectedFirmware by viewModel.inspectedFirmware.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val watchTogetherCode by viewModel.watchTogetherCode.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBanner) {
        statusBanner?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBanner()
        }
    }

    val effectiveOnline = network.isOnline && !settings.offlineModeForced
    val notifications = remember(network, settings, usbBridges, devices, updatePackages) {
        viewModel.buildNotifications()
    }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (currentRoute == ROUTE_AI) {
        CyberAiScreen(
            settings = settings,
            aiState = aiState,
            messages = aiMessages,
            onAcceptConsent = viewModel::setAiConsent,
            onSendPrompt = viewModel::sendAiPrompt,
            onBack = { navController.popBackStack() },
            onShowMessage = viewModel::showStatus
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        StatusBadge(
                            label = if (effectiveOnline) "ONLINE" else "OFFLINE",
                            statusType = if (effectiveOnline) "ONLINE" else "STANDBY"
                        )
                        IconButton(
                            onClick = { navigateTopLevel(ROUTE_MEDIA) },
                            modifier = Modifier.testTag("topbar_media_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LiveTv,
                                contentDescription = stringResource(R.string.cd_media_tv),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate(ROUTE_AI) },
                            modifier = Modifier.testTag("topbar_ai_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = stringResource(R.string.cd_cyber_ai),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        primaryNavItems.forEach { item ->
                            val selected = currentRoute == item.route || (item.route == ROUTE_UPDATES && currentRoute == ROUTE_FLASH)
                            val label = stringResource(item.labelRes)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(item.route) },
                                modifier = Modifier.testTag(item.testTag),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label, maxLines = 1) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        primaryNavItems.forEach { item ->
                            val selected = currentRoute == item.route || (item.route == ROUTE_UPDATES && currentRoute == ROUTE_FLASH)
                            val label = stringResource(item.labelRes)
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(item.route) },
                                modifier = Modifier.testTag(item.testTag),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_HOME,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 960.dp)
                    ) {
                        composable(ROUTE_HOME) {
                            HomeScreen(
                                settings = settings,
                                channels = channels,
                                platforms = platforms,
                                devices = devices,
                                updatePackages = updatePackages,
                                vulnerabilities = vulnerabilities,
                                network = network,
                                storage = storage,
                                notifications = notifications,
                                usbCount = usbBridges.size,
                                onToggleDisplayMode = viewModel::toggleDisplayMode,
                                onSelectTv = viewModel::selectActiveTv,
                                onSendRemoteAction = viewModel::sendRemoteAction,
                                onToggleWidget = viewModel::toggleWidgetVisibility,
                                onNavigateToMedia = { navigateTopLevel(ROUTE_MEDIA) },
                                onNavigateToUpdates = { navigateTopLevel(ROUTE_UPDATES) },
                                onNavigateToSecurity = { navigateTopLevel(ROUTE_SECURITY) },
                                onNavigateToFlash = { navController.navigate(ROUTE_FLASH) },
                                onNavigateToAi = { navController.navigate(ROUTE_AI) },
                                onShowMessage = viewModel::showStatus
                            )
                        }

                        composable(ROUTE_DEVICES) {
                            DevicesScreen(
                                settings = settings,
                                devices = devices,
                                updatePackages = updatePackages,
                                usbBridges = usbBridges,
                                bleNodes = bleNodes,
                                isBleScanning = isBleScanning,
                                bleStatusMessage = bleStatusMessage,
                                automations = automations,
                                onStartBleScan = viewModel::startBleScan,
                                onStopBleScan = viewModel::stopBleScan,
                                onPairBleNode = viewModel::pairBleCandidate,
                                onRefreshUsb = viewModel::refreshUsbBridges,
                                onSelectTv = viewModel::selectActiveTv,
                                onSendRemoteAction = viewModel::sendRemoteAction,
                                onUpdateDevice = viewModel::updateDeviceStatusAndEndpoint,
                                onAddModularDevice = viewModel::addModularDeviceProfile,
                                onAdjustVolume = viewModel::adjustDeviceVolume,
                                onToggleAutomation = viewModel::setAutomationEnabled,
                                onNavigateToUpdates = { navigateTopLevel(ROUTE_UPDATES) },
                                onShowMessage = viewModel::showStatus
                            )
                        }

                        composable(ROUTE_UPDATES) {
                            UpdateCenterScreen(
                                settings = settings,
                                devices = devices,
                                packages = updatePackages,
                                history = updateHistory,
                                sources = updateSources,
                                recoveryPoints = recoveryPoints,
                                network = network,
                                usbBridges = usbBridges,
                                inspectedFirmware = inspectedFirmware,
                                flashLogs = flashLogs,
                                testMatrix = testMatrix,
                                isSearchingUpdates = isSearchingUpdates,
                                lastUpdateCheckEpochMs = lastUpdateCheckEpochMs,
                                installProgress = installProgress,
                                startInFlashCenter = false,
                                onCheckForUpdates = viewModel::checkForUpdatesNow,
                                onInstallPackage = viewModel::installUpdatePackage,
                                onDismissInstallProgress = viewModel::dismissInstallProgress,
                                onSetSourceEnabled = viewModel::setUpdateSourceEnabled,
                                onSetChannel = viewModel::setUpdateChannel,
                                onSetAutoCheck = viewModel::setAutoUpdateCheck,
                                onSetNotifyUpdates = viewModel::setNotifyOnUpdates,
                                onSetWifiOnly = viewModel::setWifiOnlyDownloads,
                                onSetAllowCellular = viewModel::setAllowCellularDownloads,
                                onSetAskBeforeInstall = viewModel::setAskBeforeInstall,
                                onSetUpdateWindow = viewModel::setUpdateWindow,
                                onCreateRecoveryPoint = viewModel::createRecoveryPointForDevice,
                                onExecuteRollback = viewModel::executeRecoveryRollback,
                                onRefreshUsb = viewModel::refreshUsbBridges,
                                onInspectFirmwareUri = viewModel::inspectFirmwareUri,
                                onClearFlashLogs = viewModel::clearFlashLogs,
                                onUpdateTestMatrix = viewModel::updateTestMatrixStatus,
                                onShowMessage = viewModel::showStatus
                            )
                        }

                        composable(ROUTE_FLASH) {
                            UpdateCenterScreen(
                                settings = settings,
                                devices = devices,
                                packages = updatePackages,
                                history = updateHistory,
                                sources = updateSources,
                                recoveryPoints = recoveryPoints,
                                network = network,
                                usbBridges = usbBridges,
                                inspectedFirmware = inspectedFirmware,
                                flashLogs = flashLogs,
                                testMatrix = testMatrix,
                                isSearchingUpdates = isSearchingUpdates,
                                lastUpdateCheckEpochMs = lastUpdateCheckEpochMs,
                                installProgress = installProgress,
                                startInFlashCenter = true,
                                onCheckForUpdates = viewModel::checkForUpdatesNow,
                                onInstallPackage = viewModel::installUpdatePackage,
                                onDismissInstallProgress = viewModel::dismissInstallProgress,
                                onSetSourceEnabled = viewModel::setUpdateSourceEnabled,
                                onSetChannel = viewModel::setUpdateChannel,
                                onSetAutoCheck = viewModel::setAutoUpdateCheck,
                                onSetNotifyUpdates = viewModel::setNotifyOnUpdates,
                                onSetWifiOnly = viewModel::setWifiOnlyDownloads,
                                onSetAllowCellular = viewModel::setAllowCellularDownloads,
                                onSetAskBeforeInstall = viewModel::setAskBeforeInstall,
                                onSetUpdateWindow = viewModel::setUpdateWindow,
                                onCreateRecoveryPoint = viewModel::createRecoveryPointForDevice,
                                onExecuteRollback = viewModel::executeRecoveryRollback,
                                onRefreshUsb = viewModel::refreshUsbBridges,
                                onInspectFirmwareUri = viewModel::inspectFirmwareUri,
                                onClearFlashLogs = viewModel::clearFlashLogs,
                                onUpdateTestMatrix = viewModel::updateTestMatrixStatus,
                                onShowMessage = viewModel::showStatus
                            )
                        }

                        composable(ROUTE_SECURITY) {
                            SecurityCenterScreen(
                                devices = devices,
                                packages = updatePackages,
                                vulnerabilities = vulnerabilities,
                                audits = securityAudits,
                                events = securityEvents,
                                appSelfReport = appSelfReport,
                                lastScanEpochMs = lastSecurityScanEpochMs,
                                onRunFullSecurityAudit = viewModel::runFullSecurityAudit,
                                onAuthorizeDevice = viewModel::authorizeDevice,
                                onBlockDevice = viewModel::blockDevice,
                                onResetDevicePairing = viewModel::resetDevicePairing,
                                onTriggerEmergencyResponse = viewModel::triggerEmergencyResponse,
                                onJumpToUpdateCenter = { navigateTopLevel(ROUTE_UPDATES) }
                            )
                        }

                        composable(ROUTE_MEDIA) {
                            MediaTvScreen(
                                settings = settings,
                                platforms = platforms,
                                channels = channels,
                                devices = devices,
                                network = network,
                                onSelectTv = viewModel::selectActiveTv,
                                onTogglePlatformFav = viewModel::togglePlatformFavorite,
                                onTogglePlatformWatchlist = viewModel::togglePlatformWatchlist,
                                onVerifyPlatform = viewModel::verifyPlatformReachability,
                                onToggleChannelFav = viewModel::toggleChannelFavorite,
                                onMarkChannelWatched = viewModel::markChannelWatched,
                                onVerifyChannel = viewModel::verifyChannelReachability,
                                onCreateWatchTogether = viewModel::createWatchTogetherSession,
                                onShowMessage = viewModel::showStatus
                            )
                        }

                        composable(ROUTE_SYSTEM) {
                            SystemScreen(
                                settings = settings,
                                network = network,
                                storage = storage,
                                adBlockExceptions = adBlockExceptions,
                                onSetDarkTheme = viewModel::setDarkTheme,
                                onSetAdBlock = viewModel::setAdBlockEnabled,
                                onSetDnsTracking = viewModel::setDnsTrackingFilter,
                                onAddAdBlockException = viewModel::addAdBlockException,
                                onRemoveAdBlockException = viewModel::removeAdBlockException,
                                onSetCamLock = viewModel::setCamPrivacyLock,
                                onSetMicMute = viewModel::setMicPrivacyMute,
                                onSetOfflineForced = viewModel::setOfflineModeForced,
                                onEnableKidsMode = viewModel::enableKidsMode,
                                onUnlockKidsMode = viewModel::unlockKidsMode,
                                onSetHighContrast = viewModel::setHighContrast,
                                onSetLargeText = viewModel::setLargeText,
                                onSetReducedMotion = viewModel::setReducedMotion,
                                onSetPersonalization = viewModel::setPersonalization,
                                onWipeLocalPreferences = viewModel::wipeLocalPreferences,
                                onBuildBackupJson = viewModel::generateSafeBackupJson,
                                onShowMessage = viewModel::showStatus
                            )
                        }
                    }
                }
            }
        }
    }

    watchTogetherCode?.let { session ->
        WatchTogetherDialog(
            sessionData = session,
            onDismiss = viewModel::closeWatchTogetherSession
        )
    }
}
