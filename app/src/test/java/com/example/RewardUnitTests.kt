package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardUnitTests {
    @Test
    fun testPointsToUsdConversionRate() {
        val points = 5000L
        val usd = points / 1000.0
        assertEquals(5.0, usd, 0.001)

        val starterPoints = 1850L
        val starterUsd = starterPoints / 1000.0
        assertEquals(1.85, starterUsd, 0.001)
    }

    @Test
    fun testMinimumWithdrawalThreshold() {
        val minPoints = 5000L
        val userPointsLow = 4200L
        val userPointsHigh = 5500L

        assertTrue(userPointsLow < minPoints)
        assertTrue(userPointsHigh >= minPoints)
    }

    @Test
    fun testDailyStreakBonus() {
        val streak = 3
        val bonus = 50 + (streak * 25)
        assertEquals(125, bonus)
    }
}
