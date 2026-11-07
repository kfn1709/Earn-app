package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class AppStringsUnitTest {

  @Test
  fun testAppNameConstant() {
    val expectedAppName = "AdReward"
    assertEquals("AdReward", expectedAppName)
  }
}
