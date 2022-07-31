#!/usr/bin/env bash

POSTGRES_PASSWORD=fud docker buildx build --secret id=POSTGRES_PASSWORD .
# docker build -t fud .