Feature: Query Films Starting with Letter A
  As a film database user
  I want to query movies that start with the letter "A"
  So that I can quickly find and browse films in alphabetical order starting with "A"

  Background:
    Given the film database is available
    And the database contains the Sakila sample data

  Scenario: Successfully retrieve films starting with "A"
    Given the film database contains movies with various titles
    When I request films that start with the letter "A"
    Then I should receive a list of films with titles beginning with "A"
    And the response should include film ID and title for each movie
    And the result should contain exactly 46 films
    And all returned film titles should start with the letter "A"

  Scenario: API endpoint responds correctly
    Given the film query service is running
    When I make a GET request to "/api/v1/films" with filter parameter for letter "A"
    Then I should receive a HTTP 200 OK response
    And the response should be in JSON format
    And each film object should contain "film_id" and "title" fields

  Scenario: Database query performance
    Given the film database is populated with sample data
    When I execute the film query for titles starting with "A"
    Then the query should complete within 2 seconds
    And the database should use the appropriate index for title searches

  Scenario: Handle empty results gracefully
    Given the film database contains no movies starting with "A"
    When I request films that start with the letter "A"
    Then I should receive an empty list
    And the response should have HTTP 200 OK status
    And the response should include a message indicating no films found

  Scenario: Invalid query parameter handling
    Given the film query service is running
    When I make a GET request to "/api/v1/films" with an invalid filter parameter
    Then I should receive a HTTP 400 Bad Request response
    And the response should include an error message explaining the invalid parameter 