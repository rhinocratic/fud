FROM postgres:14.4-alpine
ENV POSTGRES_PASSWORD fud
ENV POSTGRES_DB fud
RUN mkdir -p /docker-entrypoint-initdb.d
COPY ./sql/000-init.sql /docker-entrypoint-initdb.d/000-init.sql
COPY ./sql/migrations/*.up.sql /docker-entrypoint-initdb.d/