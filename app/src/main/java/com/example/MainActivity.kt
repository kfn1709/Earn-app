package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.ui.components.AdMobBanner
import com.google.android.gms.ads.MobileAds
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RewardViewModel
import com.example.ui.components.AdPlayerDialog
import com.example.ui.components.ConfettiCelebration
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MoadStudioAppScreen
import com.example.ui.screens.SpinAndScratchScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WatchAdsScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppScreen(val titleAr: String, val icon: ImageVector) {
    HOME("الرئيسية", Icons.Default.Home),
    ADS("الإعلانات", Icons.Default.PlayCircle),
    GAMES("ألعاب الحظ", Icons.Default.Casino),
    TASKS("المهام", Icons.Default.Assignment),
    WITHDRAW("السحب", Icons.Default.AccountBalanceWallet),
    LEADERBOARD("المتصدرون", Icons.Default.EmojiEvents)
}

class MainActivity : ComponentActivity() {
    private val viewModel: RewardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: RewardViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val wallet by viewModel.wallet.collectAsState()
    val adState by viewModel.adPlayerState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val celebrationTrigger by viewModel.celebrationTrigger.collectAsState()
    val payouts by viewModel.payouts.collectAsState()
    val watchedHistory by viewModel.watchedHistory.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val customProfileName by viewModel.customProfileName.collectAsState()
    val isGuestMode by viewModel.isGuestMode.collectAsState()

    val isAuthenticated = currentUser != null || customProfileName != null || isGuestMode
    var showOnboarding by remember { mutableStateOf(false) }

    // Auto-trigger onboarding once when user authenticates for the first time
    var hasShownOnboardingOnce by remember { mutableStateOf(false) }
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated && !hasShownOnboardingOnce) {
            showOnboarding = true
            hasShownOnboardingOnce = true
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    if (!isAuthenticated) {
        com.example.ui.screens.AuthGateScreen(viewModel = viewModel)
        return
    }

    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        currentScreen = AppScreen.HOME
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "AdReward",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentScreen.titleAr,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Points balance pill at top right
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { currentScreen = AppScreen.WITHDRAW }
                            .testTag("top_bar_balance_pill"),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF14291D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val pts = wallet?.points ?: 0L
                            Text(
                                text = String.format("%,d", pts),
                                color = GoldBright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Help / Guide Button
                    IconButton(
                        onClick = { showOnboarding = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14241B))
                            .testTag("top_bar_help_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "دليل الاستخدام",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Trophy / Leaderboard Icon Button
                    IconButton(
                        onClick = { currentScreen = AppScreen.LEADERBOARD },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentScreen == AppScreen.LEADERBOARD) GoldAccent.copy(alpha = 0.25f)
                                else Color(0xFF14241B)
                            )
                            .testTag("top_bar_leaderboard_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "قائمة المتصدرين",
                            tint = if (currentScreen == AppScreen.LEADERBOARD) GoldAccent else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF09120D),
                    titleContentColor = TextPrimary
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C1811))
            ) {
                // Google Mobile Ads (AdMob) Banner
                AdMobBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    onAdLoadedCallback = {
                        viewModel.onBannerLoadedReward()
                    }
                )

                NavigationBar(
                    containerColor = Color(0xFF0C1811),
                    contentColor = EmeraldPrimary,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.border(1.dp, DarkCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    val navItems = listOf(
                        AppScreen.HOME,
                        AppScreen.ADS,
                        AppScreen.GAMES,
                        AppScreen.TASKS,
                        AppScreen.WITHDRAW
                    )

                    navItems.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.titleAr,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.titleAr,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF00391C),
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> MoadStudioAppScreen(
                        viewModel = viewModel,
                        wallet = wallet,
                        onOpenAllAds = { currentScreen = AppScreen.ADS }
                    )

                    AppScreen.ADS -> {
                        val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity
                        val isLoadingRewardedAd by viewModel.isLoadingRewardedAd.collectAsState()
                        WatchAdsScreen(
                            campaigns = viewModel.adCampaigns,
                            wallet = wallet,
                            watchedHistory = watchedHistory,
                            onWatchClick = { campaign -> viewModel.startWatchingAd(campaign) },
                            onAdMobRewardedClick = {
                                activity?.let { viewModel.showAdMobRewardedAd(it) }
                            },
                            isLoadingRewardedAd = isLoadingRewardedAd
                        )
                    }

                    AppScreen.GAMES -> SpinAndScratchScreen(
                        viewModel = viewModel,
                        wallet = wallet,
                        onNavigateToAds = { currentScreen = AppScreen.ADS }
                    )

                    AppScreen.TASKS -> TasksScreen(
                        viewModel = viewModel,
                        wallet = wallet
                    )

                    AppScreen.WITHDRAW -> WithdrawScreen(
                        viewModel = viewModel,
                        wallet = wallet,
                        payoutHistory = payouts
                    )

                    AppScreen.LEADERBOARD -> LeaderboardScreen()
                }
            }

            // Confetti Particle Celebration Overlay
            ConfettiCelebration(triggerTime = celebrationTrigger)
        }
    }

    // Modal Interactive Ad Player
    AdPlayerDialog(
        adState = adState,
        onClaimReward = { viewModel.claimAdReward() },
        onClose = { viewModel.closeAdPlayer() },
        onInteractiveTap = { viewModel.onInteractiveAdTap() }
    )

    // Onboarding Guide Carousel Dialog
    if (showOnboarding) {
        com.example.ui.components.OnboardingDialog(
            onDismiss = { showOnboarding = false }
        )
    }
}
