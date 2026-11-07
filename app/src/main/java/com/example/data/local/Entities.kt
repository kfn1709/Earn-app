package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val points: Long = 1250L, // Starter bonus of 1,250 points ($1.25) to give immediate progress
    val totalEarnedPoints: Long = 1250L,
    val totalWithdrawnUsd: Double = 0.0,
    val adsWatchedToday: Int = 0,
    val lastActiveDate: String = "",
    val dailyStreak: Int = 3,
    val hasClaimedToday: Boolean = false,
    val spinsLeftToday: Int = 5,
    val scratchCardsLeftToday: Int = 3,
    val referralCode: String = "CASH789",
    val invitedCount: Int = 2
)

@Entity(tableName = "payout_transactions")
data class PayoutTransactionEntity(
    @PrimaryKey val id: String,
    val methodId: String,
    val methodNameAr: String,
    val pointsDeducted: Long,
    val amountUsd: Double,
    val accountInfo: String,
    val status: String,
    val timestamp: Long
)

@Entity(tableName = "watched_ad_history")
data class WatchedAdHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaignId: String,
    val campaignTitle: String,
    val pointsEarned: Int,
    val timestamp: Long
)
