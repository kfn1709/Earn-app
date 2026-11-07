package com.example.data.repository

import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.PayoutTransactionEntity
import com.example.data.local.UserWalletEntity
import com.example.data.local.WatchedAdHistoryEntity
import com.example.data.model.AdCampaign
import com.example.data.model.AdType
import com.example.data.model.LivePayoutProof
import com.example.data.model.PaymentMethod
import com.example.data.model.PayoutOption
import com.example.data.model.SponsoredTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RewardRepository(private val database: AppDatabase) {
    private val dao = database.walletDao()

    val walletFlow: Flow<UserWalletEntity?> = dao.getWalletFlow()
    val payoutHistoryFlow: Flow<List<PayoutTransactionEntity>> = dao.getAllPayoutsFlow()
    val watchedHistoryFlow: Flow<List<WatchedAdHistoryEntity>> = dao.getRecentWatchedAdsFlow()

    suspend fun ensureWalletInitialized() {
        val current = dao.getWalletDirect()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (current == null) {
            val initial = UserWalletEntity(
                id = 1,
                points = 2450L,
                totalEarnedPoints = 2450L,
                totalWithdrawnUsd = 0.0,
                adsWatchedToday = 0,
                lastActiveDate = todayStr,
                dailyStreak = 3,
                hasClaimedToday = false,
                spinsLeftToday = 5,
                scratchCardsLeftToday = 3,
                referralCode = "MOAD_2026",
                invitedCount = 3
            )
            dao.insertOrUpdateWallet(initial)
        } else if (current.lastActiveDate != todayStr) {
            // New day reset
            val updated = current.copy(
                lastActiveDate = todayStr,
                adsWatchedToday = 0,
                spinsLeftToday = 5,
                scratchCardsLeftToday = 3,
                hasClaimedToday = false
            )
            dao.updateWallet(updated)
        }
    }

    suspend fun rewardAdCompleted(campaign: AdCampaign): Long {
        val current = dao.getWalletDirect() ?: return 0L
        val newPoints = current.points + campaign.rewardPoints
        val newTotal = current.totalEarnedPoints + campaign.rewardPoints
        val updated = current.copy(
            points = newPoints,
            totalEarnedPoints = newTotal,
            adsWatchedToday = current.adsWatchedToday + 1
        )
        dao.updateWallet(updated)

        dao.insertWatchedAd(
            WatchedAdHistoryEntity(
                campaignId = campaign.id,
                campaignTitle = campaign.titleAr,
                pointsEarned = campaign.rewardPoints,
                timestamp = System.currentTimeMillis()
            )
        )
        return newPoints
    }

    suspend fun rewardBannerImpression(points: Int = 15): Long {
        val current = dao.getWalletDirect() ?: return 0L
        val updated = current.copy(
            points = current.points + points,
            totalEarnedPoints = current.totalEarnedPoints + points
        )
        dao.updateWallet(updated)
        dao.insertWatchedAd(
            WatchedAdHistoryEntity(
                campaignId = "admob_banner",
                campaignTitle = "بانر إعلاني AdMob",
                pointsEarned = points,
                timestamp = System.currentTimeMillis()
            )
        )
        return updated.points
    }

    suspend fun claimDailyBonus(bonusPoints: Int): Boolean {
        val current = dao.getWalletDirect() ?: return false
        if (current.hasClaimedToday) return false

        val updated = current.copy(
            points = current.points + bonusPoints,
            totalEarnedPoints = current.totalEarnedPoints + bonusPoints,
            hasClaimedToday = true,
            dailyStreak = current.dailyStreak + 1
        )
        dao.updateWallet(updated)
        return true
    }

    suspend fun applySpinReward(wonPoints: Int): Int {
        val current = dao.getWalletDirect() ?: return 0
        if (current.spinsLeftToday <= 0) return 0

        val updated = current.copy(
            points = current.points + wonPoints,
            totalEarnedPoints = current.totalEarnedPoints + wonPoints,
            spinsLeftToday = current.spinsLeftToday - 1
        )
        dao.updateWallet(updated)
        return current.spinsLeftToday - 1
    }

    suspend fun applyScratchReward(wonPoints: Int): Int {
        val current = dao.getWalletDirect() ?: return 0
        if (current.scratchCardsLeftToday <= 0) return 0

        val updated = current.copy(
            points = current.points + wonPoints,
            totalEarnedPoints = current.totalEarnedPoints + wonPoints,
            scratchCardsLeftToday = current.scratchCardsLeftToday - 1
        )
        dao.updateWallet(updated)
        return current.scratchCardsLeftToday - 1
    }

    suspend fun completeTask(task: SponsoredTask): Boolean {
        val current = dao.getWalletDirect() ?: return false
        val updated = current.copy(
            points = current.points + task.rewardPoints,
            totalEarnedPoints = current.totalEarnedPoints + task.rewardPoints
        )
        dao.updateWallet(updated)
        return true
    }

    suspend fun addInviteBonus(): Int {
        val current = dao.getWalletDirect() ?: return 0
        val bonus = 500
        val updated = current.copy(
            points = current.points + bonus,
            totalEarnedPoints = current.totalEarnedPoints + bonus,
            invitedCount = current.invitedCount + 1
        )
        dao.updateWallet(updated)
        return updated.invitedCount
    }

    suspend fun requestWithdrawal(
        method: PaymentMethod,
        option: PayoutOption,
        accountInfo: String
    ): Result<PayoutTransactionEntity> {
        val current = dao.getWalletDirect() ?: return Result.failure(Exception("المحفظة غير متوفرة"))

        if (current.points < option.pointsRequired) {
            return Result.failure(Exception("نقاطك الحالية (${current.points}) أقل من المطلوب (${option.pointsRequired})"))
        }

        if (accountInfo.trim().length < 4) {
            return Result.failure(Exception("يرجى إدخال بيانات حساب صحيحة للتحويل"))
        }

        val transaction = PayoutTransactionEntity(
            id = "TX-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.US),
            methodId = method.id,
            methodNameAr = method.nameAr,
            pointsDeducted = option.pointsRequired,
            amountUsd = option.usdAmount,
            accountInfo = accountInfo.trim(),
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )

        dao.insertPayout(transaction)

        val updatedWallet = current.copy(
            points = current.points - option.pointsRequired,
            totalWithdrawnUsd = current.totalWithdrawnUsd + option.usdAmount
        )
        dao.updateWallet(updatedWallet)

        return Result.success(transaction)
    }

    fun getAdCampaigns(): List<AdCampaign> = listOf(
        AdCampaign(
            id = "ad_101",
            titleAr = "لعبة أبطال الممالك 3D",
            sponsorName = "Kings of Eldria",
            taglineAr = "ابنِ قلعتك وقُد جيوشك نحو النصر! حمّل اللعبة مجاناً الآن.",
            rewardPoints = 120,
            durationSeconds = 15,
            type = AdType.REWARDED_VIDEO,
            rating = 4.8f,
            categoryAr = "ألعاب استراتيجية",
            callToActionAr = "تثبيت ولعب مجاناً",
            bannerDrawableRes = R.drawable.ad_sponsor_game,
            isHot = true
        ),
        AdCampaign(
            id = "ad_102",
            titleAr = "بطاقة فيزا كاش باك للتسوق",
            sponsorName = "PayGlobal Card",
            taglineAr = "استرجع حتى 10% كاش باك فوري على كافة مشترياتك الدولية.",
            rewardPoints = 100,
            durationSeconds = 20,
            type = AdType.REWARDED_VIDEO,
            rating = 4.9f,
            categoryAr = "تطبيقات مالية",
            callToActionAr = "طلب البطاقة مجاناً",
            bannerDrawableRes = R.drawable.hero_reward_banner,
            isHot = false
        ),
        AdCampaign(
            id = "ad_103",
            titleAr = "إعلان خاطف: عروض الجمعة الكبرى",
            sponsorName = "MegaStore Express",
            taglineAr = "خصومات حصرية تصل إلى 70% وتوصيل مجاني لجميع الطلبات.",
            rewardPoints = 40,
            durationSeconds = 5,
            type = AdType.INTERSTITIAL,
            rating = 4.7f,
            categoryAr = "تسوق إلكتروني",
            callToActionAr = "تصفح العروض",
            bannerDrawableRes = null,
            isHot = false
        ),
        AdCampaign(
            id = "ad_104",
            titleAr = "تجربة تفاعلية: أحجية الجواهر السحرية",
            sponsorName = "Jewel Quest Studio",
            taglineAr = "طابق 3 جواهر ذهبية لفتح الصندوق وكسب مكافأة إضافية!",
            rewardPoints = 160,
            durationSeconds = 25,
            type = AdType.PLAYABLE_MINI,
            rating = 4.9f,
            categoryAr = "ألعاب ألغاز تفاعلية",
            callToActionAr = "العب المستوى التجريبي",
            bannerDrawableRes = R.drawable.ad_sponsor_game,
            isHot = true
        ),
        AdCampaign(
            id = "ad_105",
            titleAr = "تطبيق حجز الطيران والفنادق",
            sponsorName = "FlyEasy Travel",
            taglineAr = "أرخص أسعار تذاكر الطيران مع ضمان أفضل سعر وخيارات تقسيط ميسرة.",
            rewardPoints = 90,
            durationSeconds = 15,
            type = AdType.REWARDED_VIDEO,
            rating = 4.6f,
            categoryAr = "سياحة وسفر",
            callToActionAr = "استكشف الوجهات",
            bannerDrawableRes = null,
            isHot = false
        ),
        AdCampaign(
            id = "ad_106",
            titleAr = "إعلان سريع: تطبيق تعلم اللغات",
            sponsorName = "LinguaMaster AI",
            taglineAr = "تعلم الإنجليزية والفرنسية في 10 دقائق يومياً بتقنيات الذكاء الاصطناعي.",
            rewardPoints = 45,
            durationSeconds = 5,
            type = AdType.INTERSTITIAL,
            rating = 4.8f,
            categoryAr = "تعليم وتطوير",
            callToActionAr = "ابدأ التعلم مجاناً",
            bannerDrawableRes = null,
            isHot = false
        )
    )

    fun getPaymentMethods(): List<PaymentMethod> = listOf(
        PaymentMethod(
            id = "cih",
            nameEn = "CIH Bank",
            nameAr = "بنك CIH المغربي",
            iconKey = "bank",
            accentColorHex = 0xFFF97316,
            minPoints = 1000L,
            descriptionAr = "تحويل فوري ومباشر إلى حسابك البنكي أو رقم الـ RIB ببنك CIH.",
            inputLabelAr = "رقم الحساب أو الـ RIB (24 رقم)",
            inputPlaceholderAr = "230 450 ...",
            options = listOf(
                PayoutOption(1.0, 1000L, isPopular = true),
                PayoutOption(5.0, 5000L),
                PayoutOption(10.0, 10000L)
            )
        ),
        PaymentMethod(
            id = "bmce",
            nameEn = "BMCE (Bank of Africa)",
            nameAr = "بنك أفريقيا (BMCE)",
            iconKey = "bank",
            accentColorHex = 0xFF3B82F6,
            minPoints = 1000L,
            descriptionAr = "تحويل بنكي مباشر إلى حسابك لدى بنك BMCE Bank of Africa.",
            inputLabelAr = "رقم الحساب أو الـ RIB (24 رقم)",
            inputPlaceholderAr = "011 810 ...",
            options = listOf(
                PayoutOption(1.0, 1000L, isPopular = true),
                PayoutOption(5.0, 5000L),
                PayoutOption(10.0, 10000L)
            )
        ),
        PaymentMethod(
            id = "paypal",
            nameEn = "PayPal",
            nameAr = "باي بال (PayPal)",
            iconKey = "paypal",
            accentColorHex = 0xFF0070BA,
            minPoints = 5000L,
            descriptionAr = "تحويل دولار مباشر إلى حساب PayPal الموثق الخاص بك خلال 24 ساعة.",
            inputLabelAr = "بريدك الإلكتروني في PayPal",
            inputPlaceholderAr = "example@email.com",
            options = listOf(
                PayoutOption(5.0, 5000L, isPopular = true),
                PayoutOption(10.0, 10000L),
                PayoutOption(25.0, 25000L),
                PayoutOption(50.0, 50000L)
            )
        ),
        PaymentMethod(
            id = "vodafone_cash",
            nameEn = "Vodafone / Mobile Wallet",
            nameAr = "فودافون كاش ومحافظ الجوال",
            iconKey = "wallet",
            accentColorHex = 0xFFE60000,
            minPoints = 5000L,
            descriptionAr = "تحويل فوري إلى محفظتك الإلكترونية بالعملة المحلية بسعر الصرف الرسمي.",
            inputLabelAr = "رقم المحفظة الإلكترونية",
            inputPlaceholderAr = "010xxxxxxxx",
            options = listOf(
                PayoutOption(5.0, 5000L, isPopular = true),
                PayoutOption(10.0, 10000L),
                PayoutOption(20.0, 20000L)
            )
        ),
        PaymentMethod(
            id = "usdt_crypto",
            nameEn = "USDT (TRC-20)",
            nameAr = "تيذر USDT (العملات الرقمية)",
            iconKey = "crypto",
            accentColorHex = 0xFF26A17B,
            minPoints = 10000L,
            descriptionAr = "إيداع عملة USDT الرقمية إلى عنوان محفظتك على شبكة TRC20 أو BEP20 بدون رسوم.",
            inputLabelAr = "عنوان محفظة USDT (TRC20)",
            inputPlaceholderAr = "T...",
            options = listOf(
                PayoutOption(10.0, 10000L, isPopular = true),
                PayoutOption(25.0, 25000L),
                PayoutOption(50.0, 50000L)
            )
        ),
        PaymentMethod(
            id = "google_play",
            nameEn = "Google Play",
            nameAr = "بطاقات Google Play",
            iconKey = "play",
            accentColorHex = 0xFF01875F,
            minPoints = 5000L,
            descriptionAr = "كود بطاقة هدايا جوجل بلاي لشحن حسابك وشراء التطبيقات والألعاب.",
            inputLabelAr = "البريد الإلكتروني لاستلام الكود",
            inputPlaceholderAr = "user@gmail.com",
            options = listOf(
                PayoutOption(5.0, 5000L),
                PayoutOption(10.0, 10000L, isPopular = true),
                PayoutOption(25.0, 25000L)
            )
        ),
        PaymentMethod(
            id = "stc_pay",
            nameEn = "STC Pay / Zain",
            nameAr = "STC Pay ورصيد الجوال",
            iconKey = "phone",
            accentColorHex = 0xFF4F008C,
            minPoints = 5000L,
            descriptionAr = "شحن مباشر لمحفظة STC Pay أو رصيد اتصال فوري.",
            inputLabelAr = "رقم الهاتف المسجل",
            inputPlaceholderAr = "05xxxxxxxx",
            options = listOf(
                PayoutOption(5.0, 5000L),
                PayoutOption(10.0, 10000L, isPopular = true)
            )
        ),
        PaymentMethod(
            id = "amazon_gift",
            nameEn = "Amazon Gift Card",
            nameAr = "بطاقات أمازون",
            iconKey = "amazon",
            accentColorHex = 0xFFFF9900,
            minPoints = 10000L,
            descriptionAr = "قسيمة مشتريات أمازون الرقمية تصل إلى بريدك فور تدقيق الطلب.",
            inputLabelAr = "البريد الإلكتروني لاستلام القسيمة",
            inputPlaceholderAr = "user@email.com",
            options = listOf(
                PayoutOption(10.0, 10000L),
                PayoutOption(25.0, 25000L, isPopular = true)
            )
        )
    )

    fun getSponsoredTasks(): List<SponsoredTask> = listOf(
        SponsoredTask(
            id = "task_1",
            titleAr = "تقييم التطبيق 5 نجوم وكتابة رأيك",
            descriptionAr = "ادعم تطبيق AdReward في المتجر للمساعدة في تسريع دفع الأرباح لجميع الأعضاء.",
            sponsor = "فريق الدعم",
            rewardPoints = 400,
            categoryAr = "مهمة سريعة",
            actionTextAr = "تقييم الآن"
        ),
        SponsoredTask(
            id = "task_2",
            titleAr = "استطلاع رأي قصير حول الألعاب المفضلة",
            descriptionAr = "أجب عن 5 أسئلة سريعة لتخصيص الإعلانات الأكثر ربحاً لحسابك.",
            sponsor = "مركز استطلاعات AdMetrics",
            rewardPoints = 350,
            categoryAr = "استبيان",
            actionTextAr = "بدء الاستطلاع"
        ),
        SponsoredTask(
            id = "task_3",
            titleAr = "تثبيت وتجربة محفظة Binance للتداول",
            descriptionAr = "سجل حسابك التجريبي وافتح التطبيق لمدة دقيقتين لكسب النقاط فوراً.",
            sponsor = "CryptoPartners",
            rewardPoints = 1200,
            categoryAr = "عرض حصري",
            actionTextAr = "تثبيت التطبيق"
        ),
        SponsoredTask(
            id = "task_4",
            titleAr = "متابعة قناة التيليجرام الرسمية لإثباتات الدفع",
            descriptionAr = "انضم لمجتمعنا لمشاهدة إيصالات التحويل اليومية والمسابقات الأسبوعية.",
            sponsor = "مجتمع AdReward",
            rewardPoints = 300,
            categoryAr = "تواصل اجتماعي",
            actionTextAr = "الانضمام للقناة"
        )
    )

    fun getLivePayoutProofs(): List<LivePayoutProof> = listOf(
        LivePayoutProof("p1", "أحمد الشمري", 25.0, "PayPal", "منذ دقيقتين", "أ"),
        LivePayoutProof("p2", "خالد المصري", 10.0, "فودافون كاش", "منذ 6 دقائق", "خ"),
        LivePayoutProof("p3", "سارة المنصوري", 50.0, "USDT TRC20", "منذ 14 دقيقة", "س"),
        LivePayoutProof("p4", "عمر التونسي", 10.0, "Google Play", "منذ 21 دقيقة", "ع"),
        LivePayoutProof("p5", "فيصل العتيبي", 25.0, "STC Pay", "منذ 33 دقيقة", "ف"),
        LivePayoutProof("p6", "ياسمين كمال", 10.0, "PayPal", "منذ 45 دقيقة", "ي")
    )
}
