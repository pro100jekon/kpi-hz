FROM amazoncorretto:17.0.13-al2023-headless
LABEL authors="yevhenii_s"

WORKDIR app
COPY ./target/kpi-0.0.1-SNAPSHOT.jar app.jar
#COPY ./run.sh run.sh
#RUN chmod 777 /app/run.sh

#ENTRYPOINT ["./run.sh"]
#ENTRYPOINT ["java", "-jar", "app.jar", "majority"]
ENTRYPOINT ["java", "-jar", "app.jar", "w1"]