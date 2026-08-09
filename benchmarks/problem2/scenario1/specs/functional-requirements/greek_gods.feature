Feature: Greek Gods API
  As an API consumer
  I want to retrieve information about Greek gods
  So that I can use this data in my application

  Background:
    Given the Greek Gods API service is running
    And the API base URL is "http://localhost:8080"

  Scenario: Successfully retrieve all Greek gods
    When I send a GET request to "/api/v1/gods/greek"
    Then the response status code should be 200
    And the response should be in JSON format
    And the response should contain a list of Greek gods
    And each god in the response should have the following attributes:
      | attribute | type    |
      | id        | integer |
      | name      | string  |

  Scenario: Verify a specific Greek god in the response
    When I send a GET request to "/api/v1/gods/greek"
    Then the response status code should be 200
    And the response should contain a god with the following details:
      | id   | name |
      | 1    | Zeus |
