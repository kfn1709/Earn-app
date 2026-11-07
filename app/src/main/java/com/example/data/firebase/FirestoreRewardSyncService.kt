package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.UserWalletEntity
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestoreRewardSyncService(private val context: Context) {

    private val db: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        Firebase.firestore(dbId)
    }

    private val auth by lazy { Firebase.auth }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun syncLocalWalletToCloud(wallet: UserWalletEntity) {
        val user = currentUser ?: return
        try {
            val userRef = db.collection("users").document(user.uid)
            val data = hashMapOf<String, Any>(
                "userId" to user.uid,
                "displayName" to (user.displayName ?: "User"),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "points" to wallet.points,
                "totalEarnedUsd" to (wallet.totalEarnedPoints / 1000.0),
                "adsWatchedCount" to wallet.adsWatchedToday,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userRef.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSync", "Wallet successfully synced to Firestore for user: ${user.uid}")
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to sync wallet to Firestore", e)
        }
    }

    suspend fun recordPayoutToCloud(
        payoutId: String,
        method: String,
        accountDetails: String,
        pointsDeducted: Int,
        amountUsd: Double
    ): Boolean {
        val user = currentUser ?: return false
        return try {
            val payoutRef = db.collection("users")
                .document(user.uid)
                .collection("payouts")
                .document(payoutId)

            val payoutData = hashMapOf<String, Any>(
                "payoutId" to payoutId,
                "userId" to user.uid,
                "method" to method,
                "accountDetails" to accountDetails,
                "pointsDeducted" to pointsDeducted,
                "amountUsd" to amountUsd,
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp()
            )
            payoutRef.set(payoutData).await()
            Log.d("FirestoreSync", "Payout request $payoutId recorded in Firestore")
            true
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to record payout to Firestore", e)
            false
        }
    }

    suspend fun fetchCloudProfile(): Map<String, Any>? {
        val user = currentUser ?: return null
        return try {
            val snapshot = db.collection("users").document(user.uid).get().await()
            if (snapshot.exists()) snapshot.data else null
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to fetch profile", e)
            null
        }
    }
}
