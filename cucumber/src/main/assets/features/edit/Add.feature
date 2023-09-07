@android @add
Feature: Edit screen feature

  @android @add_1
  Scenario: User can save a network configuration
    When I initialize the App
    Then I am on Main screen
    And I click on add network configuration button
    Then I type "Linux" in field "Name"
    And I type "192.168.1.1" in field "Server"
    And I type "Public" in field "Shared path"
    And I type "Miguel" in field "User"
    And I reveal the password field
    And I type "Password" in field "Password"
    And I start listening for "Tracking"
    And I click the save button
    Then I am on Main screen

  @android @add_2
  Scenario: User is notified about empty server field
    When I initialize the App
    Then I am on Main screen
    And I click on add network configuration button
    And I type "Public" in field "Shared path"
    And I click the save button
    Then I see error for field "Server"

  @android @add_3
  Scenario: User is notified about empty shared path field
    When I initialize the App
    Then I am on Main screen
    And I click on add network configuration button
    And I type "192.168.1.1" in field "Server"
    And I click the save button
    Then I see error for field "Shared path"