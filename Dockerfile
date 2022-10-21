FROM adoptopenjdk/openjdk11:jre-11.0.15_10-debianslim

WORKDIR /opt/app
ARG JAR_FILE=target/lending-service.jar

ARG ARCH
ARG HOST
#for logstash
RUN apt update && apt-get install lsb-base
RUN curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.0.0-$ARCH.deb
RUN dpkg -i filebeat-8.0.0-$ARCH.deb
RUN apt update

COPY filebeat.yml /etc/filebeat/filebeat.yml
RUN sed -i "s/IP/$HOST/" /etc/filebeat/filebeat.yml
COPY start.sh .
RUN chmod +x start.sh && chmod go-w /etc/filebeat/filebeat.yml

COPY ${JAR_FILE} app.jar
CMD ./start.sh

