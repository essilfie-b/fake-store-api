# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set working directory
WORKDIR /app
# Copy all project files
COPY . .
# Run tests and generate allure-results
RUN mvn clean test
# ---------- RUNTIME STAGE ----------
FROM openjdk:17-jdk-slim
# Install Allure CLI
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://github.com/allure-framework/allure2/releases/download/2.24.1/allure-2.24.1.zip && \
    unzip allure-2.24.1.zip && \
    mv allure-2.24.1 /opt/allure && \
    ln -s /opt/allure/bin/allure /usr/bin/allure && \
    apt-get clean
# Copy test results from build stage
COPY --from=build /app/target/allure-results /allure-results
# Set working directory for runtime
WORKDIR /allure-results
# Expose Allure report port
EXPOSE 4040
# Start Allure server
CMD ["allure", "serve", "/allure-results"]