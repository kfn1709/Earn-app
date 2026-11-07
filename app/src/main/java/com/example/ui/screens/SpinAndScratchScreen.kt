package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserWalletEntity
import com.example.ui.RewardViewModel
import com.example.ui.components.ScratchCardView
import com.example.ui.components.SpinWheelView
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SpinAndScratchScreen(
    viewModel: RewardViewModel,
    wallet: UserWalletEntity?,
    onNavigateToAds: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val isSpinning by viewModel.isSpinning.collectAsState()
    val targetDegree by viewModel.targetSpinDegree.collectAsState()
    val lastSpinWon by viewModel.lastSpinWon.collectAsState()
    val scratchSession by viewModel.scratchState.collectAsState()

    val spinsRemaining = wallet?.spinsLeftToday ?: 0
    val scratchRemaining = wallet?.scratchCardsLeftToday ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09120D)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Selector Row
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF102017),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = EmeraldPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = EmeraldPrimary,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("عجلة الحظ ($spinsRemaining)", fontWeight = FontWeight.Bold)
                            }
                        },
                        selectedContentColor = EmeraldPrimary,
                        unselectedContentColor = TextSecondary
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("بطاقة الحك ($scratchRemaining)", fontWeight = FontWeight.Bold)
                            }
                        },
                        selectedContentColor = GoldAccent,
                        unselectedContentColor = TextSecondary
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Lucky Spin Wheel Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102118)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "أدر عجلة الحظ واربح حتى 500 نقطة!",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "محاولاتك المتبقية اليوم: $spinsRemaining لفات مجانية",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldBright
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Animated Wheel
                        SpinWheelView(
                            targetDegree = targetDegree,
                            isSpinning = isSpinning,
                            onSpinClick = { viewModel.spinWheel() },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Won Prize Banner
                        if (lastSpinWon != null && !isSpinning) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F3621),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مبروك! فزت بـ +$lastSpinWon نقطة في هذه الدورة!",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Big Spin Button
                        Button(
                            onClick = { viewModel.spinWheel() },
                            enabled = !isSpinning && spinsRemaining > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("main_spin_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (spinsRemaining > 0) GoldAccent else Color(0xFF28362D),
                                contentColor = if (spinsRemaining > 0) Color(0xFF422B00) else TextMuted
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isSpinning -> "العجلة تدور الآن..."
                                    spinsRemaining > 0 -> "أدر العجلة واربح النقاط ($spinsRemaining متبقية)"
                                    else -> "نفدت محاولات اليوم"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        if (spinsRemaining == 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onNavigateToAds,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF142C1F),
                                    contentColor = EmeraldPrimary
                                )
                            ) {
                                Text("شاهد إعلانات لكسب لفات إضافية")
                            }
                        }
                    }
                }
            }
        } else {
            // Scratch Card Section
            item {
                ScratchCardView(
                    session = scratchSession,
                    cardsRemainingToday = scratchRemaining,
                    onCellClick = { cellId -> viewModel.scratchCell(cellId) },
                    onNewCardClick = { viewModel.initNewScratchCard() }
                )
            }
        }

        // Helpful Tip Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0C1912),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "نظام الحظ العادل 100%",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "كل دورة أو بطاقة حك مضمونة النقاط وتضاف تلقائياً لرصيدك القابل للسحب.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
