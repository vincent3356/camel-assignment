FROM centos:7
RUN yum -y update
RUN yum -y remove java
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel

COPY target/balance-1.0.0.jar balance.jar
WORKDIR /
VOLUME /tmp
EXPOSE 8080

# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/balance.jar"]

COPY entrypoint.sh /
ENTRYPOINT ["sh", "/entrypoint.sh"]

