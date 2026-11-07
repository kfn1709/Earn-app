package com.example.data.model

enum class AdType(val titleAr: String, val badgeColorHex: Long) {
    REWARDED_VIDEO("فيديو بمكافأة", 0xFF00E676),
    INTERSTITIAL("إعلان سريع", 0xFFFFD54F),
    SPONSORED_DEMO("تجربة تطبيق", 0xFF448AFF),
    PLAYABLE_MINI("لعبة تفاعلية", 0xFFFF5252)
}

data class AdCampaign(
    val id: String,
    val titleAr: String,
    val sponsorName: String,
    val taglineAr: String,
    val rewardPoints: Int,
    val durationSeconds: Int,
    val type: AdType,
    val rating: Float,
    val categoryAr: String,
    val callToActionAr: String,
    val bannerDrawableRes: Int? = null,
    val isHot: Boolean = false
)

enum class PayoutStatus(val labelAr: String, val colorHex: Long) {
    PENDING("قيد المراجعة", 0xFFFFB300),
    PROCESSING("جاري التحويل", 0xFF448AFF),
    COMPLETED("تم الدفع بنجاح", 0xFF00E676),
    FAILED("مرفوض", 0xFFFF5252)
}

data class PayoutOption(
    val usdAmount: Double,
    val pointsRequired: Long,
    val isPopular: Boolean = false
)

data class PaymentMethod(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val iconKey: String,
    val accentColorHex: Long,
    val minPoints: Long,
    val descriptionAr: String,
    val inputLabelAr: String,
    val inputPlaceholderAr: String,
    val options: List<PayoutOption>
)

data class LivePayoutProof(
    val id: String,
    val userName: String,
    val amountUsd: Double,
    val methodName: String,
    val timeAgoAr: String,
    val avatarInitial: String
)

data class SponsoredTask(
    val id: String,
    val titleAr: String,
    val descriptionAr: String,
    val sponsor: String,
    val rewardPoints: Int,
    val categoryAr: String,
    val actionTextAr: String,
    val isCompleted: Boolean = false
)
