FROM gcr.io/distroless/java21

COPY build/libs/pam-stilling-feed-admin-all.jar ./app.jar
ENV JAVA_OPTS="-XX:-OmitStackTraceInFastThrow -Xms256m -Xmx2304m"
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app.jar"]
