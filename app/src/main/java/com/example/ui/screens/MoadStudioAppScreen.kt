package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserWalletEntity
import com.example.ui.RewardViewModel
import com.example.ui.components.SpinWheelView
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

enum class MoadTab(val titleAr: String) {
    EARN("ربح النقاط"),
    GAMES("الألعاب والحظ"),
    WITHDRAW("سحب الأرباح")
}

@Composable
fun MoadStudioAppScreen(
    viewModel: RewardViewModel,
    wallet: UserWalletEntity?,
    onOpenAllAds: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(MoadTab.EARN) }
    var withdrawMethod by remember { mutableStateOf("CIH") }
    var accountNumber by remember { mutableStateOf("") }

    val isLoadingRewardedAd by viewModel.isLoadingRewardedAd.collectAsState()
    val isSpinning by viewModel.isSpinning.collectAsState()
    val targetDegree by viewModel.targetSpinDegree.collectAsState()
    val lastSpinWon by viewModel.lastSpinWon.collectAsState()

    val currentPoints = wallet?.points ?: 2450L
    val usdEquivalent = currentPoints / 1000.0

    // Pulsing animation for protected account badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyBg)
    ) {
        // Neon Glow Effects (Indigo at top center, Emerald at top right)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Indigo Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x336366F1), Color(0x006366F1)),
                    center = Offset(size.width * 0.5f, size.height * 0.1f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.5f, size.height * 0.1f)
            )

            // Emerald Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2210B981), Color(0x0010B981)),
                    center = Offset(size.width * 0.85f, size.height * 0.22f),
                    radius = size.width * 0.45f
                ),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.22f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Profile & Protected Account Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar & Greeting
                val currentUser by viewModel.currentUser.collectAsState()
                val isSyncing by viewModel.isSyncing.collectAsState()

                // Profile Avatar & Greeting
                val customName by viewModel.customProfileName.collectAsState()
                val displayName = currentUser?.displayName ?: customName ?: "Moad Studio"
                val avatarInitials = displayName.take(2).uppercase()

                // Profile Avatar & Greeting
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // MD Gradient Avatar
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(IndigoPrimary, VioletAccent)
                                )
                            )
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF030712)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = avatarInitials,
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "مرحباً بك،",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = displayName,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                    }
                }

                // Protected Account / Cloud Sync Badge + Logout Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (currentUser != null) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF6366F1).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentUser != null) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFF6366F1).copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.clickable {
                            if (currentUser == null) {
                                viewModel.signInWithGoogle()
                            } else {
                                viewModel.syncWalletWithCloud()
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentUser != null) Color(0xFF10B981).copy(alpha = pulseAlpha)
                                        else Color(0xFF818CF8).copy(alpha = pulseAlpha)
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isSyncing -> "جاري الحفظ..."
                                    currentUser != null -> "سحابي محمي ✓"
                                    else -> "حساب نشط"
                                },
                                color = if (currentUser != null) EmeraldLight else Color(0xFFA5B4FC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Logout Icon Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { viewModel.signOut() }
                            .testTag("app_logout_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "تسجيل الخروج",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Glowing Balance Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(26.dp)),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0F172A).copy(alpha = 0.95f),
                                    Color(0xFF030712),
                                    Color(0xFF0F172A).copy(alpha = 0.95f)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Points Section
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Wallet,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "الرصيد الإجمالي",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format("%,d", currentPoints),
                                        color = TextPrimary,
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-0.5).sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PTS",
                                        color = Color(0xFF818CF8),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }

                            // USD Equivalent
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "ما يعادل",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format("$%.2f", usdEquivalent),
                                    color = Color(0xFF34D399),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Card Footer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "معدل السحب: 1000 نقطة = $1",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Row(
                                modifier = Modifier
                                    .clickable { activeTab = MoadTab.WITHDRAW }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "سحب فوري",
                                    color = Color(0xFF818CF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF818CF8),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Tabs (ربح النقاط / الألعاب والحظ / سحب الأرباح)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MoadTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    if (isSelected) IndigoPrimary else Color.Transparent
                                )
                                .clickable { activeTab = tab }
                                .testTag("tab_${tab.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.titleAr,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content corresponding to selected Tab
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    MoadTab.EARN -> EarnTabContent(
                        viewModel = viewModel,
                        wallet = wallet,
                        isLoadingAd = isLoadingRewardedAd,
                        onWatchAdClick = {
                            (context as? Activity)?.let { activity ->
                                viewModel.showAdMobRewardedAd(activity)
                            }
                        }
                    )

                    MoadTab.GAMES -> GamesTabContent(
                        viewModel = viewModel,
                        isSpinning = isSpinning,
                        targetDegree = targetDegree,
                        lastSpinWon = lastSpinWon
                    )

                    MoadTab.WITHDRAW -> WithdrawTabContent(
                        viewModel = viewModel,
                        points = currentPoints,
                        withdrawMethod = withdrawMethod,
                        accountNumber = accountNumber,
                        onMethodChange = { withdrawMethod = it },
                        onAccountChange = { accountNumber = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EarnTabContent(
    viewModel: RewardViewModel,
    wallet: UserWalletEntity?,
    isLoadingAd: Boolean,
    onWatchAdClick: () -> Unit
) {
    val context = LocalContext.current
    val referralCode = wallet?.referralCode ?: "MOAD_2026"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Banner Ad / Rewarded Video Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, IndigoPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF312E81).copy(alpha = 0.5f),
                                Color(0xFF581C87).copy(alpha = 0.35f),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IndigoPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "إعلان مكافأة",
                                color = Color(0xFFA5B4FC),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "شاهد فيديو واكسب",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "+150 نقطة فورية لكل مشاهدة كاملة",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Play Rewarded Button
                    Button(
                        onClick = onWatchAdClick,
                        enabled = !isLoadingAd,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("moad_watch_ad_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IndigoPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        if (isLoadingAd) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Watch Ad",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Check-in Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "المكافأة اليومية",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "سجل دخولك كل 24 ساعة واحصل على هدية",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.claimDailyBonus() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color(0xFF030712)
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("claim_daily_200_btn")
                ) {
                    Text(
                        text = "استلم 200+",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Referral Feature Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "دعوة الأصدقاء",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "اربح 500 نقطة عن كل صديق يسجل بكودك",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Code box with copy button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF030712),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "كودك:",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = referralCode,
                                color = Color(0xFF818CF8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("كود دعوة Moad Studio", referralCode)
                                clipboard.setPrimaryClip(clip)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = TextPrimary
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("copy_moad_code_btn")
                        ) {
                            Text("نسخ الكود", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GamesTabContent(
    viewModel: RewardViewModel,
    isSpinning: Boolean,
    targetDegree: Float,
    lastSpinWon: Int?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.8f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(IndigoPrimary.copy(alpha = 0.12f))
                    .border(1.dp, IndigoPrimary.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = Color(0xFF818CF8),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "عجلة الحظ التفاعلية",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "جرب حظك واربح حتى 1000 نقطة في كل لفة",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Spin Wheel
            SpinWheelView(
                targetDegree = targetDegree,
                isSpinning = isSpinning,
                onSpinClick = { viewModel.spinWheel() },
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (lastSpinWon != null && !isSpinning) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF064E3B)
                ) {
                    Text(
                        text = "🎉 مبروك! ربحت +$lastSpinWon نقطة إضافية.",
                        color = Color(0xFFA7F3D0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = { viewModel.spinWheel() },
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("moad_spin_wheel_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndigoPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isSpinning) "العجلة تدور..." else "تدوير العجلة الآن",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun WithdrawTabContent(
    viewModel: RewardViewModel,
    points: Long,
    withdrawMethod: String,
    accountNumber: String,
    onMethodChange: (String) -> Unit,
    onAccountChange: (String) -> Unit
) {
    val methods = listOf("CIH", "BMCE", "PayPal", "USDT")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.8f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "سحب الأرباح الفوري",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Method Picker Chips
            Column {
                Text(
                    text = "اختر طريقة السحب",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    methods.forEach { m ->
                        val isSelected = withdrawMethod == m
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) IndigoPrimary.copy(alpha = 0.25f) else Color(0xFF030712)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) IndigoPrimary else DarkCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onMethodChange(m) }
                                .testTag("withdraw_method_$m"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = m,
                                color = if (isSelected) Color(0xFFA5B4FC) else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Account / RIB Text Field
            Column {
                Text(
                    text = if (withdrawMethod == "CIH" || withdrawMethod == "BMCE")
                        "رقم الحساب أو الـ RIB (24 رقم)"
                    else "البريد الإلكتروني / عنوان المحفظة",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = onAccountChange,
                    placeholder = {
                        Text(
                            text = if (withdrawMethod == "CIH") "230 450 ..." else "user@email.com",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("moad_account_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color(0xFF030712),
                        unfocusedContainerColor = Color(0xFF030712)
                    )
                )
            }

            // Confirm Payout Button
            Button(
                onClick = {
                    if (accountNumber.trim().isEmpty()) {
                        return@Button
                    }
                    val methodObj = viewModel.paymentMethods.find {
                        it.id.equals(withdrawMethod, ignoreCase = true) || it.nameEn.contains(withdrawMethod, ignoreCase = true)
                    } ?: viewModel.paymentMethods.first()

                    val option = methodObj.options.firstOrNull() ?: com.example.data.model.PayoutOption(1.0, 1000L)
                    viewModel.requestWithdrawal(methodObj, option, accountNumber) {}
                },
                enabled = points >= 1000L && accountNumber.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("moad_confirm_withdraw_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF059669),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF1E293B),
                    disabledContentColor = TextMuted
                )
            ) {
                Text(
                    text = if (points >= 1000L) "تأكيد سحب الأرباح ($1.00)" else "الحد الأدنى 1000 نقطة ($1.00)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
