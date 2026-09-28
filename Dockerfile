# 배포용 이미지 (Railway). develop 에 머지되면 Railway 가 이 파일로 다시 빌드한다
# 로컬 개발은 그대로 ./gradlew bootRun + .env 를 쓴다. 이 파일은 배포에만 쓰인다

# 1단계: 빌드. build.gradle 이 Java 17 을 요구한다
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null
COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# 2단계: 실행. 빌드 도구 없이 jar 하나만 담는다
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
# 한글 깨짐 방지(UTF-8), 저장 시각을 한국 시간으로, 메모리는 받은 한도의 75% 까지
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul -XX:MaxRAMPercentage=75"
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
