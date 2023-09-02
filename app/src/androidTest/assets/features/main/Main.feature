Feature: Main screen feature

  @android @main_1
  Scenario: User can see the saved configurations
    When I insert initial data
    And I initialize the App
    Then I am on Main screen
    And I see "Linux" element