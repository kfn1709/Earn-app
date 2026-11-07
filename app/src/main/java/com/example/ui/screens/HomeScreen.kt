package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import android.app.Activity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserWalletEntity
import com.example.ui.RewardViewModel
import com.example.ui.components.CurrentBalanceAdMobCard
import com.example.ui.components.LivePayoutTicker
import com.example.ui.components.WalletCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: RewardViewModel,
    wallet: UserWalletEntity?,
    onNavigateToAds: () -> Unit,
    onNavigateToSpin: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoadingRewardedAd by viewModel.isLoadingRewardedAd.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09120D)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Visual Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 8.5f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_reward_banner),
                    contentDescription = "AdReward Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC09120D))
                            )
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent
                            ) {
                                Text(
                                    text = "المنصة الأولى للربح",
                                    color = Color(0xFF3E2C00),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "شاهد الإعلانات اليومية واكسب كاش حقيقي",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Community Payout Ticker
        item {
            LivePayoutTicker(proofs = viewModel.liveProofs)
        }

        // 'Current Balance' Card with 'Watch Ad' Button to trigger AdMob Rewarded Ad
        item {
            CurrentBalanceAdMobCard(
                currentPoints = wallet?.points ?: 0L,
                adsWatchedToday = wallet?.adsWatchedToday ?: 0,
                isLoadingAd = isLoadingRewardedAd,
                onWatchAdClick = {
                    (context as? Activity)?.let { activity ->
                        viewModel.showAdMobRewardedAd(activity)
                    }
                }
            )
        }

        // 7-Day Streak Daily Bonus Section
        item {
            DailyStreakCard(
                currentStreak = wallet?.dailyStreak ?: 1,
                hasClaimedToday = wallet?.hasClaimedToday ?: false,
                onClaimClick = { viewModel.claimDailyBonus() }
            )
        }

        // Quick Earn Ways Grid
        item {
            Text(
                text = "طرق الربح السريع",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickEarnActionCard(
                        title = "فيديوهات مكافأة",
                        reward = "+120 نقطة",
                        icon = Icons.Default.PlayCircle,
                        iconTint = EmeraldPrimary,
                        bgColor = Color(0xFF0F261B),
                        onClick = onNavigateToAds,
                        modifier = Modifier.weight(1f)
                    )
                    QuickEarnActionCard(
                        title = "عجلة الحظ",
                        reward = "حتى +500 نقطة",
                        icon = Icons.Default.AutoAwesome,
                        iconTint = GoldAccent,
                        bgColor = Color(0xFF26200F),
                        onClick = onNavigateToSpin,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickEarnActionCard(
                        title = "بطاقات الحك",
                        reward = "حتى +300 نقطة",
                        icon = Icons.Default.TouchApp,
                        iconTint = Color(0xFF40C4FF),
                        bgColor = Color(0xFF0F1E26),
                        onClick = onNavigateToSpin,
                        modifier = Modifier.weight(1f)
                    )
                    QuickEarnActionCard(
                        title = "دعوة الأصدقاء",
                        reward = "+500 نقطة",
                        icon = Icons.Default.Share,
                        iconTint = Color(0xFFFF5252),
                        bgColor = Color(0xFF261214),
                        onClick = { viewModel.shareInviteCode(context) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Referral Box CTA
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_referral_box"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "شارك كود دعوتك الخاص",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "كودك: ${wallet?.referralCode ?: "CASH789"} (+500 نقطة لكل صديق)",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldBright
                        )
                    }

                    Button(
                        onClick = { viewModel.shareInviteCode(context) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color(0xFF00391C)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("مشاركة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Earnings Potential Explainer
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0D1B13),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 نصيحة لمضاعفة أرباحك اليومية",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مشاهدة 15 إعلاناً يومياً + تدوير العجلة + إنهاء مهمتين كفيلة بوصولك للحد الأدنى للسحب (5$) كل 3 إلى 4 أيام! كما يمكنك دعوة 3 أصدقاء لسحب فوري.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DailyStreakCard(
    currentStreak: Int,
    hasClaimedToday: Boolean,
    onClaimClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111E16)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سلسلة تسجيل الدخول (7 أيام)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!hasClaimedToday) {
                    Button(
                        onClick = onClaimClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = Color(0xFF422B00)
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("claim_daily_btn")
                    ) {
                        Text("استلم مكافأة اليوم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1B3828)
                    ) {
                        Text(
                            text = "تم الاستلام اليوم ✓",
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7 Days Circles Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dayPrizes = listOf(50, 75, 100, 125, 150, 200, 350)
                for (day in 1..7) {
                    val isPast = day < currentStreak
                    val isCurrent = day == currentStreak
                    val isClaimed = isPast || (isCurrent && hasClaimedToday)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isClaimed -> EmeraldPrimary
                                        isCurrent -> GoldAccent.copy(alpha = 0.3f)
                                        else -> Color(0xFF1A2E22)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isCurrent) GoldAccent else Color.Transparent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isClaimed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF00391C),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "+${dayPrizes[day - 1]}",
                                    color = if (isCurrent) GoldBright else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يوم $day",
                            color = if (isCurrent) GoldAccent else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickEarnActionCard(
    title: String,
    reward: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("quick_earn_${title}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, iconTint.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = reward,
                style = MaterialTheme.typography.labelSmall,
                color = iconTint,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
