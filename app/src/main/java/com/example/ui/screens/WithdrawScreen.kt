package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PayoutTransactionEntity
import com.example.data.local.UserWalletEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.PayoutOption
import com.example.ui.RewardViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(
    viewModel: RewardViewModel,
    wallet: UserWalletEntity?,
    payoutHistory: List<PayoutTransactionEntity>,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var selectedOption by remember { mutableStateOf<PayoutOption?>(null) }
    var accountInput by remember { mutableStateOf("") }
    var isInputError by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentPoints = wallet?.points ?: 0L
    val usdBalance = currentPoints / 1000.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09120D)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Balance Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102218)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "رصيد الأرباح الجاهز للسحب",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("$%.2f USD", usdBalance),
                            style = MaterialTheme.typography.displayMedium,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1B3D2B)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%,d نقطة", currentPoints),
                                    color = GoldBright,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "الحد الأدنى لطلب السحب: 5,000 نقطة (5.00$ دولار). التحويل يتم خلال 24 ساعة بدون أي رسوم اقتطاع.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Methods Section Title
        item {
            Text(
                text = "اختر وسيلة استلام الأرباح",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Payment Methods List
        items(viewModel.paymentMethods) { method ->
            PaymentMethodCard(
                method = method,
                userPoints = currentPoints,
                onClick = {
                    selectedMethod = method
                    selectedOption = method.options.firstOrNull()
                    accountInput = ""
                    isInputError = false
                }
            )
        }

        // Payout Transactions History Section
        if (payoutHistory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سجل طلبات السحب الخاصة بك",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(payoutHistory) { tx ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF101F17),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${tx.methodNameAr} • $${tx.amountUsd.toInt()}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            // Status Chip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (tx.status == "COMPLETED") Color(0xFF173824) else Color(0xFF382C10)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (tx.status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (tx.status == "COMPLETED") EmeraldPrimary else GoldAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (tx.status == "COMPLETED") "تم التحويل" else "قيد المراجعة",
                                        color = if (tx.status == "COMPLETED") EmeraldPrimary else GoldAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الحساب: ${tx.accountInfo}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            val dateStr = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                .format(Date(tx.timestamp))
                            Text(
                                text = dateStr,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "رقم المعاملة: ${tx.id}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Trust & Guarantee Box
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0B1711),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "دفع آمن ومضمون بنسبة 100%: جميع السحوبات تخضع للتدقيق التلقائي للحماية من الاحتيال ويتم إرسالها لحسابك مباشرة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    // Modal BottomSheet for Payout Confirmation
    selectedMethod?.let { method ->
        ModalBottomSheet(
            onDismissRequest = { selectedMethod = null },
            sheetState = sheetState,
            containerColor = Color(0xFF0F1E16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Method Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(method.accentColorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForMethod(method.iconKey),
                            contentDescription = null,
                            tint = Color(method.accentColorHex),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = method.nameAr,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = method.descriptionAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Amount Options Chips
                Text(
                    text = "حدد قيمة السحب:",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    method.options.forEach { opt ->
                        val isSelected = selectedOption == opt
                        val canAfford = currentPoints >= opt.pointsRequired

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedOption = opt }
                                .testTag("payout_opt_${opt.usdAmount.toInt()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isSelected -> EmeraldPrimary
                                canAfford -> Color(0xFF162D20)
                                else -> Color(0xFF111E16)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EmeraldPrimary else DarkCardBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$${opt.usdAmount.toInt()}",
                                    color = if (isSelected) Color(0xFF00391C) else TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${opt.pointsRequired / 1000}k نقطة",
                                    color = if (isSelected) Color(0xFF00391C) else GoldAccent,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Account Destination Input Field
                Column {
                    Text(
                        text = method.inputLabelAr,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = accountInput,
                        onValueChange = {
                            accountInput = it
                            isInputError = false
                        },
                        placeholder = {
                            Text(method.inputPlaceholderAr, color = TextMuted)
                        },
                        isError = isInputError,
                        supportingText = if (isInputError) {
                            { Text("يرجى إدخال بيانات صحيحة", color = Color(0xFFFF5252)) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_account_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = EmeraldPrimary,
                            focusedContainerColor = Color(0xFF14261C),
                            unfocusedContainerColor = Color(0xFF14261C)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Submit Payout Button
                val opt = selectedOption ?: method.options.first()
                val hasEnoughPoints = currentPoints >= opt.pointsRequired

                Button(
                    onClick = {
                        if (accountInput.trim().length < 4) {
                            isInputError = true
                        } else {
                            viewModel.requestWithdrawal(method, opt, accountInput) {
                                selectedMethod = null
                            }
                        }
                    },
                    enabled = hasEnoughPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_withdraw_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF00391C),
                        disabledContainerColor = Color(0xFF20362A),
                        disabledContentColor = TextMuted
                    )
                ) {
                    Text(
                        text = if (hasEnoughPoints)
                            "تأكيد سحب $${opt.usdAmount.toInt()} (${opt.pointsRequired} نقطة)"
                        else "نقاطك غير كافية لهذا المبلغ (متبقي ${opt.pointsRequired - currentPoints})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    method: PaymentMethod,
    userPoints: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canRedeem = userPoints >= method.minPoints

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("method_card_${method.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF112017)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(method.accentColorHex).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForMethod(method.iconKey),
                    contentDescription = null,
                    tint = Color(method.accentColorHex),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.nameAr,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "الحد الأدنى: ${method.minPoints / 1000}$ (${method.minPoints} نقطة)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (canRedeem) EmeraldPrimary else TextMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (canRedeem) EmeraldPrimary else Color(0xFF1A3324)
            ) {
                Text(
                    text = if (canRedeem) "سحب الآن" else "متاح قريباً",
                    color = if (canRedeem) Color(0xFF00391C) else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun getIconForMethod(key: String): ImageVector = when (key) {
    "paypal" -> Icons.Default.MonetizationOn
    "wallet" -> Icons.Default.PhoneAndroid
    "crypto" -> Icons.Default.CurrencyBitcoin
    "play" -> Icons.Default.PlayArrow
    "phone" -> Icons.Default.PhoneAndroid
    "amazon" -> Icons.Default.ShoppingCart
    else -> Icons.Default.AccountBalance
}
