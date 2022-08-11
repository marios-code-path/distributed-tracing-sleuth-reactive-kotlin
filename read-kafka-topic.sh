#!/usr/bin/env bash

docker exec --interactive --tty broker \
kafka-console-consumer --bootstrap-server broker:9092 \
                       --topic zipkin \
                       --from-beginning