#!/bin/bash

toopVersion=0.10.6-SNAPSHOT

java -jar toop-simulator-${toopVersion}.jar \
     -mode DP \
     -dcURL "http://localhost:8081/to-dc" \
     -commanderJarBundle toop-commander-${toopVersion}.jar \
     -simPort 50000