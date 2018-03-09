#!/bin/bash

# remove dangling volume
docker volume rm `docker volume ls -q -f dangling=true`

# cleanup docker compose
docker-compose down