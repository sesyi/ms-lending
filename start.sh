#!/bin/bash

service filebeat start
service filebeat status

exec java -jar app.jar

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?