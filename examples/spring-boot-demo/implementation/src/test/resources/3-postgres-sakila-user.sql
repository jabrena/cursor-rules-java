-- The database dump used the "postgres" user. But we want everything to
-- be owned by the sakila user.
ALTER SCHEMA public OWNER TO sakila;
ALTER DATABASE sakila OWNER TO sakila;
REASSIGN OWNED BY postgres TO sakila;
DROP OWNED BY postgres;
DROP USER IF EXISTS postgres;

SELECT 'sakiladb/postgres has successfully initialized.' AS sakiladb_completion_message;
