FROM maven
RUN apt-get update && apt-get install bzip2 sudo
ADD src/ /opt/unikitty/src/
ADD app/ /opt/unikitty/app/
ADD config/ /opt/unikitty/config/
ADD public/ /opt/unikitty/public/
ADD tests/ /opt/unikitty/tests/
ADD vendor/ /opt/unikitty/vendor/
ADD pom.xml /opt/unikitty/
ADD .bowerrc /opt/unikitty/
ADD *.js /opt/unikitty/
ADD *.json /opt/unikitty/
ADD .jshintrc /opt/unikitty/
ADD .ember-cli /opt/unikitty/


WORKDIR /opt/unikitty
RUN mvn package -DskipTests -Dmaven.test.skip=true

VOLUME /opt/config

EXPOSE 8080

CMD java -jar /opt/unikitty/target/*.jar --spring.config.location=file:/opt/config/application.properties
