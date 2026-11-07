package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getWalletFlow(): Flow<UserWalletEntity?>

    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    suspend fun getWalletDirect(): UserWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: UserWalletEntity)

    @Update
    suspend fun updateWallet(wallet: UserWalletEntity)

    @Query("SELECT * FROM payout_transactions ORDER BY timestamp DESC")
    fun getAllPayoutsFlow(): Flow<List<PayoutTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(transaction: PayoutTransactionEntity)

    @Query("SELECT * FROM watched_ad_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentWatchedAdsFlow(): Flow<List<WatchedAdHistoryEntity>>

    @Insert
    suspend fun insertWatchedAd(item: WatchedAdHistoryEntity)
}
