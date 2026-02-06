# Use a more recent Java version (OpenJDK 8 is very old)
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the JAR file
COPY target/enquiry-api-1.0.0.jar app.jar

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -S -D -u 1001 -G appuser appuser

# Change ownership of the app directory
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Set environment variable for CORS
ENV CORS_ALLOWED_ORIGINS=*

# Expose port (Cloud Run uses 8080 by default)
EXPOSE 8080

# Health check with better timeout for slow starts
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:8080/api/enquiries/health || exit 1

# Run with optimized JVM settings for containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=8080", "-jar", "app.jar"] ..