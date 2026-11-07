package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobRewardedAdManager(private val context: Context) {
    // Official Google sample test ad unit for Rewarded Ads
    private val adUnitId = "ca-app-pub-3940256099942544/5224354917"
    private var rewardedAd: RewardedAd? = null
    var isLoading = false
        private set

    init {
        loadAd()
    }

    fun loadAd(onLoaded: (() -> Unit)? = null) {
        if (rewardedAd != null || isLoading) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                    Log.d("AdMobRewarded", "Rewarded ad loaded successfully")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoading = false
                    Log.w("AdMobRewarded", "Failed to load rewarded ad: ${error.message}")
                }
            }
        )
    }

    fun isAdReady(): Boolean = rewardedAd != null

    fun showAd(
        activity: Activity,
        onRewardEarned: (Int) -> Unit,
        onAdDismissed: () -> Unit,
        onAdFailedToShow: (String) -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null) {
            // If ad is not ready yet, trigger reload and inform callback
            loadAd()
            onAdFailedToShow("الإعلان غير جاهز حالياً، جاري إعادة التحميل...")
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadAd() // Preload the next rewarded ad
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                loadAd()
                onAdFailedToShow(error.message)
            }
        }

        ad.show(activity) { rewardItem ->
            val rewardAmount = if (rewardItem.amount > 0) rewardItem.amount else 150
            onRewardEarned(rewardAmount)
        }
    }
}
