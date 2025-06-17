# Feature 1: Simple Query / List movies starting by A

**Schema:** https://github.com/sakiladb/postgres/blob/master/1-postgres-sakila-schema.sql

```sql
SELECT FILM_ID, TITLE
FROM film
WHERE title LIKE 'A%'
```

**Note:** The query returns 46 rows.

```bash
docker run -e POSTGRES_PASSWORD=sakila --rm --name sakiladb -p 5432:5432 -d "sakiladb/postgres"
./mvnw spring-boot:run
curl http://localhost:8080/api/v1/films
docker stop sakiladb
```

## References

- https://hub.docker.com/r/sakiladb/postgres
- https://github.com/sakiladb/postgres
