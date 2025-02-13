FROM amazoncorretto:17
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=5 \
  CMD curl -f http://localhost:8888/actuator/health || exit 1