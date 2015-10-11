#! /bin/bash
HOST_PIPE_DIR="$(cd $(dirname ${BASH_SOURCE[0]}) && pwd)"
exec java -jar "${HOST_PIPE_DIR}"/target/scala-2.11/GCMN\ Host\ Pipe-assembly-0.1-SNAPSHOT.jar
