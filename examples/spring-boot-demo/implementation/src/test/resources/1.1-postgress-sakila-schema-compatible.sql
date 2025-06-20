-- Compatible Sakila Schema for TestContainers
-- Simplified version removing postgres user dependencies and complex ownership

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: mpaa_rating; Type: TYPE
--

CREATE TYPE mpaa_rating AS ENUM (
    'G',
    'PG',
    'PG-13',
    'R',
    'NC-17'
);

--
-- Name: year; Type: DOMAIN
--

CREATE DOMAIN year AS integer
    CONSTRAINT year_check CHECK (((VALUE >= 1901) AND (VALUE <= 2155)));

--
-- Create film table with Sakila schema structure
--

CREATE TABLE film (
    film_id SERIAL PRIMARY KEY,
    title character varying(255) NOT NULL,
    description text,
    release_year year,
    language_id smallint NOT NULL,
    original_language_id smallint,
    rental_duration smallint DEFAULT 3 NOT NULL,
    rental_rate numeric(4,2) DEFAULT 4.99 NOT NULL,
    length smallint,
    replacement_cost numeric(5,2) DEFAULT 19.99 NOT NULL,
    rating mpaa_rating DEFAULT 'G'::mpaa_rating,
    last_update timestamp without time zone DEFAULT now() NOT NULL,
    special_features text[],
    fulltext tsvector DEFAULT to_tsvector('english', '')
);

--
-- Create language table (required for foreign key)
--

CREATE TABLE language (
    language_id SERIAL PRIMARY KEY,
    name character(20) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);

--
-- Create indexes for performance
--

CREATE INDEX idx_title ON film USING btree (title);
CREATE INDEX film_fulltext_idx ON film USING gist (fulltext);

--
-- Add foreign key constraint
--

ALTER TABLE ONLY film
    ADD CONSTRAINT film_language_id_fkey FOREIGN KEY (language_id) REFERENCES language(language_id) ON UPDATE CASCADE ON DELETE RESTRICT;

--
-- Create trigger function for last_updated
--

CREATE OR REPLACE FUNCTION last_updated() RETURNS trigger
    AS $$
BEGIN
    NEW.last_update = CURRENT_TIMESTAMP;
    RETURN NEW;
END $$
    LANGUAGE plpgsql;

--
-- Create trigger for film table
--

CREATE TRIGGER last_updated
    BEFORE UPDATE ON film
    FOR EACH ROW
    EXECUTE PROCEDURE last_updated();

CREATE TRIGGER last_updated
    BEFORE UPDATE ON language
    FOR EACH ROW
    EXECUTE PROCEDURE last_updated();

-- Print schema completion message
SELECT 'Sakila compatible schema created successfully' as schema_status; 