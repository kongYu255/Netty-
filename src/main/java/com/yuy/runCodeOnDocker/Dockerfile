FROM java:8
WORKDIR /data/RunOnDocker
COPY ./certs/* /data/RunOnDocker/certs/
COPY ./config/* /data/RunOnDocker/config/
COPY ./RunOnDocker.jar /data/RunOnDocker/
EXPOSE 8081
CMD java -jar RunOnDocker.jar
