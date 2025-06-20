-- Test Data for Film Query Acceptance Tests
-- This file contains only the data inserts - schema is loaded separately from 1-postgres-sakila-schema.sql

-- Insert required language record (required by foreign key constraint)
INSERT INTO language (language_id, name) VALUES (1, 'English') ON CONFLICT (language_id) DO NOTHING;

-- Insert focused test data for our scenarios
-- Films starting with 'A' (46 total to match expected test results)
INSERT INTO film (title, description, release_year, language_id, length, rating) VALUES
('ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist', 2006, 1, 86, 'PG'),
('ACE GOLDFINGER', 'A Astounding Epistle of a Database Administrator', 2006, 1, 48, 'G'),
('ADAPTATION HOLES', 'A Astounding Reflection of a Lumberjack', 2006, 1, 50, 'NC-17'),
('AFFAIR PREJUDICE', 'A Fanciful Documentary of a Frisbee', 2006, 1, 117, 'G'),
('AFRICAN EGG', 'A Fast-Paced Documentary of a Pastry Chef', 2006, 1, 130, 'G'),
('AGENT TRUMAN', 'A Intrepid Panorama of a Robot And a Boy', 2006, 1, 169, 'PG'),
('AIRPLANE SIERRA', 'A Touching Saga of a Hunter And a Butler', 2006, 1, 62, 'PG-13'),
('AIRPORT POLLOCK', 'A Epic Tale of a Moose And a Girl', 2006, 1, 54, 'R'),
('ALABAMA DEVIL', 'A Thoughtful Panorama of a Database Administrator', 2006, 1, 114, 'PG-13'),
('ALADDIN CALENDAR', 'A Action-Packed Tale of a Man And a Lumberjack', 2006, 1, 63, 'NC-17'),
('ALAMO VIDEOTAPE', 'A Boring Epistle of a Butler And a Cat', 2006, 1, 126, 'G'),
('ALASKA PHANTOM', 'A Fanciful Saga of a Hunter And a Pastry Chef', 2006, 1, 136, 'PG'),
('ALI FOREVER', 'A Action-Packed Drama of a Dentist And a Crocodile', 2006, 1, 150, 'PG'),
('ALICE FANTASIA', 'A Emotional Drama of a A Shark And a Database Administrator', 2006, 1, 94, 'NC-17'),
('ALIEN CENTER', 'A Brilliant Drama of a Cat And a Mad Scientist', 2006, 1, 46, 'NC-17'),
('ALLEY EVOLUTION', 'A Fast-Paced Drama of a Robot And a Composer', 2006, 1, 180, 'NC-17'),
('ALONE TRIP', 'A Fast-Paced Character Study of a Composer', 2006, 1, 82, 'R'),
('ALTER VICTORY', 'A Thoughtful Drama of a Composer And a Feminist', 2006, 1, 57, 'PG-13'),
('AMADEUS HOLY', 'A Emotional Display of a Pioneer And a Technical Writer', 2006, 1, 113, 'PG'),
('AMELIE HELLFIGHTERS', 'A Boring Drama of a Woman And a Squirrel', 2006, 1, 79, 'R'),
('AMERICAN CIRCUS', 'A Insightful Drama of a Girl And a Astronaut', 2006, 1, 129, 'R'),
('AMISTAD MIDSUMMER', 'A Emotional Character Study of a Dentist', 2006, 1, 85, 'G'),
('ANALYZE HOOSIERS', 'A Thoughtful Display of a Explorer And a Pastry Chef', 2006, 1, 181, 'R'),
('ANASTASIA HOLY', 'A Serene Reflection of a Pioneer And a Mad Scientist', 2006, 1, 86, 'R'),
('ANGELS LIFE', 'A Thoughtful Display of a Woman And a Astronaut', 2006, 1, 74, 'G'),
('ANNIE IDENTITY', 'A Amazing Panorama of a Pastry Chef And a Boat', 2006, 1, 86, 'G'),
('ANONYMOUS HUMAN', 'A Amazing Reflection of a Database Administrator', 2006, 1, 179, 'NC-17'),
('ANTHEM LUKE', 'A Touching Panorama of a Waitress And a Woman', 2006, 1, 91, 'PG-13'),
('ANTITRUST TOMATOES', 'A Fateful Yarn of a Womanizer And a Feminist', 2006, 1, 168, 'NC-17'),
('ANYTHING SAVANNAH', 'A Epic Story of a Pastry Chef And a Woman', 2006, 1, 82, 'R'),
('APACHE DIVINE', 'A Awe-Inspiring Reflection of a Pastry Chef', 2006, 1, 92, 'NC-17'),
('APOCALYPSE FLAMINGOS', 'A Astounding Story of a Dog And a Squirrel', 2006, 1, 119, 'R'),
('APOLLO TEEN', 'A Action-Packed Reflection of a Crocodile', 2006, 1, 153, 'PG-13'),
('ARABIA DOGMA', 'A Touching Epistle of a Madman And a Mad Scientist', 2006, 1, 62, 'NC-17'),
('ARACHNOPHOBIA ROLLERCOASTER', 'A Action-Packed Reflection of a Pastry Chef', 2006, 1, 147, 'PG-13'),
('ARGONAUTS TOWN', 'A Emotional Epistle of a Forensic Psychologist', 2006, 1, 127, 'PG-13'),
('ARIZONA BANG', 'A Brilliant Panorama of a Mad Scientist And a Mad Scientist', 2006, 1, 121, 'PG'),
('ARK RIDGEMONT', 'A Beautiful Yarn of a Pioneer And a Monkey', 2006, 1, 68, 'NC-17'),
('ARMAGEDDON LOST', 'A Fast-Paced Tale of a Boat And a Teacher', 2006, 1, 99, 'G'),
('ARMY FLINTSTONES', 'A Boring Saga of a Database Administrator', 2006, 1, 148, 'R'),
('ARSENIC INDEPENDENCE', 'A Fanciful Documentary of a Mad Scientist', 2006, 1, 137, 'PG'),
('ARTIST COLDBLOODED', 'A Stunning Reflection of a Robot And a Moose', 2006, 1, 170, 'NC-17'),
('ATLANTIS CAUSE', 'A Thrilling Yarn of a Feminist And a Hunter', 2006, 1, 170, 'G'),
('ATTACKS HATE', 'A Fast-Paced Panorama of a Technical Writer', 2006, 1, 113, 'PG-13'),
('ATTRACTION NEWTON', 'A Astounding Panorama of a Composer And a Frisbee', 2006, 1, 83, 'PG-13'),
('AURORA GRADUATION', 'A Astounding Drama of a Crocodile And a Mad Scientist', 2006, 1, 130, 'R'),
-- Films starting with other letters for comprehensive testing
('BABY HALL', 'A Boring Epistle of a A Shark And a Girl', 2006, 1, 153, 'NC-17'),
('BACHELOR DORADO', 'A Awe-Inspiring Reflection of a Boat And a Explorer', 2006, 1, 126, 'PG-13'),
('CHICAGO NORTH', 'A Fateful Yarn of a Mad Cow And a Waitress', 2006, 1, 185, 'PG-13'),
('DOOR PAINT', 'A Thrilling Story of a Woman And a Boat', 2006, 1, 109, 'PG'),
('ZORRO ARK', 'A Intrepid Panorama of a Mad Scientist And a Boy', 2006, 1, 50, 'NC-17');

-- Verify data integrity
SELECT 
    COUNT(*) as total_films,
    COUNT(CASE WHEN title LIKE 'A%' THEN 1 END) as films_starting_with_a
FROM film;

-- Print completion message for TestContainer waiting
SELECT 'Test film database with Sakila schema initialized successfully' as initialization_status; 