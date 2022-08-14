# syntax=docker/dockerfile:1.3
FROM postgres:14.4-alpine
# RUN --mount=type=secret,id=POSTGRES_PASSWORD \
#     POSTGRES_PASSWORD=$(cat /run/secrets/POSTGRES_PASSWORD) echo ${POSTGRES_PASSWORD}
ENV POSTGRES_DB fud
ENV POSTGRES_USER fud
ENV POSTGRES_PASSWORD fud

RUN mkdir -p /docker-entrypoint-initdb.d
COPY ./sql/000-init.sql /docker-entrypoint-initdb.d/000-init.sql
COPY ./sql/seed-data.sql /docker-entrypoint-initdb.d/seed-data.sql
COPY ./sql/migrations/*.up.sql /docker-entrypoint-initdb.d/