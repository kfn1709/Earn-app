package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class TopEarner(
    val rank: Int,
    val name: String,
    val countryFlag: String,
    val totalWithdrawnUsd: Double,
    val totalPoints: Long
)

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier
) {
    val topEarners = listOf(
        TopEarner(1, "سالم الكعبي", "🇸🇦", 345.0, 345000L),
        TopEarner(2, "محمد رضوان", "🇪🇬", 280.0, 280000L),
        TopEarner(3, "أحمد الدوسري", "🇰🇼", 225.0, 225000L),
        TopEarner(4, "ياسين المنصوري", "🇦🇪", 190.0, 190000L),
        TopEarner(5, "حمزة الشريف", "🇲🇦", 165.0, 165000L),
        TopEarner(6, "عمر الزهراني", "🇸🇦", 140.0, 140000L),
        TopEarner(7, "طارق التونسي", "🇹🇳", 120.0, 120000L),
        TopEarner(8, "كريم السعدي", "🇮🇶", 105.0, 105000L)
    )

    val faqs = listOf(
        FaqItem(
            "كيف يحقق التطبيق الأرباح ويدفعها للمستخدمين؟",
            "يدفع المعلنون والشركات العالمية مبالغ مقابل الترويج لمنتجاتهم وألعابهم داخل تطبيقنا، ونحن نشارك معك حصة مباشرة من هذه العوائد الإعلانية في صورة نقاط قابلة للتحويل إلى أموال حقيقية."
        ),
        FaqItem(
            "ما هو الحد الأدنى للسحب وكم يستغرق التحويل؟",
            "الحد الأدنى هو 5,000 نقطة أي ما يعادل 5.00$ دولار أمريكي. وتتم معالجة الطلبات وإرسال الحوالة إلى حسابك (باي بال، فودافون كاش، محفظة USDT، بطاقات جوجل بلاي) خلال 24 ساعة."
        ),
        FaqItem(
            "هل يُسمح باستخدام برامج VPN أو حسابات متعددة؟",
            "لا، يُمنع تماماً استخدام VPN أو برامج المحاكاة الوهمية أو إنشاء أكثر من حساب للشخص الواحد لضمان أمان النظام وحماية مدفوعات جميع الأعضاء الصادقين."
        ),
        FaqItem(
            "كيف يمكنني الوصول للحد الأدنى للسحب بأسرع وقت؟",
            "شاهد حصتك اليومية من الفيديوهات، احرص على تدوير عجلة الحظ، اكشط بطاقات الحظ اليومية، وشارك كود دعوتك مع 3 من أصدقائك للحصول على 1,500 نقطة إضافية فوراً."
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09120D)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Community Total Stats Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102118)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إجمالي السحوبات المدفوعة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$148,620+",
                            style = MaterialTheme.typography.displayMedium,
                            color = GoldBright,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1B3D2B)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "48,500+",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "مستخدم نشط",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Leaderboard Title
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "قائمة المتصدرين هذا الشهر",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Top 3 Podium Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Rank 2 (Silver)
                PodiumColumn(
                    earner = topEarners[1],
                    medalColor = Color(0xFFC0C0C0),
                    height = 135.dp,
                    modifier = Modifier.weight(1f)
                )

                // Rank 1 (Gold)
                PodiumColumn(
                    earner = topEarners[0],
                    medalColor = GoldAccent,
                    height = 155.dp,
                    modifier = Modifier.weight(1.15f)
                )

                // Rank 3 (Bronze)
                PodiumColumn(
                    earner = topEarners[2],
                    medalColor = Color(0xFFCD7F32),
                    height = 125.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Rest of the Leaderboard
        items(topEarners.drop(3)) { earner ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF101F17),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${earner.rank}",
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.width(28.dp)
                    )

                    Text(text = earner.countryFlag, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = earner.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "$${earner.totalWithdrawnUsd.toInt()} USD",
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // FAQ Section Title
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الأسئلة الشائعة وقواعد الأمان",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Expandable FAQ items
        items(faqs) { faq ->
            ExpandableFaqCard(faq = faq)
        }
    }
}

@Composable
fun PodiumColumn(
    earner: TopEarner,
    medalColor: Color,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF122218)),
        border = androidx.compose.foundation.BorderStroke(1.dp, medalColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(medalColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${earner.rank}",
                    color = medalColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            Text(
                text = "${earner.countryFlag} ${earner.name.split(" ").first()}",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1
            )

            Text(
                text = "$${earner.totalWithdrawnUsd.toInt()}",
                color = GoldBright,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ExpandableFaqCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF102017),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
