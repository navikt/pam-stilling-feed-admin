FROM gcr.io/distroless/java25-debian13:latest@sha256:1d7a0cea4653f62be34a5b9b1da82a4dd097ae8935d1d3f4ab84146e0396fd2b

COPY build/libs/pam-stilling-feed-admin-all.jar ./app.jar
ENV JAVA_OPTS="-XX:-OmitStackTraceInFastThrow -Xms256m -Xmx2304m"
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app.jar"]
