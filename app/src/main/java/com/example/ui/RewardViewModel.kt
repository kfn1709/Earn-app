package com.example.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.data.model.AdType
import com.example.ui.components.AdMobRewardedAdManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PayoutTransactionEntity
import com.example.data.local.UserWalletEntity
import com.example.data.local.WatchedAdHistoryEntity
import com.example.data.model.AdCampaign
import com.example.data.model.LivePayoutProof
import com.example.data.model.PaymentMethod
import com.example.data.model.PayoutOption
import com.example.data.model.SponsoredTask
import com.example.data.repository.RewardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AdPlayerState(
    val campaign: AdCampaign? = null,
    val isPlaying: Boolean = false,
    val remainingSeconds: Int = 0,
    val isRewardClaimable: Boolean = false,
    val isCompleted: Boolean = false,
    val interactiveTaps: Int = 0
)

class RewardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RewardRepository(AppDatabase.getDatabase(application))

    val wallet: StateFlow<UserWalletEntity?> = repository.walletFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val payouts: StateFlow<List<PayoutTransactionEntity>> = repository.payoutHistoryFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchedHistory: StateFlow<List<WatchedAdHistoryEntity>> = repository.watchedHistoryFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _adPlayerState = MutableStateFlow(AdPlayerState())
    val adPlayerState: StateFlow<AdPlayerState> = _adPlayerState.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _lastSpinWon = MutableStateFlow<Int?>(null)
    val lastSpinWon: StateFlow<Int?> = _lastSpinWon.asStateFlow()

    private val _targetSpinDegree = MutableStateFlow(0f)
    val targetSpinDegree: StateFlow<Float> = _targetSpinDegree.asStateFlow()

    private val _scratchState = MutableStateFlow<ScratchCardSession?>(null)
    val scratchState: StateFlow<ScratchCardSession?> = _scratchState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _celebrationTrigger = MutableStateFlow(0L)
    val celebrationTrigger: StateFlow<Long> = _celebrationTrigger.asStateFlow()

    private val rewardedAdManager = AdMobRewardedAdManager(application)
    private val _isLoadingRewardedAd = MutableStateFlow(false)
    val isLoadingRewardedAd: StateFlow<Boolean> = _isLoadingRewardedAd.asStateFlow()

    val adCampaigns: List<AdCampaign> = repository.getAdCampaigns()
    val paymentMethods: List<PaymentMethod> = repository.getPaymentMethods()
    val sponsoredTasks: List<SponsoredTask> = repository.getSponsoredTasks()
    val liveProofs: List<LivePayoutProof> = repository.getLivePayoutProofs()

    private val authManager = com.example.data.firebase.GoogleAuthManager(application)
    private val firestoreSyncService = com.example.data.firebase.FirestoreRewardSyncService(application)

    private val _currentUser = MutableStateFlow(authManager.currentUser)
    val currentUser = _currentUser.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private var adTimerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureWalletInitialized()
            initNewScratchCard()
            // If user is signed in, sync cloud data
            authManager.currentUser?.let {
                syncWalletWithCloud()
            }
        }
    }

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode = _isGuestMode.asStateFlow()

    private val _customProfileName = MutableStateFlow<String?>(null)
    val customProfileName = _customProfileName.asStateFlow()

    private val _customProfileEmail = MutableStateFlow<String?>(null)
    val customProfileEmail = _customProfileEmail.asStateFlow()

    fun signInWithGoogle() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = authManager.signInWithGoogle()
            result.onSuccess { user ->
                _currentUser.value = user
                _customProfileName.value = user.displayName
                _customProfileEmail.value = user.email
                _isGuestMode.value = false
                _celebrationTrigger.value = System.currentTimeMillis()
                _toastMessage.value = "مرحباً بك يا ${user.displayName ?: "صديقنا"}! تم تسجيل الدخول وحفظ حسابك سحابياً."
                syncWalletWithCloud()
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "فشل تسجيل الدخول بواسطة Google"
            }
            _isSyncing.value = false
        }
    }

    fun loginWithEmail(name: String, email: String, isNewUser: Boolean) {
        viewModelScope.launch {
            _customProfileName.value = name.ifBlank { email.substringBefore("@") }
            _customProfileEmail.value = email
            _isGuestMode.value = false
            _celebrationTrigger.value = System.currentTimeMillis()
            _toastMessage.value = if (isNewUser) "🎉 تم إنشاء الحساب بنجاح!" else "✨ مرحباً بك مجدداً!"
            // Also attempt cloud sync if authenticated
            syncWalletWithCloud()
        }
    }

    fun continueAsGuest() {
        _isGuestMode.value = true
        _customProfileName.value = "زائر كريم"
        _toastMessage.value = "تمت المتابعة كزائر"
    }

    fun signOut() {
        authManager.signOut()
        _currentUser.value = null
        _customProfileName.value = null
        _customProfileEmail.value = null
        _isGuestMode.value = false
        _toastMessage.value = "تم تسجيل الخروج"
    }

    fun syncWalletWithCloud() {
        viewModelScope.launch {
            val currentWallet = wallet.value ?: return@launch
            firestoreSyncService.syncLocalWalletToCloud(currentWallet)
        }
    }

    private fun triggerHaptic(durationMs: Long = 40) {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    fun startWatchingAd(campaign: AdCampaign) {
        adTimerJob?.cancel()
        _adPlayerState.value = AdPlayerState(
            campaign = campaign,
            isPlaying = true,
            remainingSeconds = campaign.durationSeconds,
            isRewardClaimable = false,
            isCompleted = false,
            interactiveTaps = 0
        )
        triggerHaptic(50)

        adTimerJob = viewModelScope.launch {
            for (sec in campaign.durationSeconds downTo 1) {
                _adPlayerState.value = _adPlayerState.value.copy(remainingSeconds = sec)
                delay(1000)
            }
            _adPlayerState.value = _adPlayerState.value.copy(
                remainingSeconds = 0,
                isRewardClaimable = true
            )
            triggerHaptic(80)
        }
    }

    fun onInteractiveAdTap() {
        val current = _adPlayerState.value
        if (current.isPlaying && !current.isCompleted) {
            triggerHaptic(25)
            _adPlayerState.value = current.copy(interactiveTaps = current.interactiveTaps + 1)
        }
    }

    fun claimAdReward() {
        val current = _adPlayerState.value
        val campaign = current.campaign ?: return
        if (!current.isRewardClaimable || current.isCompleted) return

        viewModelScope.launch {
            repository.rewardAdCompleted(campaign)
            _adPlayerState.value = current.copy(isCompleted = true)
            _celebrationTrigger.value = System.currentTimeMillis()
            triggerHaptic(120)
            _toastMessage.value = "مبروك! تمت إضافة +${campaign.rewardPoints} نقطة إلى محفظتك"
        }
    }

    fun closeAdPlayer() {
        adTimerJob?.cancel()
        _adPlayerState.value = AdPlayerState()
    }

    fun onBannerLoadedReward() {
        viewModelScope.launch {
            repository.rewardBannerImpression(15)
            triggerHaptic(40)
        }
    }

    fun showAdMobRewardedAd(activity: Activity) {
        if (_isLoadingRewardedAd.value) return
        _isLoadingRewardedAd.value = true
        triggerHaptic(50)

        rewardedAdManager.showAd(
            activity = activity,
            onRewardEarned = { points ->
                viewModelScope.launch {
                    val campaign = AdCampaign(
                        id = "admob_rewarded",
                        titleAr = "إعلان فيديو بمكافأة AdMob",
                        sponsorName = "Google AdMob Network",
                        taglineAr = "إعلان رسمي معتمد من شبكة Google للأرباح",
                        rewardPoints = points,
                        durationSeconds = 15,
                        type = AdType.REWARDED_VIDEO,
                        rating = 5.0f,
                        categoryAr = "إعلانات جوجل",
                        callToActionAr = "تثبيت الآن"
                    )
                    repository.rewardAdCompleted(campaign)
                    _isLoadingRewardedAd.value = false
                    _celebrationTrigger.value = System.currentTimeMillis()
                    triggerHaptic(120)
                    _toastMessage.value = "مبروك! تمت إضافة +$points نقطة إلى محفظتك بنجاح!"
                }
            },
            onAdDismissed = {
                _isLoadingRewardedAd.value = false
            },
            onAdFailedToShow = {
                _isLoadingRewardedAd.value = false
                // When running in container environment or when ad is caching, fallback to interactive player
                val fallbackCampaign = adCampaigns.firstOrNull()
                if (fallbackCampaign != null) {
                    startWatchingAd(fallbackCampaign)
                } else {
                    _toastMessage.value = "جاري تجهيز إعلان AdMob، يرجى المحاولة بعد لحظات..."
                }
            }
        )
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val currentStreak = wallet.value?.dailyStreak ?: 1
            val bonusPoints = 50 + (currentStreak * 25)
            val success = repository.claimDailyBonus(bonusPoints)
            if (success) {
                _celebrationTrigger.value = System.currentTimeMillis()
                triggerHaptic(100)
                _toastMessage.value = "تم استلام مكافأة اليوم: +$bonusPoints نقطة بنجاح!"
            } else {
                _toastMessage.value = "لقد قمت باستلام مكافأة اليوم بالفعل، عد غداً لمضاعفة النقاط!"
            }
        }
    }

    fun spinWheel() {
        val currentWallet = wallet.value ?: return
        if (currentWallet.spinsLeftToday <= 0) {
            _toastMessage.value = "نفدت محاولات العجلة لليوم! شاهد إعلانات للحصول على محاولات إضافية."
            return
        }
        if (_isSpinning.value) return

        _isSpinning.value = true
        _lastSpinWon.value = null

        // 8 sectors on wheel: [50, 25, 200, 100, 500, 75, 150, 30]
        val wheelValues = listOf(50, 25, 200, 100, 500, 75, 150, 30)
        val chosenIndex = Random.nextInt(wheelValues.size)
        val prize = wheelValues[chosenIndex]

        val baseSpins = (5..8).random() * 360f
        val sectorAngle = 360f / wheelValues.size
        // Center of sector
        val targetDegree = baseSpins + (chosenIndex * sectorAngle) + (sectorAngle / 2f)

        _targetSpinDegree.value = _targetSpinDegree.value + targetDegree
        triggerHaptic(60)

        viewModelScope.launch {
            delay(3500) // Spin animation duration
            _isSpinning.value = false
            _lastSpinWon.value = prize
            repository.applySpinReward(prize)
            _celebrationTrigger.value = System.currentTimeMillis()
            triggerHaptic(140)
            _toastMessage.value = "فوز رائع! أربحت +$prize نقطة من عجلة الحظ!"
        }
    }

    fun initNewScratchCard() {
        val prizes = listOf(100, 150, 200, 250, 300, 500)
        val winPrize = prizes.random()
        val cells = List(6) { index ->
            ScratchCell(
                id = index,
                value = if (index < 3) winPrize else prizes.filter { it != winPrize }.random(),
                isRevealed = false
            )
        }.shuffled()

        _scratchState.value = ScratchCardSession(
            winPrize = winPrize,
            cells = cells,
            isFullyRevealed = false,
            isClaimed = false
        )
    }

    fun scratchCell(cellId: Int) {
        val current = _scratchState.value ?: return
        val currentWallet = wallet.value ?: return

        if (currentWallet.scratchCardsLeftToday <= 0 && !current.isFullyRevealed) {
            _toastMessage.value = "نفدت بطاقات الحك لليوم! عد غداً للمزيد من البطاقات الذهبية."
            return
        }

        triggerHaptic(30)
        val updatedCells = current.cells.map {
            if (it.id == cellId) it.copy(isRevealed = true) else it
        }

        val revealedCount = updatedCells.count { it.isRevealed }
        val isDone = revealedCount >= 3

        _scratchState.value = current.copy(
            cells = updatedCells,
            isFullyRevealed = isDone
        )

        if (isDone && !current.isClaimed) {
            _scratchState.value = _scratchState.value?.copy(isClaimed = true)
            viewModelScope.launch {
                repository.applyScratchReward(current.winPrize)
                _celebrationTrigger.value = System.currentTimeMillis()
                triggerHaptic(120)
                _toastMessage.value = "مبروك! كشفت 3 مربعات متطابقة وربحت +${current.winPrize} نقطة!"
            }
        }
    }

    fun completeTask(task: SponsoredTask) {
        viewModelScope.launch {
            val success = repository.completeTask(task)
            if (success) {
                _celebrationTrigger.value = System.currentTimeMillis()
                triggerHaptic(90)
                _toastMessage.value = "تمت المهمة بنجاح! أضيفت +${task.rewardPoints} نقطة."
            }
        }
    }

    fun shareInviteCode(context: Context) {
        val code = wallet.value?.referralCode ?: "CASH789"
        val shareText = "اربح المال والمكافآت الحقيقية من مشاهدة الإعلانات اليومية على تطبيق AdReward! استخدم كود دعوتي [$code] للحصول على 500 نقطة مجانية فورية عند التسجيل: https://adreward.app/ref/$code"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(intent, "مشاركة كود الدعوة")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)

        viewModelScope.launch {
            // Reward user on sharing
            repository.addInviteBonus()
            _toastMessage.value = "تمت مشاركة كودك! تمت إضافة +500 نقطة مكافأة دعوة الأصدقاء."
        }
    }

    fun requestWithdrawal(
        method: PaymentMethod,
        option: PayoutOption,
        accountInfo: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.requestWithdrawal(method, option, accountInfo)
            result.onSuccess {
                _celebrationTrigger.value = System.currentTimeMillis()
                triggerHaptic(150)
                _toastMessage.value = "تم إرسال طلب سحب $${option.usdAmount} بنجاح! سيتم التحويل خلال 24 ساعة."
                onSuccess()
            }.onFailure { error ->
                triggerHaptic(80)
                _toastMessage.value = error.message ?: "فشل طلب السحب"
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}

data class ScratchCardSession(
    val winPrize: Int,
    val cells: List<ScratchCell>,
    val isFullyRevealed: Boolean,
    val isClaimed: Boolean
)

data class ScratchCell(
    val id: Int,
    val value: Int,
    val isRevealed: Boolean
)
