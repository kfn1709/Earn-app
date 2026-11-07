package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScratchCardSession
import com.example.ui.ScratchCell
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ScratchCardView(
    session: ScratchCardSession?,
    cardsRemainingToday: Int,
    onCellClick: (Int) -> Unit,
    onNewCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (session == null) return

    val cardBrush = Brush.verticalGradient(
        listOf(Color(0xFF1B3828), Color(0xFF0F2217), Color(0xFF09140E))
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(cardBrush)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "بطاقة الحظ الذهبية",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF234431)
                ) {
                    Text(
                        text = "متبقي: $cardsRemainingToday بطاقات",
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "اكشط 3 مربعات ذهبية لكشف جائزتك الفورية ومطابقتها!",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2 Rows x 3 Columns Scratch Cells Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val row1 = session.cells.take(3)
                val row2 = session.cells.drop(3)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row1.forEach { cell ->
                        ScratchCellItem(
                            cell = cell,
                            onClick = { onCellClick(cell.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row2.forEach { cell ->
                        ScratchCellItem(
                            cell = cell,
                            onClick = { onCellClick(cell.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Message or New Card CTA
            if (session.isFullyRevealed) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F3621),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "مبروك! تم حصد +${session.winPrize} نقطة إلى محفظتك!",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (cardsRemainingToday > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNewCardClick,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(40.dp)
                            .testTag("scratch_next_card_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color(0xFF00391C)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("بطاقة جديدة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScratchCellItem(
    cell: ScratchCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goldFoilBrush = Brush.linearGradient(
        listOf(
            Color(0xFFFFD54F),
            Color(0xFFFFB300),
            Color(0xFFFF8F00),
            Color(0xFFFFD54F)
        )
    )

    Box(
        modifier = modifier
            .height(84.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !cell.isRevealed) { onClick() }
            .testTag("scratch_cell_${cell.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (cell.isRevealed) {
            // Revealed Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D2218))
                    .border(1.5.dp, EmeraldPrimary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "+${cell.value}",
                        color = GoldBright,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "نقطة",
                        color = EmeraldPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        } else {
            // Unrevealed Gold Foil Surface
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(goldFoilBrush)
                    .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF5A3E00),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "اكشط",
                        color = Color(0xFF4E3400),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
