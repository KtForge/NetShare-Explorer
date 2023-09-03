@android @main
Feature: Main screen feature

  @android @main_1
  Scenario: User can see the saved configurations
    When I insert initial data
    Then I initialize the App
    And I am on Main screen
    And I see "Linux" element