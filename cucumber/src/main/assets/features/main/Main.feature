@android @main
Feature: Main screen feature

  @android @main_1
  Scenario: User see the initial screen
    When I initialize the App
    And I am on Main screen
    Then I see "You have no network configurations, add one!" element
    And I see "Configure a network location" element

  @android @main_2
  Scenario: User can see the saved configurations
    When I insert initial data
    Then I initialize the App
    And I am on Main screen
    And I see "Linux" element