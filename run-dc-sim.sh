#!/bin/bash

toopVersion=0.10.6-SNAPSHOT

java -jar toop-simulator-${toopVersion}.jar \
     -mode DC \
     -dcURL "http://localhost:8082/to-dp" \
     -commanderJarBundle toop-commander-${toopVersion}.jar \
     -simPort 50000
