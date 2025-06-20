-- Initialize Sakila test database schema and data
-- This script creates the film table and populates it with test data
-- that matches the expectations from the acceptance tests

-- Create film table
CREATE TABLE IF NOT EXISTS film (
    film_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

-- Clear any existing data
TRUNCATE TABLE film RESTART IDENTITY;

-- Insert test data for films starting with 'A' (46 films expected)
INSERT INTO film (title) VALUES
('ACADEMY DINOSAUR'), ('ACE GOLDFINGER'), ('ADAPTATION HOLES'), ('AFFAIR PREJUDICE'), 
('AFRICAN EGG'), ('AGENT TRUMAN'), ('AIRPLANE SIERRA'), ('AIRPORT POLLOCK'), 
('ALABAMA DEVIL'), ('ALADDIN CALENDAR'), ('ALAMO VIDEOTAPE'), ('ALASKA PHANTOM'), 
('ALI FOREVER'), ('ALICE FANTASIA'), ('ALIEN CENTER'), ('ALLEY EVOLUTION'), 
('ALONE TRIP'), ('ALTER VICTORY'), ('AMADEUS HOLY'), ('AMELIE HELLFIGHTERS'), 
('AMERICAN CIRCUS'), ('AMISTAD MIDSUMMER'), ('ANACONDA CONFESSIONS'), ('ANALYZE HOOSIERS'), 
('ANGELS LIFE'), ('ANNIE IDENTITY'), ('ANONYMOUS HUMAN'), ('ANTHEM LUKE'), 
('ANTITRUST TOMATOES'), ('ANYTHING SAVANNAH'), ('APACHE DIVINE'), ('APOCALYPSE FLAMINGOS'), 
('APOLLO TEEN'), ('ARABIA DOGMA'), ('ARACHNOPHOBIA ROLLERCOASTER'), ('ARMAGEDDON LOST'), 
('ARMY FLINTSTONES'), ('ARSENIC INDEPENDENCE'), ('ARTIST COLDBLOODED'), ('ATLANTIS CAUSE'), 
('ATTACKS HATE'), ('ATTRACTION NEWTON'), ('AUTUMN CROW'), ('AVIATOR POLLOCK'), 
('AWAKENINGS BED'), ('AWESOME GUMP');

-- Insert a few test films for other letters to test edge cases
INSERT INTO film (title) VALUES
('DANCING FEVER'), ('EAGLE LOVERBOY'), ('FAMILY SWEET'), ('GASOLINE DUDE'),
('HAMLET WISDOM'), ('ICE CROSSING'), ('JACKET FRISCO'), ('KARATE MOON'),
('LABOR TRACY'), ('MAGIC MALLRATS'), ('NATURAL STOCK'), ('OCEAN THIRTEEN'),
('PACIFIC AMISTAD'), ('QUEEN LUKE'), ('RADIO JACK'), ('SATURDAY LAMBS'),
('TAXI KICK'), ('UNFAITHFUL KILL'), ('VALLEY PACKER'), ('WAGON JAWS'),
('YOUNG LANGUAGE'), ('ZION GARDEN');

-- Create index on title for performance (as expected by acceptance tests)
CREATE INDEX IF NOT EXISTS idx_film_title ON film(title);

-- Verify the data
SELECT 
    LEFT(title, 1) as starting_letter, 
    COUNT(*) as film_count 
FROM film 
WHERE LEFT(title, 1) IN ('A', 'B', 'C')
GROUP BY LEFT(title, 1) 
ORDER BY starting_letter; 