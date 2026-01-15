# Spring AI MCP Server Migration Guide

**Goal:** Migrate broken/old Spring AI MCP servers to Spring AI 1.1.2 (latest stable).

**Copy this file to share with other Claude Code sessions - it's self-contained.**

---

## Quick Fix (TL;DR)

If you just want to upgrade quickly:

```xml
<!-- pom.xml: Change version and artifact -->
<spring-ai.version>1.1.2</spring-ai.version>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>  <!-- or webflux -->
</dependency>

<!-- Remove Spring milestones repository - not needed -->
```

```yaml
# application.yml: Ensure web type is set and protocol
spring:
  main:
    web-application-type: servlet  # or reactive for webflux
  ai:
    mcp:
      server:
        protocol: STREAMABLE  # Use STREAMABLE for HTTP, SSE for Server-Sent Events
```

Build and run. No code changes needed!

---

## Problem

Old MCP servers fail because:
- Using outdated Spring AI versions (1.0.0-M6, M7, M8)
- Old artifact names (naming changed in 1.1.x)
- Missing configuration
- Wrong CORS setup for reactive/servlet

## Solution: Upgrade to 1.1.2

Spring AI 1.1.2 is the latest stable (Nov 2025). Both WebMVC and WebFlux work.

---

## Step 1: Update pom.xml

### Full Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
    </parent>

    <properties>
        <java.version>17</java.version>
        <spring-ai.version>1.1.2</spring-ai.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>${spring-ai.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- Choose ONE: -->

        <!-- Option 1: WebMVC (Servlet/Tomcat) - Simpler -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
        </dependency>

        <!-- Option 2: WebFlux (Reactive/Netty) - For high concurrency
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-mcp-server-webflux</artifactId>
        </dependency>
        -->
    </dependencies>

    <!-- No repositories needed - 1.1.2 is in Maven Central -->
</project>
```

### Key Changes

**Artifact naming changed:**
| Old (1.0.0-M6) | New (1.1.2) |
|---|---|
| `spring-ai-mcp-server-webmvc-spring-boot-starter` | `spring-ai-starter-mcp-server-webmvc` |
| `spring-ai-mcp-server-webflux-spring-boot-starter` | `spring-ai-starter-mcp-server-webflux` |

**Repository not needed:** 1.1.2 is in Maven Central (remove milestones repo)

---

## Step 2: Update application.yml

```yaml
server:
  port: 8080

spring:
  main:
    # CRITICAL: Must match your dependency choice
    web-application-type: servlet  # for WebMVC
    # web-application-type: reactive  # for WebFlux
  ai:
    mcp:
      server:
        name: your-server-name
        version: 0.0.1
        type: SYNC
        protocol: STREAMABLE  # Use STREAMABLE for HTTP, SSE for Server-Sent Events (default)
```

---

## Step 3: Tool Definitions (Usually No Changes)

Tools should use `@Tool` annotation:

```java
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class MyTools {

    @Tool(description = "What this tool does")
    public String myTool(@ToolParam(description = "Parameter") String param) {
        return "result";
    }
}
```

---

## Step 4: Tool Registration (Usually No Changes)

```java
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider myToolCallbacks(MyTools myTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(myTools)
                .build();
    }
}
```

---

## Step 5: CORS Configuration

### WebMVC (Servlet)

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

### WebFlux (Reactive)

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
```

**Critical:** WebMVC uses `WebMvcConfigurer`, WebFlux uses `CorsWebFilter`

---

## Step 6: Build and Test

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run
```

**Verify startup log:**
- WebMVC: `Tomcat started on port 8080`
- WebFlux: `Netty started on port 8080`
- Both: `Registered tools: X`

**Test HTTP endpoint:**
```bash
curl -v http://localhost:8080/mcp
```

Expected: `HTTP 200`, streamable HTTP connection

**Connect to Claude Code:**
```bash
claude mcp add --transport http your-server-name http://localhost:8080/mcp
```

**Verify in Claude Code:**
```
/mcp
```

Should list your server with tools.

---

## Common Issues

### Issue: Build fails "version missing"
**Cause:** Wrong artifact name
**Fix:** Use `spring-ai-starter-mcp-server-webmvc` (not old naming)

### Issue: "Tomcat started" but expected "Netty"
**Cause:** Wrong web-application-type
**Fix:** Set `web-application-type: reactive` for WebFlux

### Issue: CORS errors
**Cause:** Wrong CORS config for stack
**Fix:** WebMVC = `WebMvcConfigurer`, WebFlux = `CorsWebFilter`

### Issue: Tools not registered
**Cause:** Missing `@Service` or no `ToolCallbackProvider` bean
**Fix:** Ensure tool class has `@Service` and config creates bean

### Issue: HTTP endpoint 404
**Cause:** Wrong dependency or streamable-http config
**Fix:** Verify correct starter artifact for WebMVC vs WebFlux and ensure streamable-http is enabled in application.yml

---

## Reference Implementation

This repository contains a working example:

**Files to study:**
- `pom.xml` - Correct dependencies
- `src/main/resources/application.yml` - Server config
- `src/main/java/com/example/mcppoc/tools/DemoTools.java` - Tool examples
- `src/main/java/com/example/mcppoc/config/McpConfig.java` - Tool registration
- `src/main/java/com/example/mcppoc/config/CorsConfig.java` - CORS setup

---

## Verification Checklist

After migration:

- [ ] pom.xml has Spring AI 1.1.2
- [ ] Artifact is `spring-ai-starter-mcp-server-webmvc` or `webflux`
- [ ] application.yml has correct `web-application-type`
- [ ] Tools use `@Tool` annotation
- [ ] Tool class has `@Service`
- [ ] ToolCallbackProvider bean exists
- [ ] CORS config matches stack (WebMVC vs WebFlux)
- [ ] Protocol is set in application.yml (STREAMABLE for HTTP, SSE for Server-Sent Events)
- [ ] `mvn clean package` succeeds
- [ ] Server starts (Tomcat or Netty)
- [ ] curl `/mcp` returns HTTP 400/500 (expected without proper MCP request)
- [ ] `claude mcp add --transport http` succeeds
- [ ] `/mcp` shows server and tools
- [ ] Tools are invocable

---

## Summary

**Only change pom.xml in most cases:**
1. Update version to 1.1.2
2. Fix artifact name
3. Remove milestones repo

**If starting fresh, use this reference project as a template!**
