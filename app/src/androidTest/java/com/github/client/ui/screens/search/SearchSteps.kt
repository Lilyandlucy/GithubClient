//package com.github.client.ui.screens.search
//
//import androidx.compose.ui.test.hasText
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.performTextInput
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.rule.ActivityTestRule
//import com.github.client.ui.MainActivity
//import com.github.client.ui.screens.home.HomeScreen
//import io.cucumber.java.After
//import io.cucumber.java.Before
//import io.cucumber.java.en.Given
//import io.cucumber.java.en.Then
//import io.cucumber.java.en.When
//import org.junit.Rule
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class SearchSteps {
//
//    @Rule
//    @JvmField
//    val rule = ActivityTestRule(MainActivity::class.java)
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @Before
//    fun setup() {
//        composeTestRule.setContent {
//            HomeScreen(
//                navigateToRepositoryDetails = { _, _ -> },
//                navigateToSearch = {},
//                navigateToProfile = {},
//                navigateToLogin = {})
//        }
//    }
//
//    @After
//    fun tearDown() {
//        rule.finishActivity()
//    }
//
//    @Given("The user is on the home screen")
//    fun theUserIsOnTheHomeScreen() {
//        composeTestRule.onNodeWithText("GitHub Client").assertExists()
//    }
//
//    @When("The user clicks the search button")
//    fun theUserClicksTheSearchButton() {
//        composeTestRule.onNodeWithContentDescription("Search").performClick()
//    }
//
//    @When("The user enters {string} in the search box")
//    fun theUserEntersInTheSearchBox(keyword: String) {
//        composeTestRule.onNodeWithContentDescription("Search repositories...")
//            .performTextInput(keyword)
//    }
//
//    @Then("The user should see search results for {string}")
//    fun theUserShouldSeeSearchResultsFor(keyword: String) {
//        val searchResult = composeTestRule.onNode(hasText(keyword))
//        searchResult.assertExists()
//    }
//}