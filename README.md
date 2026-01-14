# Java MCP Server POC

A minimal Spring Boot MCP (Model Context Protocol) server that works with Claude Code.

**Tech Stack:** Spring Boot 3.4.1 + Spring AI 1.1.2 (WebMVC/Tomcat)

## Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Connect to Claude Code
claude mcp add --transport sse simple-poc http://localhost:8080/sse
```

## Demo Tools

1. **greet** - Greets a person by name
2. **addNumbers** - Adds two numbers
3. **getCurrentTime** - Returns server time
4. **reverseString** - Reverses text

## Project Structure

```
src/main/java/com/example/mcppoc/
├── McpServerPocApplication.java    # Main app
├── tools/DemoTools.java            # 4 demo tools with @Tool
└── config/
    ├── McpConfig.java              # Tool registration
    └── CorsConfig.java             # CORS setup

src/main/resources/
└── application.yml                  # Server config
```

## Configuration

**pom.xml:**
```xml
<spring-ai.version>1.1.2</spring-ai.version>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
</dependency>
```

**application.yml:**
```yaml
server:
  port: 8080
spring:
  main:
    web-application-type: servlet  # WebMVC (Tomcat)
  ai:
    mcp:
      server:
        name: simple-poc
        version: 0.0.1
        type: SYNC
```

## WebMVC vs WebFlux

This project uses **WebMVC** (servlet/Tomcat). Both work with Claude Code:

| | WebMVC | WebFlux |
|---|---|---|
| **Artifact** | `spring-ai-starter-mcp-server-webmvc` | `spring-ai-starter-mcp-server-webflux` |
| **Server** | Tomcat (servlet) | Netty (reactive) |
| **Type** | `servlet` | `reactive` |
| **CORS** | `WebMvcConfigurer` | `CorsWebFilter` |
| **Use When** | Traditional apps, simpler | High concurrency, reactive |

To switch: Change artifact, `web-application-type`, and CORS config.

## Migrating Old Servers?

See **[MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)** - comprehensive guide for upgrading broken MCP servers to Spring AI 1.1.2.

## Key Points

- ✅ Uses latest stable Spring AI (1.1.2)
- ✅ Available in Maven Central (no special repos)
- ✅ Tested and working with Claude Code
- ✅ WebMVC implementation (WebFlux also works)
- ✅ Complete reference code included

## Version Notes

**Artifact naming changed in 1.1.x:**
- Old (1.0.0-M6): `spring-ai-mcp-server-webmvc-spring-boot-starter`
- New (1.1.2): `spring-ai-starter-mcp-server-webmvc`

If upgrading, just update artifact name and version - no code changes needed!

## Resources

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
- [Spring AI 1.1 Release](https://spring.io/blog/2025/11/12/spring-ai-1-1-GA-released/)
- [Model Context Protocol](https://modelcontextprotocol.io/)
