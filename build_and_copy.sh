#!/bin/sh
mvn clean package -DpropertyFilePath=/var/lib/termed/app.properties && scp target/termed.war viikuna:
