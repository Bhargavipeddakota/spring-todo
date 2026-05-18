# Why Use a Multi-Stage Docker Build?

A multi-stage Docker build helps create smaller, faster, and more secure production images by separating the **build environment** from the **runtime environment**.

Instead of shipping the entire Gradle toolchain and source code into production, we only copy the final executable JAR into the runtime image.

---

# Problems With the Original Dockerfile

Original Dockerfile:

```dockerfile
FROM gradle:jdk25
WORKDIR /app
COPY . .
RUN gradle clean build
CMD ["java", "-jar", "build/libs/todo-0.0.1-SNAPSHOT.jar"]
```

## Issues

### 1. Large Final Image

The image contains:

* Gradle
* Build cache
* Source code
* Temporary build files
* JDK tooling

This significantly increases image size.

---

### 2. Security Concerns

Production containers should contain only what is required to run the application.

Keeping build tools inside runtime containers increases the attack surface.

---

### 3. Slower Deployments

Larger images:

* Take longer to build
* Take longer to push/pull
* Consume more storage
* Increase CI/CD execution time

---

# Multi-Stage Build Approach

We split the Docker build into two stages:

## Stage 1 — Builder

Purpose:

* Compile the application
* Download dependencies
* Produce the executable JAR

Image used:

```dockerfile
FROM gradle:8.8-jdk25 AS builder
```

This image contains:

* Gradle
* Full JDK
* Build tooling

---

## Stage 2 — Runtime

Purpose:

* Run the Spring Boot application

Image used:

```dockerfile
FROM eclipse-temurin:25-jre
```

This image contains only:

* Java Runtime Environment (JRE)

It does **not** include:

* Gradle
* Source code
* Build caches
* Compiler tools

This makes the final image much leaner.

---

# Why the COPY Instructions Are Ordered This Way

## Step 1 — Copy Gradle Metadata First

```dockerfile
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
```

These files define:

* Dependencies
* Build configuration
* Gradle wrapper configuration

They change less frequently than application source code.

---

# Why This Matters for Docker Layer Caching

Docker caches layers independently.

If dependencies have not changed, Docker can reuse the cached dependency layer instead of downloading everything again.

This dramatically speeds up rebuilds.

---

## Step 2 — Download Dependencies

```dockerfile
RUN ./gradlew dependencies --no-daemon || true
```

This preloads Gradle dependencies into a dedicated cached layer.

Why before copying source code?

Because source code changes frequently, while dependencies change less often.

Without this optimization:

* Every code change would invalidate the dependency download layer
* Gradle dependencies would be downloaded repeatedly

---

## Step 3 — Copy Source Code

```dockerfile
COPY src ./src
```

Source code changes often.

By copying it after dependency resolution:

* Only compilation layers rebuild when code changes
* Dependency layers remain cached

---

## Step 4 — Build the Application

```dockerfile
RUN ./gradlew clean bootJar --no-daemon
```

This produces the executable Spring Boot JAR.

---

# Why Only the JAR Is Copied to Runtime Image

```dockerfile
COPY --from=builder /app/build/libs/*.jar app.jar
```

This ensures the runtime image contains only:

* The application JAR
* Java runtime

Nothing else from the build environment is included.

---

# Benefits of Multi-Stage Builds

| Benefit             | Explanation                                   |
| ------------------- | --------------------------------------------- |
| Smaller image size  | Only runtime artifacts are shipped            |
| Faster deployments  | Smaller images transfer faster                |
| Better security     | No build tools in production                  |
| Improved caching    | Dependency layers reused                      |
| Cleaner runtime     | Only required files exist                     |
| Lower storage usage | Less disk consumption in registries and hosts |

---

# Final Outcome

The resulting image is:

* Lean
* Production-ready
* Faster to build
* Faster to deploy
* More secure
* Easier to maintain

This is the recommended approach for containerizing modern Spring Boot applications.
