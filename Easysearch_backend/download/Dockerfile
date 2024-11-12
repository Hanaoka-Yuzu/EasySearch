FROM openjdk:17 as production-stage
# ENV db_pass='' db_hostname='10.0.16.9'
COPY ./target/ /target/
CMD java -jar target/download-*.jar