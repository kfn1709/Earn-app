package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111", // Google's official sample test banner unit
    onAdLoadedCallback: (() -> Unit)? = null
) {
    var isLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1C14))
            .border(
                1.dp,
                if (isLoaded) EmeraldPrimary.copy(alpha = 0.4f) else DarkCardBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 4.dp)
            .testTag("admob_banner_box"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoaded = true
                            hasError = false
                            onAdLoadedCallback?.invoke()
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            hasError = true
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )

        if (!isLoaded && !hasError) {
            Text(
                text = "جاري تحميل إعلان Google AdMob الرسمي...",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 14.dp)
            )
        }
    }
}
