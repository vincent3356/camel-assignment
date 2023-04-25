FROM centos:7   # or windows
RUN yum -y update
RUN yum -y remove java
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel

MAINTAINER vincent
COPY target/camel-sprint-boot-service-a-0.0.1-SNAPSHOT assignment.jar
WORKDIR /
VOLUME /tmp
EXPOSE 8080

# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/assignment.jar"]

COPY entrypoint.sh /
ENTRYPOINT ["sh", "/entrypoint.sh"]

