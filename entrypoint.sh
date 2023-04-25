#!/bin/sh
DATE=$(date "+%Y%m%d")
#TIME=$(date +"%H%M%S")

export DATETIME=$DATE

#echo $DATETIME
set -ex
java -Djava.security.egd=file:/dev/./urandom -jar /assignment.jar