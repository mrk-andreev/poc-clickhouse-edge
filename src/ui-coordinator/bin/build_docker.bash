#!/bin/bash

set -e

docker build --compress -t markandreev/poc-clickhouse-remote-ui-server:latest -f Dockerfile	.
