#!/bin/bash

set -e

docker build --compress -t markandreev/poc-clickhouse-remote-app-server:latest -f src/main/docker/Dockerfile	.
