FROM java:alpine

ADD target/uploadform-0.0.1-SNAPSHOT.jar /app/uploadform.jar
ENTRYPOINT java -Djava.awt.headless=true -jar /app/uploadform.jar
