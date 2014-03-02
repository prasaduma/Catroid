Feature: When script restarting

  A When script should be restarted when the message is broadcast again while the script is still running.

  Background:
    Given I have a Program
    And this program has an Object 'test object'

  Scenario: A program with two start scripts and one When script
    Given 'test object' has a Start script
    And this script has a Broadcast 'hello' brick
    Given 'test object' has a Start script
    And this script has a Wait 100 milliseconds brick
    And this script has a Broadcast 'hello' brick
    Given 'test object' has a WhenBroadcastReceived 'hello' script
    And this script has a Print brick with
      """
      I am the When 'hello' script (1).
      """
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with
      """
      I am the When 'hello' script (2).
      """
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with
      """
      I am the When 'hello' script (3).
      """
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output
      """
      I am the When 'hello' script (1).
      I am the When 'hello' script (1).
      I am the When 'hello' script (2).
      I am the When 'hello' script (3).
      """

  Scenario: A WhenBroadcastReceived script is restarted when the message it reacts to is sent from within the script
    Given 'test object' has a Start script
    And this script has a Broadcast 'print a immediately and send broadcast message again' brick
    Given 'test object' has a WhenBroadcastReceived 'print a immediately and send broadcast message again' script
    And this script has a Print brick with 'a'
    And this script has a Broadcast 'print a immediately and send broadcast message again' brick
    When I start the program
    And I wait for at least 1000 milliseconds
    Then I should see at least 'aaaaaaaaaa'
