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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserWalletEntity
import com.example.data.local.WatchedAdHistoryEntity
import com.example.data.model.AdCampaign
import com.example.data.model.AdType
import com.example.ui.components.CurrentBalanceAdMobCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WatchAdsScreen(
    campaigns: List<AdCampaign>,
    wallet: UserWalletEntity?,
    watchedHistory: List<WatchedAdHistoryEntity>,
    onWatchClick: (AdCampaign) -> Unit,
    onAdMobRewardedClick: () -> Unit = {},
    isLoadingRewardedAd: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<AdType?>(null) }
    val filteredCampaigns = if (selectedFilter == null) {
        campaigns
    } else {
        campaigns.filter { it.type == selectedFilter }
    }

    val watchedToday = wallet?.adsWatchedToday ?: 0
    val dailyLimit = 25
    val dailyProgress = (watchedToday.toFloat() / dailyLimit.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09120D)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 'Current Balance' Card & 'Watch Ad' Button to trigger AdMob rewarded ad
        item {
            CurrentBalanceAdMobCard(
                currentPoints = wallet?.points ?: 0L,
                adsWatchedToday = wallet?.adsWatchedToday ?: 0,
                isLoadingAd = isLoadingRewardedAd,
                onWatchAdClick = onAdMobRewardedClick
            )
        }

        // Header & Daily Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102117)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الإعلانات المتاحة اليوم",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1B3D2A)
                        ) {
                            Text(
                                text = "$watchedToday / $dailyLimit مشاهدة",
                                color = EmeraldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { dailyProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldPrimary,
                        trackColor = Color(0xFF1B3828)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "يتم تجديد باقة الإعلانات تلقائياً على مدار اليوم لتحقيق أقصى ربح ممكن.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("الكل") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color(0xFF00391C),
                            containerColor = Color(0xFF122218),
                            labelColor = TextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AdType.REWARDED_VIDEO,
                        onClick = { selectedFilter = AdType.REWARDED_VIDEO },
                        label = { Text("فيديوهات مكافأة") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color(0xFF00391C),
                            containerColor = Color(0xFF122218),
                            labelColor = TextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AdType.INTERSTITIAL,
                        onClick = { selectedFilter = AdType.INTERSTITIAL },
                        label = { Text("إعلانات سريعة (5ث)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color(0xFF00391C),
                            containerColor = Color(0xFF122218),
                            labelColor = TextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AdType.PLAYABLE_MINI,
                        onClick = { selectedFilter = AdType.PLAYABLE_MINI },
                        label = { Text("عروض تفاعلية") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color(0xFF00391C),
                            containerColor = Color(0xFF122218),
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Campaigns List
        items(filteredCampaigns) { campaign ->
            AdCampaignCard(
                campaign = campaign,
                onWatchClick = { onWatchClick(campaign) }
            )
        }

        // Watched History Section
        if (watchedHistory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سجل الإعلانات المشاهدة مؤخراً",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(watchedHistory.take(5)) { historyItem ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F1E16),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = historyItem.campaignTitle,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            val dateStr = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
                                .format(Date(historyItem.timestamp))
                            Text(
                                text = dateStr,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1A3828)
                        ) {
                            Text(
                                text = "+${historyItem.pointsEarned} نقطة",
                                color = GoldBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdCampaignCard(
    campaign: AdCampaign,
    onWatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ad_card_${campaign.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF112017)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (campaign.isHot) GoldAccent.copy(alpha = 0.5f) else DarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Optional banner preview if available
            if (campaign.bannerDrawableRes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = campaign.bannerDrawableRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Hot badge
                    if (campaign.isHot) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFF5252),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "مكافأة مضاعفة",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Top Row: Type Tag & Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(campaign.type.badgeColorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = campaign.type.titleAr,
                            color = Color(campaign.type.badgeColorHex),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "• ${campaign.sponsorName}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Duration Chip
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${campaign.durationSeconds} ثانية",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Description
            Text(
                text = campaign.titleAr,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = campaign.taglineAr,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reward Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${campaign.rewardPoints} نقطة",
                        color = GoldBright,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = onWatchClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF00391C)
                    ),
                    modifier = Modifier.testTag("watch_btn_${campaign.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاهدة الآن", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
