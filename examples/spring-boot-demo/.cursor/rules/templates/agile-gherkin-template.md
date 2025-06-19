Feature: [Feature Name From Input]

[IF Background Steps were provided THEN
Background:
  [List Background Steps From Input, each prefixed with 'Given ' or 'And ' appropriately]
ENDIF]

[FOR EACH Scenario collected in Step 7 & 8 DO
Scenario: [Scenario Title From Input]
  Given [Given From Input]
  When [When From Input]
  Then [Then From Input]
  [IF Data Examples for scenario were provided THEN include them, possibly as docstrings or tables formatted appropriately for Gherkin ENDIF]

ENDLOOP]
