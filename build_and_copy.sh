#!/bin/sh
mvn clean package -P prod && scp target/termed.war viikuna:
