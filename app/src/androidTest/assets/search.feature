Feature: Search Repositories

  Scenario: Search for repositories with keyword "abc"
    Given The user is on the home screen
    When The user clicks the search button
    And The user enters "abc" in the search box
    Then The user should see search results for "abc"