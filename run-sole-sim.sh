#!/bin/bash

toopVersion=0.10.6-SNAPSHOT

java -jar toop-simulator-${toopVersion}.jar \
     -mode SOLE \
     -dcURL "http://localhost:8081/to-dc" \
     -dpURL "http://localhost:8082/to-dc" \
     -simPort 50000