# Java MCP Server POC

A minimal Spring Boot MCP (Model Context Protocol) server exposing demo tools via HTTP/SSE transport.

## Project Structure

```
mcp-server-poc/
├── pom.xml
├── src/main/java/com/example/mcppoc/
│   ├── McpServerPocApplication.java
│   ├── config/
│   │   ├── McpConfig.java
│   │   └── CorsConfig.java
│   └── tools/
│       └── DemoTools.java
└── src/main/resources/
    └── application.yml
```

## Requirements

- Java 17+
- Maven 3.6+

## Build

```bash
mvn clean package -DskipTests
```

## Run

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## MCP Tools Exposed

1. **greet** - Greets a person by name
   - Input: `name` (string)
   - Example: "Hello, Avi! Welcome to the MCP server."

2. **addNumbers** - Adds two numbers together
   - Input: `a` (int), `b` (int)
   - Example: 42 + 17 = 59

3. **getCurrentTime** - Returns the current server time
   - Input: none
   - Example: "Current server time: Tuesday, January 14, 2026 at 11:05:55 PM"

4. **reverseString** - Reverses a text string
   - Input: `text` (string)
   - Example: "Hello World" → "dlroW olleH"

## Connect to Claude Code

### Add the MCP Server

With the server running, execute:

```bash
claude mcp add --transport sse simple-poc http://localhost:8080/sse
```

### Verify Connection

```bash
claude
> /mcp
```

You should see the `simple-poc` server listed with 4 tools.

### Test the Tools

#### Test greet
```
Use simple-poc to greet Avi
```

#### Test addNumbers
```
Use simple-poc to add 42 and 17
```

#### Test getCurrentTime
```
Use simple-poc to get the current time
```

#### Test reverseString
```
Use simple-poc to reverse the string 'Hello World'
```

## Technical Details

- **Framework**: Spring Boot 3.4.1
- **MCP Library**: Spring AI MCP WebFlux 1.0.0-M6
- **Transport**: SSE (Server-Sent Events) over HTTP
- **SSE Endpoint**: `/sse`
- **Message Endpoint**: `/mcp/message`

## Configuration

The MCP server is configured in `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  main:
    web-application-type: reactive
  ai:
    mcp:
      server:
        name: simple-poc
        version: 0.0.1
        type: SYNC
```

## Testing the SSE Endpoint

```bash
curl -v http://localhost:8080/sse
```

Expected response:
- HTTP 200 OK
- Content-Type: text/event-stream
- Initial event: `event:endpoint` with `data:/mcp/message`

## Sources

- [Spring AI MCP Server Boot Starter](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html)
- [STDIO and SSE MCP Servers - Spring AI](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-stdio-sse-server-boot-starter-docs.html)
- [Maven Repository: spring-ai-mcp-server-webflux-spring-boot-starter](https://mvnrepository.com/artifact/org.springframework.ai/spring-ai-mcp-server-webflux-spring-boot-starter/1.0.0-M6)
