package com.github.client.ui.screens.home

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigateToSearchScreen() {
        var navigateToSearchCalled = false
        composeTestRule.setContent {
            HomeScreen(
                navigateToRepositoryDetails = { _, _ -> },
                navigateToSearch = { navigateToSearchCalled = true },
                navigateToProfile = { },
                navigateToLogin = { },
            )
        }

        composeTestRule.onNodeWithContentDescription("Search").assertHasClickAction()

        composeTestRule.onNodeWithContentDescription("Search").performClick()

        assert(navigateToSearchCalled)
    }

    @Test
    fun testNavigateToLoginScreen() {
        var navigateToLoginCalled = false
        composeTestRule.setContent {
            HomeScreen(
                navigateToRepositoryDetails = { _, _ -> },
                navigateToSearch = { },
                navigateToProfile = { },
                navigateToLogin = { navigateToLoginCalled = true },
            )
        }

        composeTestRule.onNodeWithContentDescription("Login").performClick()

        assert(navigateToLoginCalled)
    }
}